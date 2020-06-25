package fastify_decorators.plugin.extensions

import com.intellij.lang.javascript.psi.ecmal4.JSAttributeList

val JSAttributeList.isStatic: Boolean
    get() = this.hasModifier(JSAttributeList.ModifierType.STATIC)