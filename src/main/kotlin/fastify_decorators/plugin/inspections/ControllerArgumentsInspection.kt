// Copyright 2019-2020 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.javascript.psi.JSElementVisitor
import com.intellij.lang.javascript.psi.JSParameter
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.ecma6.TypeScriptFunction
import com.intellij.lang.javascript.psi.ecma6.TypeScriptSingleType
import com.intellij.psi.PsiElementVisitor
import fastify_decorators.plugin.SERVICE_DECORATOR_NAME
import fastify_decorators.plugin.hasDecoratorApplied
import fastify_decorators.plugin.inspections.quickfixes.AnnotateWithServiceDecoratorQuickFix
import fastify_decorators.plugin.isFastifyDecoratorsContext

class ControllerArgumentsInspection : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JSElementVisitor() {
            override fun visitTypeScriptSingleType(singleType: TypeScriptSingleType) {
                if (!isFastifyDecoratorsContext(singleType)) return
                if (singleType.parent !is JSParameter) return
                if (isRegularMethod(singleType)) return

                val element = (singleType.firstChild as JSReferenceExpression).resolve() ?: return

                if (element is TypeScriptClass) {
                    if (!hasDecoratorApplied(
                            element,
                            SERVICE_DECORATOR_NAME
                        )
                    ) {
                        holder.registerProblem(
                            singleType,
                            "Injectable classes must be annotated with @Service decorator",
                            AnnotateWithServiceDecoratorQuickFix(
                                element
                            )
                        )
                    }

                    return
                }

                // TODO: Find available classes which implement interface if possible
                holder.registerProblem(singleType, "Only classes annotated with @Service available for injection")
            }

            private fun isRegularMethod(singleType: TypeScriptSingleType): Boolean {
                val mayBeConstructor = singleType.parent.parent.parent
                if (mayBeConstructor !is TypeScriptFunction) return true
                return !mayBeConstructor.isConstructor
            }
        }
    }
}