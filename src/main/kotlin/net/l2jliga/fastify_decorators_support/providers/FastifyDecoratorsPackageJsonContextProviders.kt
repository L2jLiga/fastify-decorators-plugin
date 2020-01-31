package net.l2jliga.fastify_decorators_support.providers

import com.intellij.javascript.nodejs.packageJson.PackageJsonFileManager
import com.intellij.lang.javascript.buildTools.npm.PackageJsonUtil
import com.intellij.psi.PsiDirectory
import com.intellij.psi.util.CachedValueProvider
import net.l2jliga.fastify_decorators_support.FASTIFY_DECORATORS_PACKAGE
import net.l2jliga.fastify_decorators_support.FastifyDecoratorsContextProvider

class FastifyDecoratorsPackageJsonContextProviders : FastifyDecoratorsContextProvider {
    override fun isFastifyDecoratorsContext(psiDir: PsiDirectory): CachedValueProvider.Result<Boolean> {
        val manager = PackageJsonFileManager.getInstance(psiDir.project)
        val dirPath = psiDir.virtualFile.path + "/"
        var result = false
        for (config in manager.validPackageJsonFiles) {
            if (dirPath.startsWith(config.parent.path + "/")) {
                val data = PackageJsonUtil.getOrCreateData(config)
                if (data.isDependencyOfAnyType(FASTIFY_DECORATORS_PACKAGE)) {
                    result = true
                    break
                }
            }
        }
        return CachedValueProvider.Result.create(result, manager.modificationTracker)
    }
}