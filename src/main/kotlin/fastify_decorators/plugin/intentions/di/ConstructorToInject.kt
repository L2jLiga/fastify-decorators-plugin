// Copyright 2019-2021 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.intentions.di

import com.intellij.lang.ecmascript6.psi.impl.ES6FieldStatementImpl
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.impl.JSChangeUtil
import com.intellij.lang.typescript.intentions.TypeScriptAddImportStatementFix
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import fastify_decorators.plugin.INJECT_DECORATOR_NAME
import fastify_decorators.plugin.extensions.deleteAndReformat
import fastify_decorators.plugin.extensions.reformat

class ConstructorToInject : ConstructorParameterAction() {
    override fun getText() = "Convert parameter property to \"@$INJECT_DECORATOR_NAME\" class field"
    override fun getFamilyName() = text

    override fun invoke(project: Project, editor: Editor, element: PsiElement) {
        val parameter = findParameter(element) ?: return
        val tsClass = PsiTreeUtil.getParentOfType(parameter, TypeScriptClass::class.java) ?: return

        val modifier = parameter.accessType.name.toLowerCase()
        val typeAndToken = parameter.typeElement?.text ?: return
        val name = parameter.name ?: return

        val esField = JSChangeUtil.createClassMemberPsiFromTextWithContext(
            "@$INJECT_DECORATOR_NAME($typeAndToken)\n$modifier $name: $typeAndToken",
            parameter,
            ES6FieldStatementImpl::class.java
        ) ?: return
        tsClass.addBefore(esField, tsClass.constructor).reformat()
        TypeScriptAddImportStatementFix(INJECT_DECORATOR_NAME, tsClass.containingFile).applyFix()

        parameter.deleteAndReformat()
    }
}