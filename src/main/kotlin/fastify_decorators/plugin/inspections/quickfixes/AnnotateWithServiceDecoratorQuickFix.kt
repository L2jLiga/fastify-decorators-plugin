// Copyright 2019-2021 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.inspections.quickfixes

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement
import com.intellij.lang.ecmascript6.psi.ES6ExportDefaultAssignment
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeList
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeListOwner
import com.intellij.lang.javascript.psi.impl.JSChangeUtil
import com.intellij.lang.javascript.refactoring.FormatFixer
import com.intellij.lang.typescript.intentions.TypeScriptAddImportStatementFix
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import fastify_decorators.plugin.SERVICE_DECORATOR_NAME
import fastify_decorators.plugin.extensions.createFromText

class AnnotateWithServiceDecoratorQuickFix(context: JSAttributeListOwner) :
    LocalQuickFixAndIntentionActionOnPsiElement(context, context.parent) {

    private val myInjectableClassName: String = when (context) {
        is TypeScriptClass -> context.name ?: ""
        is ES6ExportDefaultAssignment -> (context.lastChild as TypeScriptClass).name ?: ""
        else -> throw IllegalArgumentException("Invalid argument was provided to quick-fix")
    }

    override fun getFamilyName() = "Injectable classes"
    override fun getText() = "Annotate $myInjectableClassName with @$SERVICE_DECORATOR_NAME"

    override fun invoke(
        project: Project,
        file: PsiFile,
        editor: Editor?,
        attributesListOwner: PsiElement,
        parent: PsiElement
    ) {
        // 1. Check element type
        if (attributesListOwner !is JSAttributeListOwner) return

        // 2. Add service decorator
        val attributeList = createServiceDecorator(project)
        when (val attrs = attributesListOwner.attributeList) {
            is JSAttributeList -> attrs.addBefore(attributeList.decorators[0], attrs.firstChild)
            null -> attributesListOwner.addBefore(attributeList, attributesListOwner.firstChild)
        }

        // 3. Add import if missing
        TypeScriptAddImportStatementFix(SERVICE_DECORATOR_NAME, attributesListOwner.containingFile).applyFix()

        // 4. Reformat changes
        val document = PsiDocumentManager.getInstance(project).getDocument(parent.containingFile)
        if (document != null) FormatFixer.create(parent, FormatFixer.Mode.Reformat).fixFormat()
    }

    private fun createServiceDecorator(project: Project): JSAttributeList {
        val attributeList = project.createFromText<JSAttributeListOwner>("@Service() class A {}").attributeList!!

        JSChangeUtil.createNewLine(attributeList)

        return attributeList
    }
}