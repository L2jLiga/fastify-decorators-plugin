package net.l2jliga.fastify_decorators_support

import com.intellij.lang.javascript.psi.ecmal4.JSAttributeList
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeListOwner


const val CONTROLLER_DECORATOR_NAME = "Controller"

fun hasDecoratorApplied(element: JSAttributeListOwner, decoratorName: String = CONTROLLER_DECORATOR_NAME): Boolean {
    val jsAttribute = element.attributeList
    if (jsAttribute !is JSAttributeList) return false

    return jsAttribute.decorators.iterator()
        .asSequence()
        .filter { it.decoratorName == decoratorName }
        .count() != 0
}