package fastify_decorators.plugin.intentions.di

import com.intellij.lang.ecmascript6.psi.impl.ES6FieldStatementImpl
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.impl.JSChangeUtil
import com.intellij.lang.typescript.intentions.TypeScriptAddImportStatementFix
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil.getParentOfType
import fastify_decorators.plugin.GET_BY_TOKEN
import fastify_decorators.plugin.extensions.deleteAndReformat
import fastify_decorators.plugin.extensions.reformat

class ConstructorToGetByToken : ConstructorParameterAction() {
    override fun getText() = "Convert parameter to \"$GET_BY_TOKEN\" field"

    override fun invoke(project: Project, editor: Editor, element: PsiElement) {
        val parameter = findParameter(element) ?: return
        val tsClass = getParentOfType(parameter, TypeScriptClass::class.java) ?: return

        val modifier = parameter.accessType.name.toLowerCase()
        val typeAndToken = parameter.typeElement?.text ?: return
        val name = parameter.name ?: return

        val esField = JSChangeUtil.createClassMemberPsiFromTextWithContext(
            "$modifier $name: $typeAndToken = $GET_BY_TOKEN($typeAndToken)",
            parameter,
            ES6FieldStatementImpl::class.java
        ) ?: return
        tsClass.addBefore(esField, tsClass.constructor).reformat()
        TypeScriptAddImportStatementFix(GET_BY_TOKEN, tsClass.containingFile).applyFix()

        parameter.deleteAndReformat()
    }
}