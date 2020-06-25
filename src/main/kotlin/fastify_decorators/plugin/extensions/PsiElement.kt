// Copyright 2019-2020 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.extensions

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.lang.javascript.refactoring.FormatFixer
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ex.ProjectRootManagerEx
import com.intellij.openapi.util.EmptyRunnable
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.util.ObjectUtils
import com.intellij.util.containers.ContainerUtil
import fastify_decorators.plugin.FASTIFY_DECORATORS_CONTEXT_PROVIDER_EP
import java.util.*

private val FASTIFY_DECORATORS_CONTEXT_CACHE_KEY = Key<CachedValue<Boolean>>("fastify_decorators.isContext.cache")
private val FASTIFY_DECORATORS_PREV_CONTEXT_CACHE_KEY = Key<Boolean>("fastify_decorators.isContext.prev")
private val FASTIFY_DECORATORS_CONTEXT_RELOAD_MARKER_KEY = Key<Any>("fastify_decorators.isContext.reloadMarker")

private val reloadMonitor = Any()

fun PsiElement.replaceAndReformat(replacement: PsiElement) {
    val parent = this.parent
    val document = PsiDocumentManager.getInstance(parent.project).getDocument(parent.containingFile)

    this.replace(replacement)
    if (document != null) FormatFixer.create(parent, FormatFixer.Mode.Reformat).fixFormat()
}

val PsiElement.isFastifyDecoratorsContext: Boolean
    get() {
        if (!this.isValid) return false
        val psiFile = InjectedLanguageManager.getInstance(this.project).getTopLevelFile(this) ?: return false
        val file = psiFile.originalFile.virtualFile ?: return false

        return isFastifyDecoratorsContext(psiFile.project, file)
    }

private fun isFastifyDecoratorsContext(project: Project, virtualFile: VirtualFile): Boolean {
    val context = virtualFile.getContext()
    val psiDir = ObjectUtils.doIfNotNull(
        context.parent,
        { if (it.isValid) PsiManager.getInstance(project).findDirectory(it) else null }) ?: return false

    val currentState = CachedValuesManager.getCachedValue(psiDir, FASTIFY_DECORATORS_CONTEXT_CACHE_KEY) {
        val dependencies: MutableSet<Any> = HashSet()

        for (provider in FASTIFY_DECORATORS_CONTEXT_PROVIDER_EP.extensionList) {
            val result: CachedValueProvider.Result<Boolean> = provider.isFastifyDecoratorsContext(psiDir)
            if (result.value) return@getCachedValue result

            ContainerUtil.addAll<Any, MutableCollection<Any>>(
                dependencies,
                *result.dependencyItems
            )
        }

        CachedValueProvider.Result(false, *dependencies.toTypedArray())
    }

    checkContextChange(psiDir, currentState)
    return currentState
}


private fun checkContextChange(psiDir: PsiDirectory, currentState: Boolean) {
    val prevState = psiDir.getUserData(FASTIFY_DECORATORS_PREV_CONTEXT_CACHE_KEY)
    if (prevState != null && prevState != currentState) {
        reloadProject(psiDir.project)
    }
    psiDir.putUserData(FASTIFY_DECORATORS_PREV_CONTEXT_CACHE_KEY, currentState)
}

private fun reloadProject(project: Project) {
    synchronized(reloadMonitor) {
        if (project.getUserData(FASTIFY_DECORATORS_CONTEXT_RELOAD_MARKER_KEY) != null) {
            return
        }
        project.putUserData(FASTIFY_DECORATORS_CONTEXT_RELOAD_MARKER_KEY, Any())
    }

    ApplicationManager.getApplication().invokeLater(Runnable {
        WriteAction.run<RuntimeException> {
            ProjectRootManagerEx.getInstanceEx(project)
                .makeRootsChange(EmptyRunnable.getInstance(), false, true)
            project.putUserData(FASTIFY_DECORATORS_CONTEXT_RELOAD_MARKER_KEY, null)
        }
    }, project.disposed)
}