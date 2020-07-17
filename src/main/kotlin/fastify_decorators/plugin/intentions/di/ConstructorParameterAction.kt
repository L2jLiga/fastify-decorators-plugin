package fastify_decorators.plugin.intentions.di

import com.intellij.lang.ecmascript6.psi.ES6ExportDefaultAssignment
import com.intellij.lang.javascript.intentions.JavaScriptIntention
import com.intellij.lang.javascript.psi.JSParameter
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeListOwner
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import fastify_decorators.plugin.CONTROLLER_DECORATOR_NAME
import fastify_decorators.plugin.SERVICE_DECORATOR_NAME
import fastify_decorators.plugin.extensions.hasDecoratorApplied

abstract class ConstructorParameterAction : JavaScriptIntention() {
    override fun getFamilyName() = "Injectable classes"
    override fun startInWriteAction() = true

    override fun isAvailable(project: Project, editor: Editor, element: PsiElement): Boolean {
        val parameter = findParameter(element) ?: return false
        val tsClass = findTsClass(parameter) ?: return false

        val attributeOwner: JSAttributeListOwner =
            if (tsClass.isExportedWithDefault) (tsClass.parent as ES6ExportDefaultAssignment)
            else tsClass

        return attributeOwner.hasDecoratorApplied(CONTROLLER_DECORATOR_NAME, SERVICE_DECORATOR_NAME)
    }

    protected tailrec fun findParameter(element: PsiElement?): JSParameter? = when (element) {
        null -> null
        is JSParameter -> element
        else -> findParameter(element.parent)
    }

    private tailrec fun findTsClass(element: PsiElement?): TypeScriptClass? = when (element) {
        null -> null
        is TypeScriptClass -> element
        else -> findTsClass(element.parent)
    }
}