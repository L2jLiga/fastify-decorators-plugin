// Copyright 2019-2021 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.ecmascript6.psi.ES6ExportDefaultAssignment
import com.intellij.lang.javascript.psi.JSElementVisitor
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.ecma6.TypeScriptSingleType
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeListOwner
import com.intellij.lang.typescript.tsconfig.TypeScriptConfigUtil
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import fastify_decorators.plugin.SERVICE_DECORATOR_NAME
import fastify_decorators.plugin.extensions.hasDecorator
import fastify_decorators.plugin.extensions.isFastifyDecoratorsContext
import fastify_decorators.plugin.inspections.quickfixes.EmitDecoratorMetadataQuickFix

class EmitDecoratorMetadataInspection : ArgumentsInspectionBase() {
    override fun getStaticDescription() =
        """
            Dependency Injection can not work without emitting decorators metadata.
        """.trimIndent()

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JSElementVisitor() {
            override fun visitTypeScriptSingleType(singleType: TypeScriptSingleType) {
                if (outOfScope(singleType)) return

                val tsConfig =
                    TypeScriptConfigUtil.getConfigForFile(singleType.project, singleType.containingFile.virtualFile)
                        ?: return

                if (tsConfig.getRawCompilerOption("emitDecoratorMetadata") == "true") return

                holder.registerProblem(
                    singleType.parent.parent.parent,
                    "Dependency injection requires \"emitDecoratorMetadata\" option enabled",
                    EmitDecoratorMetadataQuickFix(singleType, tsConfig)
                )
            }

            override fun visitES6ExportDefaultAssignment(node: ES6ExportDefaultAssignment) {
                visitJSAttributeOwner(node)
            }

            override fun visitTypeScriptClass(clazz: TypeScriptClass) {
                visitJSAttributeOwner(clazz)
            }

            private fun visitJSAttributeOwner(clazz: JSAttributeListOwner) {
                if (!clazz.isFastifyDecoratorsContext) return
                if (!clazz.hasDecorator(SERVICE_DECORATOR_NAME)) return

                val tsConfig =
                    TypeScriptConfigUtil.getConfigForFile(clazz.project, clazz.containingFile.virtualFile) ?: return

                if (tsConfig.getRawCompilerOption("emitDecoratorMetadata") == "true") return

                val textRange = calculateTextRange(clazz)
                holder.registerProblem(
                    clazz,
                    textRange,
                    "Dependency injection requires \"emitDecoratorMetadata\" option enabled",
                    EmitDecoratorMetadataQuickFix(clazz, tsConfig)
                )
            }

            private fun calculateTextRange(clazz: JSAttributeListOwner): TextRange {
                return clazz.attributeList?.decorators
                    ?.find { it.text.startsWith("@$SERVICE_DECORATOR_NAME") }
                    ?.textRangeInParent
                    ?: throw IllegalStateException("@$SERVICE_DECORATOR_NAME decorator disappeared")
            }
        }
    }
}