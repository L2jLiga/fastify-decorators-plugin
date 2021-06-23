// Copyright 2019-2021 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.extensions

import com.intellij.lang.javascript.dialects.TypeScriptLanguageDialect
import com.intellij.lang.javascript.psi.impl.JSChangeUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement

inline fun <reified R : PsiElement> Project.createFromText(text: String) = JSChangeUtil.createJSFileFromText(
    this,
    text,
    TypeScriptLanguageDialect.findInstance(TypeScriptLanguageDialect::class.java),
).firstChild as R