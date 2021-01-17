// Copyright 2019-2020 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.javascript.psi.JSElementVisitor
import com.intellij.lang.javascript.psi.ecma6.ES6Decorator
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeList
import com.intellij.psi.PsiElementVisitor
import fastify_decorators.plugin.extensions.isStatic
import fastify_decorators.plugin.inspections.quickfixes.ReplaceInjectWithGetByTokenQuickFix

class InjectUsageOnStaticInspection : ArgumentsInspectionBase() {
    override fun getStaticDescription() =
        """
            @Inject initializing static properties when instantiating class which may cause error in Runtime.
        """.trimIndent()

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JSElementVisitor() {
            override fun visitES6Decorator(decorator: ES6Decorator) {
                if (outOfScope(decorator)) return

                val decoratorArgs = decorator.parent as JSAttributeList
                if (!decoratorArgs.isStatic) return

                holder.registerProblem(
                    decorator,
                    "Using @Inject with static properties not recommended.",
                    ReplaceInjectWithGetByTokenQuickFix(decorator)
                )
            }
        }
    }
}