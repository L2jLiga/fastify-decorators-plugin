// Copyright 2019-2020 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package net.l2jliga.fastify_decorators_support.inspections.quickfixes

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement
import com.intellij.lang.javascript.psi.ecma6.ES6Decorator
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeListOwner
import com.intellij.lang.javascript.psi.impl.JSChangeUtil.createStatementFromText
import com.intellij.lang.typescript.intentions.TypeScriptAddImportStatementFix
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import net.l2jliga.fastify_decorators_support.SERVICE_DECORATOR_NAME

class AnnotateWithServiceDecoratorQuickFix(context: TypeScriptClass) :
    LocalQuickFixAndIntentionActionOnPsiElement(context, context.parent) {
    private val myInjectableClassName: String = context.name!!

    override fun getFamilyName() = "Injectable classes"
    override fun getText() = "Annotate $myInjectableClassName with @Service"

    override fun invoke(
        project: Project,
        file: PsiFile,
        editor: Editor?,
        clazz: PsiElement,
        parent: PsiElement
    ) {
        if (clazz !is TypeScriptClass) return
        val attributeList = clazz.attributeList ?: return

        val decorator = createServiceDecorator(project)
        attributeList.addBefore(decorator, attributeList.firstChild)

        TypeScriptAddImportStatementFix(SERVICE_DECORATOR_NAME, file).applyFix()
    }

    private fun createServiceDecorator(project: Project): ES6Decorator {
        return (createStatementFromText(
            project,
            "@Service() class A {}",
            null,
            false
        )!!.psi as JSAttributeListOwner).attributeList!!.decorators[0]
    }
}