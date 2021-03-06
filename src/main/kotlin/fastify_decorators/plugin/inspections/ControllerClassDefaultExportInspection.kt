// Copyright 2019-2021 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.javascript.psi.JSElementVisitor
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeList
import com.intellij.psi.PsiElementVisitor
import fastify_decorators.plugin.CONTROLLER_DECORATOR_NAME
import fastify_decorators.plugin.extensions.hasDecorator
import fastify_decorators.plugin.extensions.isFastifyDecoratorsContext
import fastify_decorators.plugin.inspections.quickfixes.ControllerDefaultExportQuickFix

class ControllerClassDefaultExportInspection : LocalInspectionTool() {
    override fun getStaticDescription() =
        """
            Applicable only when application using autoloader from fastify-decorators library.
            Controller without default export can not be loaded and will throw Error in Runtime.
        """.trimIndent()

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JSElementVisitor() {
            override fun visitTypeScriptClass(typeScriptClass: TypeScriptClass) {
                if (!typeScriptClass.isFastifyDecoratorsContext) return
                if (typeScriptClass.isExportedWithDefault || !typeScriptClass.hasDecorator()) return

                val textRange = (typeScriptClass.attributeList as JSAttributeList).decorators
                    .find { it.text.startsWith("@$CONTROLLER_DECORATOR_NAME") }
                    ?.textRangeInParent
                    ?: throw IllegalStateException("@$CONTROLLER_DECORATOR_NAME decorator disappeared")

                holder.registerProblem(
                    typeScriptClass,
                    textRange,
                    "@$CONTROLLER_DECORATOR_NAME must have default export",
                    ControllerDefaultExportQuickFix(
                        typeScriptClass
                    )
                )
            }
        }
    }
}