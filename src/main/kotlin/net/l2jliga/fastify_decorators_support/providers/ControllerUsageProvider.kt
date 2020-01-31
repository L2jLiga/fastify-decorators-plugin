// Copyright 2019-2020 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package net.l2jliga.fastify_decorators_support.providers

import com.intellij.codeInsight.daemon.ImplicitUsageProvider
import com.intellij.lang.ecmascript6.psi.ES6ExportDefaultAssignment
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.psi.PsiElement
import net.l2jliga.fastify_decorators_support.hasDecoratorApplied
import net.l2jliga.fastify_decorators_support.isFastifyDecoratorsContext

class ControllerUsageProvider : ImplicitUsageProvider {
    override fun isImplicitWrite(element: PsiElement) = false

    override fun isImplicitRead(element: PsiElement) = false

    override fun isImplicitUsage(element: PsiElement): Boolean {
        if (!isFastifyDecoratorsContext(element)) return false
        val typeScriptClass = extractClass(element)
        if (typeScriptClass === null) return false

        return hasDecoratorApplied(typeScriptClass)
    }

    private fun extractClass(element: PsiElement): TypeScriptClass? = when (element) {
        is TypeScriptClass -> element
        is ES6ExportDefaultAssignment -> extractClassFromDefaultExport(element)
        else -> null
    }

    private fun extractClassFromDefaultExport(element: ES6ExportDefaultAssignment) =
        when (val reference = element.children.last()) {
            is TypeScriptClass -> reference
            is JSReferenceExpression -> extractClassFromReference(reference)
            else -> null
        }

    private fun extractClassFromReference(reference: JSReferenceExpression) =
        when (val resolvedClass = reference.resolve()) {
            is TypeScriptClass -> resolvedClass
            else -> null
        }
}
