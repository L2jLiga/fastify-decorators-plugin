// Copyright 2019-2020 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package net.l2jliga.fastify_decorators_support.providers

import com.intellij.codeInsight.daemon.ImplicitUsageProvider
import com.intellij.lang.ecmascript6.psi.ES6ExportDefaultAssignment
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.ecma6.TypeScriptFunction
import com.intellij.psi.PsiElement
import net.l2jliga.fastify_decorators_support.hasDecoratorApplied
import net.l2jliga.fastify_decorators_support.isFastifyDecoratorsContext

class ControllerConstructorUsageProvider : ImplicitUsageProvider {
    override fun isImplicitWrite(element: PsiElement) = false
    override fun isImplicitRead(element: PsiElement) = false

    override fun isImplicitUsage(element: PsiElement): Boolean {
        if (!isFastifyDecoratorsContext(element)) return false
        if (element !is TypeScriptFunction) return false
        if (!element.isConstructor) return false

        val typeScriptClass = element.parent
        if (typeScriptClass is TypeScriptClass)
            return hasDecoratorApplied(typeScriptClass)

        val defaultExport = typeScriptClass.parent
        if (defaultExport !is ES6ExportDefaultAssignment) return false

        return hasDecoratorApplied(defaultExport)
    }
}