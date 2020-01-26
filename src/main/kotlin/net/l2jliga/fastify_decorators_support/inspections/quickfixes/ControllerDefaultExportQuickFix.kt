package net.l2jliga.fastify_decorators_support.inspections.quickfixes

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.impl.JSChangeUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.codeStyle.CodeEditUtil
import com.intellij.psi.impl.source.tree.TreeElement

class ControllerDefaultExportQuickFix(context: PsiElement) : LocalQuickFixAndIntentionActionOnPsiElement(context) {
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
        startElement: PsiElement,
        endElement: PsiElement
    ) {
        if (startElement !is TypeScriptClass) return

        val tsClassNode = startElement.node
        val jsAttribute = tsClassNode.firstChildNode

        // 1. Remove export keyword
        jsAttribute.removeChild(jsAttribute.lastChildNode)
        tsClassNode.removeChild(jsAttribute)

        // 2. Find class keyword
        val children = tsClassNode.getChildren(null)
        val classStatement = children.filter { it is PsiElement }.first { it.text == "class" }

        // 3. Remove extra whitespaces
        children.take(children.indexOf(classStatement))
            .filter { it is PsiWhiteSpace }
            .forEach { tsClassNode.removeChild(it) }

        // 4. Add default export
        val el = JSChangeUtil.createStatementFromText(
            startElement.project,
            "export default ${startElement.name}",
            null,
            false
        )
        if (el != null) {
            el.addChild(jsAttribute, el.firstChildNode)

            CodeEditUtil.setOldIndentation(tsClassNode as TreeElement, 0)
            startElement.parent.node.replaceChild(tsClassNode, el)

            el.replaceChild(el.lastChildNode, tsClassNode)
        }
    }
}