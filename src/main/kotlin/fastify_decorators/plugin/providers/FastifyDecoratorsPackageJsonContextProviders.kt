package fastify_decorators.plugin.providers

import com.intellij.javascript.nodejs.PackageJsonData
import com.intellij.javascript.nodejs.packageJson.PackageJsonFileManager
import com.intellij.psi.PsiDirectory
import com.intellij.psi.util.CachedValueProvider
import fastify_decorators.plugin.FASTIFY_DECORATORS_PACKAGE
import fastify_decorators.plugin.FastifyDecoratorsContextProvider

class FastifyDecoratorsPackageJsonContextProviders :
    FastifyDecoratorsContextProvider {
    override fun isFastifyDecoratorsContext(psiDir: PsiDirectory): CachedValueProvider.Result<Boolean> {
        val manager = PackageJsonFileManager.getInstance(psiDir.project)
        val dirPath = psiDir.virtualFile.path + "/"
        var result = false
        for (config in manager.validPackageJsonFiles) {
            if (dirPath.startsWith(config.parent.path + "/")) {
                val data = PackageJsonData.getOrCreate(config)
                if (data.isDependencyOfAnyType(FASTIFY_DECORATORS_PACKAGE)) {
                    result = true
                    break
                }
            }
        }
        return CachedValueProvider.Result.create(result, manager.modificationTracker)
    }
}