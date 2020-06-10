// Copyright 2019-2020 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.lang.ecmascript6.psi.ES6ExportDefaultAssignment
import com.intellij.lang.javascript.psi.JSParameter
import com.intellij.lang.javascript.psi.ecma6.ES6Decorator
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.ecma6.TypeScriptFunction
import com.intellij.lang.javascript.psi.ecma6.TypeScriptSingleType
import com.intellij.psi.PsiElement
import fastify_decorators.plugin.CONTROLLER_DECORATOR_NAME
import fastify_decorators.plugin.INJECT_DECORATOR_NAME
import fastify_decorators.plugin.SERVICE_DECORATOR_NAME
import fastify_decorators.plugin.extensions.hasDecoratorApplied
import fastify_decorators.plugin.extensions.isFastifyDecoratorsContext

abstract class ArgumentsInspectionBase : LocalInspectionTool() {
    fun outOfScope(decorator: ES6Decorator) = !decorator.isFastifyDecoratorsContext
            || decorator.decoratorName != INJECT_DECORATOR_NAME
            || !isDIClass(getTypeScriptClass(decorator))

    fun outOfScope(singleType: TypeScriptSingleType) = !singleType.isFastifyDecoratorsContext
            || singleType.parent !is JSParameter
            || withinRegularMethod(singleType)
            || !isDIClass(getTypeScriptClass(singleType))

    private fun isDIClass(clazz: TypeScriptClass): Boolean {
        if (clazz.hasDecoratorApplied(CONTROLLER_DECORATOR_NAME, SERVICE_DECORATOR_NAME)) return true

        val parent = clazz.parent
        if (parent is ES6ExportDefaultAssignment) return parent.hasDecoratorApplied(CONTROLLER_DECORATOR_NAME)

        return false
    }

    private fun withinRegularMethod(singleType: TypeScriptSingleType): Boolean {
        val mayBeConstructor = singleType.parent.parent.parent
        if (mayBeConstructor !is TypeScriptFunction) return true
        return !mayBeConstructor.isConstructor
    }

    private tailrec fun getTypeScriptClass(element: PsiElement): TypeScriptClass =
        if (element is TypeScriptClass) element else getTypeScriptClass(element.parent)
}