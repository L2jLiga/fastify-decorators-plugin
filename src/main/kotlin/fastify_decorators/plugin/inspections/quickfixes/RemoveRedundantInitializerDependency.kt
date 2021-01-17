// Copyright 2019-2020 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.inspections.quickfixes

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.lang.javascript.refactoring.FormatFixer
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LeafPsiElement

class RemoveRedundantInitializerDependency(context: JSReferenceExpression) :
    LocalQuickFixAndIntentionActionOnPsiElement(context, context.parent) {
    override fun getFamilyName() = "Service initializers"
    override fun getText() = "Remove redundant dependency"

    override fun invoke(
        project: Project,
        file: PsiFile,
        editor: Editor?,
        dependency: PsiElement,
        parent: PsiElement
    ) {
        // 1. Remove extra comma if required
        (
            findComma(dependency, next = { it?.prevSibling }) ?: findComma(
                dependency,
                next = { it?.nextSibling }
            )
            )?.delete()

        // 2. Remove redundant dependency itself
        dependency.delete()

        // 3. Reformat changes
        val document = PsiDocumentManager.getInstance(project).getDocument(parent.containingFile)
        if (document != null) FormatFixer.create(parent, FormatFixer.Mode.Reformat).fixFormat()
    }

    private fun findComma(element: PsiElement, next: (PsiElement?) -> PsiElement?): PsiElement? {
        var maybeComma = next(element)
        while (maybeComma is PsiWhiteSpace) maybeComma = next(maybeComma)

        if (maybeComma is LeafPsiElement && maybeComma.text == ",") return maybeComma
        return null
    }
}