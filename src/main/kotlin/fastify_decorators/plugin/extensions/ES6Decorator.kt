package fastify_decorators.plugin.extensions

import com.intellij.lang.javascript.psi.ecma6.ES6Decorator
import com.intellij.psi.PsiElement

fun ES6Decorator.getArguments(): PsiElement? {
    val callExpression =
        if (this.children.isNotEmpty()) this.children.first()
        else return null

    return if (callExpression.children.isNotEmpty()) callExpression.children.last() else null
}