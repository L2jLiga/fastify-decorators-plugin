// Copyright 2019-2020 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.inspections.quickfixes

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement
import com.intellij.lang.ecmascript6.psi.ES6ExportDefaultAssignment
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeList
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeListOwner
import com.intellij.lang.typescript.intentions.TypeScriptAddImportStatementFix
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import fastify_decorators.plugin.SERVICE_DECORATOR_NAME
import fastify_decorators.plugin.createStatementFromText

class AnnotateWithServiceDecoratorQuickFix(context: JSAttributeListOwner) :
    LocalQuickFixAndIntentionActionOnPsiElement(context, context.parent) {

    private val myInjectableClassName: String = when (context) {
        is TypeScriptClass -> context.name!!
        is ES6ExportDefaultAssignment -> (context.lastChild as TypeScriptClass).name!!
        else -> throw IllegalArgumentException("Invalid argument was provided to quick-fix")
    }

    override fun getFamilyName() = "Injectable classes"
    override fun getText() = "Annotate $myInjectableClassName with @Service"

    override fun invoke(
        project: Project,
        file: PsiFile,
        editor: Editor?,
        clazz: PsiElement,
        parent2: PsiElement
    ) {
        if (clazz !is JSAttributeListOwner) return

        val attributeList = createServiceDecorator(project)
        when (val attrs = clazz.attributeList) {
            is JSAttributeList -> attrs.addBefore(attributeList.decorators[0], attrs.firstChild)
            null -> clazz.addBefore(attributeList, clazz.firstChild)
        }

        TypeScriptAddImportStatementFix(SERVICE_DECORATOR_NAME, clazz.containingFile).applyFix()
    }

    private fun createServiceDecorator(project: Project): JSAttributeList {
        val attributeList =
            (createStatementFromText(project, "@Service() class A {}").psi as JSAttributeListOwner).attributeList!!

        attributeList.node.addChild(PsiWhiteSpaceImpl("\n"))

        return attributeList
    }
}