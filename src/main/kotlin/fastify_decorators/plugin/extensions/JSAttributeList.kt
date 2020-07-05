package fastify_decorators.plugin.extensions

import com.intellij.lang.javascript.psi.ecmal4.JSAttributeList
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeList.ModifierType.STATIC

val JSAttributeList.isStatic: Boolean
    get() = this.hasModifier(STATIC)
