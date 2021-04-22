// Copyright 2019-2021 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.intentions.di

import com.intellij.lang.ecmascript6.psi.impl.ES6FieldStatementImpl
import com.intellij.lang.javascript.intentions.JavaScriptIntention
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.lang.javascript.psi.ecma6.ES6Decorator
import com.intellij.lang.javascript.psi.ecma6.TypeScriptField
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeListOwner
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import fastify_decorators.plugin.GET_BY_TOKEN
import fastify_decorators.plugin.INJECT_DECORATOR_NAME
import fastify_decorators.plugin.extensions.findInstance
import fastify_decorators.plugin.inspections.quickfixes.ReplaceInjectWithGetByTokenQuickFix

class InjectToGetByToken : JavaScriptIntention(), TokenProvider {
    override fun getText() = "Replace \"@$INJECT_DECORATOR_NAME\" with \"$GET_BY_TOKEN\""
    override fun getFamilyName() = "\"@$INJECT_DECORATOR_NAME\" to \"$GET_BY_TOKEN\""
    override fun startInWriteAction() = true

    override fun isAvailable(
        project: Project,
        editor: Editor,
        element: PsiElement
    ) = getInjectDecorator(element) != null

    override fun invoke(
        project: Project,
        editor: Editor,
        element: PsiElement
    ) {
        val decorator = getInjectDecorator(element) ?: return
        ReplaceInjectWithGetByTokenQuickFix(decorator).invoke(project, editor, decorator.containingFile)
    }

    override fun getExpression(element: ES6FieldStatementImpl): JSReferenceExpression? {
        val jsExpression = getInjectDecorator(element)?.expression ?: return null
        return if (jsExpression is JSCallExpression) jsExpression.arguments.firstOrNull() as? JSReferenceExpression?
        else null
    }

    override fun getNameBy(element: ES6FieldStatementImpl) =
        (getInjectDecorator(element)?.parent?.parent?.children?.findInstance<TypeScriptField>())?.name
}

private tailrec fun getInjectDecorator(element: PsiElement?): ES6Decorator? =
    when (element) {
        null -> null
        is ES6Decorator -> if (element.decoratorName == INJECT_DECORATOR_NAME) element else null
        is JSAttributeListOwner -> element.attributeList?.decorators?.find { it.decoratorName == INJECT_DECORATOR_NAME }
        else -> getInjectDecorator(element.parent)
    }