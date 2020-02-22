// Copyright 2019-2020 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.ecmascript6.psi.ES6ImportedBinding
import com.intellij.lang.javascript.psi.JSElementVisitor
import com.intellij.lang.javascript.psi.JSParameter
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.ecma6.TypeScriptFunction
import com.intellij.lang.javascript.psi.ecma6.TypeScriptSingleType
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeListOwner
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import fastify_decorators.plugin.SERVICE_DECORATOR_NAME
import fastify_decorators.plugin.hasDecoratorApplied
import fastify_decorators.plugin.inspections.quickfixes.AnnotateWithServiceDecoratorQuickFix
import fastify_decorators.plugin.isFastifyDecoratorsContext

class ControllerArgumentsInspection : LocalInspectionTool() {
    override fun getStaticDescription(): String {
        return "Controller constructor arguments without @Service decorator can not be injected and will throw Error in Runtime"
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JSElementVisitor() {
            override fun visitTypeScriptSingleType(singleType: TypeScriptSingleType) {
                if (!isFastifyDecoratorsContext(singleType)) return
                if (singleType.parent !is JSParameter) return
                if (withinRegularMethod(singleType)) return

                val element = (singleType.firstChild as JSReferenceExpression).resolve() ?: return
                val jsAttributeListOwner = findAttributeListOwner(element)

                if (jsAttributeListOwner != null) {
                    provideQuickFix(jsAttributeListOwner, singleType)
                    return
                }

                // TODO: Find available classes which implement interface if possible
                holder.registerProblem(singleType, "Only classes annotated with @Service available for injection")
            }

            private fun findAttributeListOwner(element: PsiElement): JSAttributeListOwner? {
                return when (element) {
                    is TypeScriptClass -> element
                    is ES6ImportedBinding -> findAcceptableReference(element)
                    else -> null
                }
            }

            @Suppress("UNCHECKED_CAST")
            private fun findAcceptableReference(element: ES6ImportedBinding): JSAttributeListOwner? {
                return (element.findReferencedElements().asSequence()
                    .filter { it is JSAttributeListOwner } as Sequence<JSAttributeListOwner>)
                    .firstOrNull { it.lastChild is TypeScriptClass }
            }

            private fun provideQuickFix(
                element: JSAttributeListOwner,
                singleType: TypeScriptSingleType
            ) {
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
            }

            private fun withinRegularMethod(singleType: TypeScriptSingleType): Boolean {
                val mayBeConstructor = singleType.parent.parent.parent
                if (mayBeConstructor !is TypeScriptFunction) return true
                return !mayBeConstructor.isConstructor
            }
        }
    }
}