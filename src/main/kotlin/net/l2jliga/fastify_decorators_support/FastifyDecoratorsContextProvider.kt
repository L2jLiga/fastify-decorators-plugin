package net.l2jliga.fastify_decorators_support

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.PsiDirectory
import com.intellij.psi.util.CachedValueProvider

val FASTIFY_DECORATORS_CONTEXT_PROVIDER_EP =
    ExtensionPointName.create<FastifyDecoratorsContextProvider>("net.l2jliga.fastify_decorators_support.contextProvider")

interface FastifyDecoratorsContextProvider {
    fun isFastifyDecoratorsContext(psiDir: PsiDirectory): CachedValueProvider.Result<Boolean>
}