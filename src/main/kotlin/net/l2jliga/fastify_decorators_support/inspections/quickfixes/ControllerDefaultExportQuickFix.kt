// Copyright 2019-2020 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package net.l2jliga.fastify_decorators_support.inspections.quickfixes

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.javascript.JSKeywordElementType
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.impl.JSChangeUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.codeStyle.CodeEditUtil
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import com.intellij.psi.impl.source.tree.TreeElement

class ControllerDefaultExportQuickFix(context: PsiElement) : LocalQuickFixAndIntentionActionOnPsiElement(context, context.parent) {
    override fun getFamilyName(): String {
        return "Export class as default"
    }

    override fun getText(): String {
        return "Add 'default' modifier"
    }

    override fun invoke(
        project: Project,
        file: PsiFile,
        editor: Editor?,
        clazz: PsiElement,
        parent: PsiElement
    ) {
        if (clazz !is TypeScriptClass) return

        val tsClassNode = clazz.node
        val attribute = extractAttribute(tsClassNode)

        removeExtraWhiteSpaces(tsClassNode)

        CodeEditUtil.setOldIndentation(attribute as TreeElement, 0)
        CodeEditUtil.setOldIndentation(tsClassNode as TreeElement, 0)

        // 4. Create default export statement
        val defaultExportStatement = createDefaultExportStatement(clazz)

        defaultExportStatement.addChild(attribute, defaultExportStatement.firstChildNode)

        parent.node.replaceChild(tsClassNode, defaultExportStatement)

        defaultExportStatement.replaceChild(defaultExportStatement.lastChildNode, tsClassNode)
    }

    private fun extractAttribute(tsClassNode: ASTNode): ASTNode {
        val attribute = tsClassNode.firstChildNode

        val elementType = attribute.lastChildNode.elementType
        if (elementType is JSKeywordElementType && elementType.keyword == "export") {
            attribute.removeChild(attribute.lastChildNode)
        } else {
            attribute.addChild(PsiWhiteSpaceImpl("\n").node)
        }
        tsClassNode.removeChild(attribute)

        return attribute
    }

    private fun removeExtraWhiteSpaces(classNode: ASTNode) {
        with(classNode.getChildren(null)) {
            val classStatement = this.filter { it is PsiElement }.first { it.text == "class" }

            this.take(this.indexOf(classStatement))
                .filter { it is PsiWhiteSpace }
                .forEach { classNode.removeChild(it) }
        }
    }

    private fun createDefaultExportStatement(clazz: TypeScriptClass): ASTNode {
        return JSChangeUtil.createStatementFromText(
            clazz.project,
            "export default class ${clazz.name} {}",
            null,
            false
        )!!
    }
}