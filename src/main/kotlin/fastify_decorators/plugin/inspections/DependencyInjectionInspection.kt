// Copyright 2019-2020 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.ecmascript6.psi.ES6ImportedBinding
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSElementVisitor
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.lang.javascript.psi.ecma6.ES6Decorator
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.ecma6.TypeScriptSingleType
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeListOwner
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import fastify_decorators.plugin.SERVICE_DECORATOR_NAME
import fastify_decorators.plugin.hasDecoratorApplied
import fastify_decorators.plugin.inspections.quickfixes.AnnotateWithServiceDecoratorQuickFix

class DependencyInjectionInspection : ArgumentsInspectionBase() {
    override fun getStaticDescription(): String {
        return """
               Dependency Injection usage validation.
               Each injected class should have @Service decorator otherwise it will throw Error in Runtime.
               """.trimIndent()
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JSElementVisitor() {
            override fun visitES6Decorator(decorator: ES6Decorator) {
                if (outOfScope(decorator)) return

                val injectDecratorArgs = decorator.children.first().children.last().children
                if (injectDecratorArgs.size < 3) return

                val element = (injectDecratorArgs[1] as JSReferenceExpression).resolve() ?: return
                inspectInjectableElement(element, decorator)
            }

            override fun visitTypeScriptSingleType(singleType: TypeScriptSingleType) {
                if (outOfScope(singleType)) return

                val element = (singleType.firstChild as JSReferenceExpression).resolve() ?: return
                inspectInjectableElement(element, singleType)
            }

            private fun inspectInjectableElement(element: PsiElement, decorator: PsiElement) {
                val jsAttributeListOwner = findAttributeListOwner(element)

                if (jsAttributeListOwner != null) {
                    provideQuickFix(jsAttributeListOwner, decorator)
                    return
                }

                // TODO: Find available classes which implement interface if possible
                holder.registerProblem(decorator, "Only classes annotated with @Service available for injection")
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

            private fun provideQuickFix(element: JSAttributeListOwner, singleType: PsiElement) {
                if (!hasDecoratorApplied(element, SERVICE_DECORATOR_NAME)) holder.registerProblem(
                    singleType,
                    "Injectable classes must be annotated with @Service decorator",
                    AnnotateWithServiceDecoratorQuickFix(element)
                )
            }

        }
    }
}