// Copyright 2019-2021 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.intentions.di

import com.intellij.lang.ecmascript6.psi.impl.ES6FieldStatementImpl
import com.intellij.lang.javascript.DialectDetector
import com.intellij.lang.javascript.intentions.JavaScriptIntention
import com.intellij.lang.javascript.psi.JSArgumentList
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.lang.javascript.psi.ecma6.ES6Decorator
import com.intellij.lang.javascript.psi.ecma6.TypeScriptField
import com.intellij.lang.javascript.psi.ecma6.TypeScriptFunction
import com.intellij.lang.javascript.psi.ecma6.TypeScriptType
import com.intellij.lang.javascript.psi.ecma6.TypeScriptTypeArgumentList
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeList
import com.intellij.lang.javascript.psi.impl.JSChangeUtil
import com.intellij.lang.typescript.intentions.TypeScriptAddImportStatementFix
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import fastify_decorators.plugin.GET_BY_TOKEN
import fastify_decorators.plugin.INJECT_DECORATOR_NAME
import fastify_decorators.plugin.extensions.findInstance
import fastify_decorators.plugin.extensions.replaceAndReformat

class GetByTokenToInject : JavaScriptIntention(), TokenProvider {
    override fun getText() = "Replace \"$GET_BY_TOKEN\" with \"@$INJECT_DECORATOR_NAME\""
    override fun getFamilyName() = "\"$GET_BY_TOKEN\" to \"@$INJECT_DECORATOR_NAME\""
    override fun startInWriteAction() = true

    override fun isAvailable(project: Project, editor: Editor, element: PsiElement): Boolean {
        val tsField = findField(element) ?: return false
        val isStatic = tsField.attributeList?.hasModifier(JSAttributeList.ModifierType.STATIC) ?: false
        if (isStatic) return false

        val reference = tsField.initializer?.firstChild ?: return false
        if (reference !is JSReferenceExpression) return false

        val referenced = reference.resolve() ?: return false
        if (referenced !is TypeScriptFunction) return false

        return referenced.name == GET_BY_TOKEN
    }

    override fun invoke(project: Project, editor: Editor, element: PsiElement) {
        val tsField = findField(element) ?: return

        val token = getTokenFrom(tsField) ?: return
        val type = getTypeFrom(tsField) ?: "any"

        val field = tsField.parent
        if (field !is ES6FieldStatementImpl) return

        val replacement = createReplacementForField(project, tsField, type, token)
        val decorator = when (val attributes = field.attributeList) {
            is JSAttributeList -> attributes.addBefore(
                replacement.attributeList!!.decorators[0],
                attributes.firstChild
            ) as ES6Decorator
            else -> (field.addBefore(replacement.attributeList!!, field.firstChild) as JSAttributeList).decorators[0]
        }
        field.attributeList!!.addAfter(JSChangeUtil.createNewLine(field.attributeList!!), decorator)

        TypeScriptAddImportStatementFix(INJECT_DECORATOR_NAME, decorator.containingFile).applyFix()
        tsField.replaceAndReformat(replacement.children.findInstance<TypeScriptField>()!!)
    }

    private fun getTypeFrom(fieldStatement: TypeScriptField): String? {
        val fieldType = (fieldStatement.typeElement as? TypeScriptType?)?.text
        if (fieldType != null) return fieldType

        val argumentList = fieldStatement.initializer!!.children.findInstance<TypeScriptTypeArgumentList>() ?: return null

        return argumentList.typeArguments.firstOrNull()?.text
    }

    private fun getTokenFrom(fieldStatement: TypeScriptField): String? {
        val argumentsList: JSArgumentList = fieldStatement.initializer!!.children
            .findInstance<JSArgumentList>() ?: return null

        return argumentsList.arguments.firstOrNull()?.text
    }

    override fun getExpression(element: ES6FieldStatementImpl): JSReferenceExpression? {
        val tsField = findField(element) ?: return null
        val argumentsList: JSArgumentList = tsField.initializer!!.children
            .findInstance<JSArgumentList>() ?: return null

        return argumentsList.arguments.first() as JSReferenceExpression
    }

    override fun getNameBy(element: ES6FieldStatementImpl) = findField(element)?.name
}

private fun createReplacementForField(
    project: Project,
    field: TypeScriptField,
    type: String,
    token: String
): ES6FieldStatementImpl =
    JSChangeUtil.createClassMemberFromText(
        project,
        "@Inject($token) ${field.name}: $type",
        DialectDetector.languageDialectOfElement(field)
    ).psi as ES6FieldStatementImpl

private tailrec fun findField(element: PsiElement?): TypeScriptField? = when (element) {
    null -> null
    is TypeScriptField -> element
    is ES6FieldStatementImpl -> element.children.findInstance<TypeScriptField>()
    else -> findField(element.parent)
}