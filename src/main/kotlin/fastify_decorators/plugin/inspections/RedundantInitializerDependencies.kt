// Copyright 2019-2021 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression
import com.intellij.lang.javascript.psi.JSElementVisitor
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.lang.javascript.psi.ecma6.ES6Decorator
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.ecma6.TypeScriptFunction
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import fastify_decorators.plugin.INITIALIZER_DECORATOR_NAME
import fastify_decorators.plugin.extensions.findInstance
import fastify_decorators.plugin.extensions.getArgumentList
import fastify_decorators.plugin.extensions.isFastifyDecoratorsContext
import fastify_decorators.plugin.inspections.quickfixes.RemoveRedundantInitializerDependency

class RedundantInitializerDependencies : ArgumentsInspectionBase() {
    override fun getStaticDescription() =
        """
            Async initializer dependencies inspection.
            Inspects dependencies defined in @Initializer decorator for redundant.
        """.trimIndent()

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JSElementVisitor() {
            override fun visitES6Decorator(decorator: ES6Decorator) {
                if (!decorator.isFastifyDecoratorsContext) return
                if (decorator.decoratorName != INITIALIZER_DECORATOR_NAME) return

                val dependencies = decorator.getArgumentList()?.arguments
                    ?.findInstance<JSArrayLiteralExpression>()
                    ?.expressions ?: return

                val references = dependencies.filterIsInstance<JSReferenceExpression>().map { Pair(it, it.resolve()) }

                references.forEach { inspectInitializerDependency(it) }
            }

            private fun inspectInitializerDependency(dependency: Pair<JSReferenceExpression, PsiElement?>) {
                val element = dependency.second
                if (element !is TypeScriptClass) return

                val hasInitializer = element.children.any { child ->
                    child is TypeScriptFunction && child.attributeList?.decorators?.any { it.decoratorName == INITIALIZER_DECORATOR_NAME } ?: false
                }

                if (!hasInitializer) {
                    holder.registerProblem(
                        dependency.first,
                        "Dependency does not have an Initializer",
                        RemoveRedundantInitializerDependency(dependency.first)
                    )
                }
            }
        }
    }
}