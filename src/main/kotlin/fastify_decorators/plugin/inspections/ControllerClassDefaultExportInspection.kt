// Copyright 2019-2020 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.javascript.psi.JSElementVisitor
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClassExpression
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeList
import com.intellij.lang.javascript.psi.ecmal4.impl.JSAttributeListImpl
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import fastify_decorators.plugin.hasDecoratorApplied
import fastify_decorators.plugin.inspections.quickfixes.ControllerDefaultExportQuickFix
import fastify_decorators.plugin.isFastifyDecoratorsContext

class ControllerClassDefaultExportInspection : LocalInspectionTool() {
    override fun getStaticDescription(): String {
        return "Controller without default export can not be loaded and will throw Error in Runtime"
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JSElementVisitor() {
            override fun visitTypeScriptClass(typeScriptClass: TypeScriptClass) {
                if (!isFastifyDecoratorsContext(typeScriptClass)) return
                if (typeScriptClass.isExportedWithDefault || !hasDecoratorApplied(
                        typeScriptClass
                    )
                ) return

                val textRange = (typeScriptClass.attributeList as JSAttributeList).decorators
                    .find { it.text.startsWith("@Controller") }
                    ?.textRangeInParent
                    ?: throw IllegalStateException("@Controller decorator disappeared")

                holder.registerProblem(
                    typeScriptClass,
                    textRange,
                    "@Controller must have default export",
                    ControllerDefaultExportQuickFix(
                        typeScriptClass
                    )
                )
            }
        }
    }
}