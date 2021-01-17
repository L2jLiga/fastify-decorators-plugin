// Copyright 2019-2021 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.providers

import com.intellij.codeInsight.daemon.ImplicitUsageProvider
import com.intellij.lang.ecmascript6.psi.ES6ExportDefaultAssignment
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.ecma6.TypeScriptFunction
import com.intellij.psi.PsiElement
import fastify_decorators.plugin.CONTROLLER_DECORATOR_NAME
import fastify_decorators.plugin.SERVICE_DECORATOR_NAME
import fastify_decorators.plugin.extensions.hasDecorator
import fastify_decorators.plugin.extensions.isFastifyDecoratorsContext

class ImplicitConstructorUsageProvider : ImplicitUsageProvider {
    override fun isImplicitWrite(element: PsiElement) = false
    override fun isImplicitRead(element: PsiElement) = false

    override fun isImplicitUsage(element: PsiElement): Boolean {
        if (
            !element.isFastifyDecoratorsContext ||
            element !is TypeScriptFunction ||
            !element.isConstructor
        ) return false

        val typeScriptClass = element.parent
        val defaultExport = typeScriptClass.parent

        return when {
            defaultExport is ES6ExportDefaultAssignment -> defaultExport.hasDecorator(
                CONTROLLER_DECORATOR_NAME,
                SERVICE_DECORATOR_NAME
            )
            typeScriptClass is TypeScriptClass -> typeScriptClass.hasDecorator(
                CONTROLLER_DECORATOR_NAME,
                SERVICE_DECORATOR_NAME
            )
            else -> false
        }
    }
}