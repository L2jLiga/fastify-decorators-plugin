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

                // TODO: Check class for @Service annotation
                if (element is TypeScriptClass) return

                // TODO: Find available classes which implement interface if possible
                holder.registerProblem(singleType, "Controller constructor arguments mush be a class")
            }
        }
    }
}