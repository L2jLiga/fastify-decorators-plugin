// Copyright 2019-2020 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.extensions

import com.intellij.lang.javascript.psi.ecma6.ES6Decorator
import com.intellij.psi.PsiElement

fun ES6Decorator.getArguments(): PsiElement? {
    val callExpression =
        if (this.children.isNotEmpty()) this.children.first()
        else return null

    return if (callExpression.children.isNotEmpty()) callExpression.children.last() else null
}