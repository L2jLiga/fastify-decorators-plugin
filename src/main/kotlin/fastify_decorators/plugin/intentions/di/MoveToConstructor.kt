// Copyright 2019-2021 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.intentions.di

import com.intellij.lang.ecmascript6.psi.impl.ES6FieldStatementImpl
import com.intellij.lang.javascript.intentions.JavaScriptIntention
import com.intellij.lang.javascript.psi.JSFunction
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.ecma6.impl.TypeScriptParameterListImpl
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeList
import com.intellij.lang.javascript.psi.impl.JSChangeUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import fastify_decorators.plugin.GET_BY_TOKEN
import fastify_decorators.plugin.INJECT_DECORATOR_NAME
import fastify_decorators.plugin.extensions.findInstance
import fastify_decorators.plugin.extensions.replaceAndReformat

class MoveToConstructor : JavaScriptIntention() {
    private val availabilityProviders: List<TokenProvider> = listOf(GetByTokenToInject(), InjectToGetByToken())

    override fun getText() = "Move to constructor"
    override fun getFamilyName() = "Move \"$GET_BY_TOKEN\" or \"@$INJECT_DECORATOR_NAME\" to constructor field"
    override fun startInWriteAction() = true

    override fun isAvailable(project: Project, editor: Editor, element: PsiElement): Boolean {
        val provider = availabilityProviders
            .firstOrNull { it.isAvailable(project, editor, element) }
            ?: return false

        val esField = getES6Field(element) ?: return false

        val expr = provider.getExpression(esField)

        return when (expr?.resolve()) {
            is TypeScriptClass -> true
            else -> false
        }
    }

    override fun invoke(project: Project, editor: Editor, element: PsiElement) {
        val provider = availabilityProviders
            .firstOrNull { it.isAvailable(project, editor, element) }
            ?: return

        val esField = getES6Field(element) ?: return

        val name = provider.getNameBy(esField) ?: return
        val token = provider.getExpression(esField)?.text ?: return
        val modifier = when (esField.attributeList?.accessType) {
            JSAttributeList.AccessType.PRIVATE -> "private"
            JSAttributeList.AccessType.PROTECTED -> "protected"
            else -> "public"
        }

        val defaultConstructor = JSChangeUtil.createClassMemberPsiFromTextWithContext(
            "constructor($modifier $name: $token) {}",
            esField,
            JSFunction::class.java
        )!!
        val constructor = (esField.parent as TypeScriptClass).constructor
        if (constructor == null) {
            esField.replaceAndReformat(defaultConstructor)
            return
        }

        val parametersList = constructor.children.findInstance<TypeScriptParameterListImpl>()!!

        val parameters = parametersList.children.joinToString { it.text }
        parametersList.replaceAndReformat(
            JSChangeUtil.createClassMemberPsiFromTextWithContext(
                "constructor($parameters ${if (parameters.isEmpty()) "" else ","} $modifier $name: $token) {}",
                constructor,
                JSFunction::class.java
            )!!.parameterList!!
        )

        esField.delete()
    }
}

private tailrec fun getES6Field(element: PsiElement?): ES6FieldStatementImpl? =
    when (element) {
        null -> null
        is ES6FieldStatementImpl -> element
        else -> getES6Field(element.parent)
    }