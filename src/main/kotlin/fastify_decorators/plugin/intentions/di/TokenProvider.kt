// Copyright 2019-2020 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.intentions.di

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.ecmascript6.psi.impl.ES6FieldStatementImpl
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement

interface TokenProvider : IntentionAction {
    fun isAvailable(project: Project, editor: Editor, element: PsiElement): Boolean

    fun getExpression(element: ES6FieldStatementImpl): JSReferenceExpression?

    fun getNameBy(element: ES6FieldStatementImpl): String?
}