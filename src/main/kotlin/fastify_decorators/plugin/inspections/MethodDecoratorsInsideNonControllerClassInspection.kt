// Copyright 2019-2021 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.ecmascript6.psi.ES6ExportDefaultAssignment
import com.intellij.lang.javascript.psi.JSElementVisitor
import com.intellij.lang.javascript.psi.JSFunction
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClassExpression
import com.intellij.lang.javascript.psi.ecma6.TypeScriptFunction
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeListOwner
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import fastify_decorators.plugin.extensions.hasDecorator
import fastify_decorators.plugin.extensions.isFastifyDecoratorsContext

private val METHODS_DECORATORS = arrayOf("GET", "POST", "HEAD", "OPTIONS", "PUT", "PATCH", "DELETE")

class MethodDecoratorsInsideNonControllerClassInspection : LocalInspectionTool() {
    override fun getStaticDescription() =
        """
            Methods decorated with Http method or Fastify hook decorator will be bootstrapped only if class decorated with controller itself.
        """.trimIndent()

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JSElementVisitor() {
            override fun visitJSFunctionDeclaration(function: JSFunction) {
                if (!function.isFastifyDecoratorsContext) return
                if (function !is TypeScriptFunction) return

                val attributesOwner = getAttributesOwner(function.parent) ?: return

                if (!function.hasDecorator(*METHODS_DECORATORS)) return
                if (attributesOwner.hasDecorator()) return

                holder.registerProblem(function, "Method will not be bootstrapped on server start")
            }
        }
    }

    private fun getAttributesOwner(element: PsiElement): JSAttributeListOwner? {
        return when (element) {
            is TypeScriptClassExpression -> extractFromExpression(element)
            is TypeScriptClass -> element
            else -> null
        }
    }

    private fun extractFromExpression(element: TypeScriptClassExpression): ES6ExportDefaultAssignment? {
        val parent = element.parent
        return if (parent is ES6ExportDefaultAssignment) parent else null
    }
}