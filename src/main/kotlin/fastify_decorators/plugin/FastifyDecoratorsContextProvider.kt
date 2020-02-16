package fastify_decorators.plugin

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.PsiDirectory
import com.intellij.psi.util.CachedValueProvider

val FASTIFY_DECORATORS_CONTEXT_PROVIDER_EP =
    ExtensionPointName.create<FastifyDecoratorsContextProvider>("fastify_decorators.plugin.contextProvider")

interface FastifyDecoratorsContextProvider {
    fun isFastifyDecoratorsContext(psiDir: PsiDirectory): CachedValueProvider.Result<Boolean>
}