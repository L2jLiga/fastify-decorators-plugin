package net.l2jliga.fastify_decorators_support.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.javascript.psi.JSElementVisitor
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import net.l2jliga.fastify_decorators_support.hasDecoratorApplied
import net.l2jliga.fastify_decorators_support.inspections.quickfixes.ControllerDefaultExportQuickFix

class ControllerClassDefaultExportInspection : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JSElementVisitor() {
            override fun visitTypeScriptClass(typeScriptClass: TypeScriptClass) {
                val hasNoDefaultExport = typeScriptClass.isExported && !typeScriptClass.isExportedWithDefault

                if (hasNoDefaultExport && hasDecoratorApplied(typeScriptClass)) {
                    val textRange = TextRange.from((typeScriptClass.attributeList?.textLength ?: 6) - 6, 6)
                    holder.registerProblem(
                        typeScriptClass,
                        textRange,
                        "Controller must have default export",
                        ControllerDefaultExportQuickFix(typeScriptClass)
                    )
                }
            }
        }
    }
}