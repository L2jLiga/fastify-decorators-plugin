// Copyright 2019-2020 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package net.l2jliga.fastify_decorators_support.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.javascript.psi.JSElementVisitor
import com.intellij.lang.javascript.psi.JSParameter
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.ecma6.TypeScriptFunction
import com.intellij.lang.javascript.psi.ecma6.TypeScriptSingleType
import com.intellij.psi.PsiElementVisitor
import net.l2jliga.fastify_decorators_support.SERVICE_DECORATOR_NAME
import net.l2jliga.fastify_decorators_support.hasDecoratorApplied
import net.l2jliga.fastify_decorators_support.inspections.quickfixes.AnnotateWithServiceDecoratorQuickFix
import net.l2jliga.fastify_decorators_support.isFastifyDecoratorsContext

class ControllerArgumentsInspection : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JSElementVisitor() {
            override fun visitTypeScriptSingleType(singleType: TypeScriptSingleType) {
                if (!isFastifyDecoratorsContext(singleType)) return
                if (singleType.parent !is JSParameter) return

                val mayBeConstructor = singleType.parent.parent.parent
                if (mayBeConstructor !is TypeScriptFunction) return
                if (!mayBeConstructor.isConstructor) return

                val element = (singleType.firstChild as JSReferenceExpression).resolve() ?: return

                if (element is TypeScriptClass) {
                    if (!hasDecoratorApplied(element, SERVICE_DECORATOR_NAME)) {
                        holder.registerProblem(
                            singleType,
                            "Injectable classes must be annotated with @Service decorator",
                            AnnotateWithServiceDecoratorQuickFix(element)
                        )
                    }

                    return
                }

                // TODO: Find available classes which implement interface if possible
                holder.registerProblem(singleType, "Only classes annotated with @Service available for injection")
            }
        }
    }
}