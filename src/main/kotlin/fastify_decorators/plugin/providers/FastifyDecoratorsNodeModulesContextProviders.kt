// Copyright 2019-2020 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.providers

import com.intellij.javascript.nodejs.library.NodeModulesDirectoryManager
import com.intellij.psi.PsiDirectory
import com.intellij.psi.util.CachedValueProvider
import fastify_decorators.plugin.FASTIFY_DECORATORS_PACKAGE
import fastify_decorators.plugin.FastifyDecoratorsContextProvider

class FastifyDecoratorsNodeModulesContextProviders :
    FastifyDecoratorsContextProvider {
    override fun isFastifyDecoratorsContext(psiDir: PsiDirectory): CachedValueProvider.Result<Boolean> {
        val manager = NodeModulesDirectoryManager.getInstance(psiDir.project)
        val dirPath = psiDir.virtualFile.path + "/"
        var result = false
        for (dir in manager.nodeModulesDirectories) {
            val nodeModules = dir.nodeModulesDir
            if (dirPath.startsWith(nodeModules.parent.path + "/")) {
                val child = dir.nodeModulesDir.findFileByRelativePath(FASTIFY_DECORATORS_PACKAGE)
                if (child != null && child.isValid && child.isDirectory) {
                    result = true
                    break
                }
            }
        }
        return CachedValueProvider.Result.create(result, manager.nodeModulesDirChangeTracker)
    }
}
