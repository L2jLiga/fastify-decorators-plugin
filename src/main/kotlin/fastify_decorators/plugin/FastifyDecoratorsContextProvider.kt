// Copyright 2019-2020 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.PsiDirectory
import com.intellij.psi.util.CachedValueProvider

val FASTIFY_DECORATORS_CONTEXT_PROVIDER_EP =
    ExtensionPointName.create<FastifyDecoratorsContextProvider>("fastify_decorators.plugin.contextProvider")

interface FastifyDecoratorsContextProvider {
    fun isFastifyDecoratorsContext(psiDir: PsiDirectory): CachedValueProvider.Result<Boolean>
}