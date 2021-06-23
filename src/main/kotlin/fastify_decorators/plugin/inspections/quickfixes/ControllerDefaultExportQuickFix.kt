// Copyright 2019-2021 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.inspections.quickfixes

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement
import com.intellij.lang.ecmascript6.psi.ES6ExportDefaultAssignment
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeList
import com.intellij.lang.javascript.psi.impl.JSChangeUtil
import com.intellij.lang.javascript.refactoring.FormatFixer
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import fastify_decorators.plugin.extensions.createFromText

class ControllerDefaultExportQuickFix(context: TypeScriptClass) :
    LocalQuickFixAndIntentionActionOnPsiElement(context, context.parent) {
    private val myClassName = context.name!!
    override fun getFamilyName() = "Controller classes should be exported with 'default'"
    override fun getText() = "Export $myClassName with 'default'"

    override fun invoke(
        project: Project,
        file: PsiFile,
        editor: Editor?,
        tsClass: PsiElement,
        parent: PsiElement
    ) {
        if (tsClass !is TypeScriptClass) return

        val attribute = extractAttribute(tsClass)

        val defaultExportStatement = createDefaultExportStatement(tsClass)
        defaultExportStatement.node.addChild(attribute.node, defaultExportStatement.firstChild.node)

        tsClass.replace(defaultExportStatement)
            .lastChild.replace(tsClass)

        val document = PsiDocumentManager.getInstance(project).getDocument(parent.containingFile)
        if (document != null) FormatFixer.create(parent, FormatFixer.Mode.Reformat).fixFormat()
    }

    private fun extractAttribute(tsClass: TypeScriptClass): JSAttributeList {
        val attributeList: JSAttributeList = tsClass.attributeList!!

        attributeList.findModifierElement(JSAttributeList.ModifierType.EXPORT)?.delete()
        attributeList.delete()

        attributeList.add(JSChangeUtil.createNewLine(attributeList))

        return attributeList
    }

    private fun createDefaultExportStatement(tsClass: TypeScriptClass) =
        tsClass.project.createFromText<ES6ExportDefaultAssignment>("export default class ${tsClass.name} {}")
}