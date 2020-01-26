package net.l2jliga.fastify_decorators_support

import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeList
import net.l2jliga.fastify_decorators_support.providers.CONTROLLER_DECORATOR_NAME

fun hasDecoratorApplied(element: TypeScriptClass): Boolean {
    val jsAttribute = element.attributeList
    if (jsAttribute !is JSAttributeList) return false

    return jsAttribute.decorators.iterator()
        .asSequence()
        .filter { it.decoratorName == CONTROLLER_DECORATOR_NAME }
        .count() != 0
}