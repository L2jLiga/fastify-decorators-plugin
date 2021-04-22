// Copyright 2019-2021 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.extensions

import com.intellij.lang.javascript.psi.JSArgumentList
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.ecma6.ES6Decorator

fun ES6Decorator.getArgumentList(): JSArgumentList? {
    val callExpression = this.children.findInstance<JSCallExpression>() ?: return null

    return callExpression.children.findInstance<JSArgumentList>()
}