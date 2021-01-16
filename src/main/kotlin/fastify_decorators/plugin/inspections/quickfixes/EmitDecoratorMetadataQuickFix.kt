// Copyright 2019-2020 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.inspections.quickfixes

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonProperty
import com.intellij.lang.typescript.tsconfig.TypeScriptConfig
import com.intellij.lang.typescript.tsconfig.TypeScriptConfigUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager

class EmitDecoratorMetadataQuickFix(context: PsiElement, private val tsConfig: TypeScriptConfig) :
    LocalQuickFixAndIntentionActionOnPsiElement(context, context.parent) {
    override fun getFamilyName() = "Injectable classes"
    override fun getText() = "Enable \"emitDecoratorMetadata\" in ${tsConfig.configFile.name}"

    override fun invoke(
        project: Project,
        file: PsiFile,
        editor: Editor?,
        clazz: PsiElement,
        parent: PsiElement
    ) {
        val psiConfig: PsiFile = PsiManager.getInstance(clazz.project).findFile(tsConfig.configFile) ?: return

        val configObject: JsonObject = psiConfig.children.first { it is JsonObject } as JsonObject
        val compilerOptions: JsonProperty = configObject.findProperty("compilerOptions") ?: return
        val value: JsonObject = (compilerOptions.value ?: return) as JsonObject

        val emitDecoratorMetadata = value.findProperty("emitDecoratorMetadata")
        if (emitDecoratorMetadata != null) {
            emitDecoratorMetadata.replace(
                TypeScriptConfigUtil.createJsonProperty(
                    clazz.project,
                    "emitDecoratorMetadata",
                    "true",
                    false
                )
            )

            return
        }

        val placeToAdd = value.findProperty("experimentalDecorators")

        val emitMetadataProperty =
            TypeScriptConfigUtil.createJsonProperty(clazz.project, "emitDecoratorMetadata", "true", true)
        val comma = emitMetadataProperty.nextSibling

        if (placeToAdd != null) {
            value.addAfter(emitMetadataProperty, placeToAdd)
            value.addAfter(comma, placeToAdd)
        } else {
            val firstProperty = value.children.first()
            value.addBefore(emitMetadataProperty, firstProperty)
            value.addBefore(comma, firstProperty)
        }
    }
}
