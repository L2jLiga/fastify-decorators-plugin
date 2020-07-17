package fastify_decorators.plugin.inspections.quickfixes

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement
import com.intellij.lang.ecmascript6.psi.impl.ES6FieldStatementImpl
import com.intellij.lang.javascript.DialectDetector
import com.intellij.lang.javascript.psi.ecma6.ES6Decorator
import com.intellij.lang.javascript.psi.ecma6.TypeScriptField
import com.intellij.lang.javascript.psi.ecma6.TypeScriptSingleType
import com.intellij.lang.javascript.psi.impl.JSChangeUtil
import com.intellij.lang.javascript.refactoring.util.JSRefactoringUtil
import com.intellij.lang.typescript.intentions.TypeScriptAddImportStatementFix
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import fastify_decorators.plugin.GET_BY_TOKEN
import fastify_decorators.plugin.INJECT_DECORATOR_NAME
import fastify_decorators.plugin.extensions.replaceAndReformat

class ReplaceInjectWithGetByTokenQuickFix(context: ES6Decorator) :
    LocalQuickFixAndIntentionActionOnPsiElement(context, context.parent.parent) {
    override fun getFamilyName() = "Injectable classes"
    override fun getText() = "Replace \"@$INJECT_DECORATOR_NAME\" with \"$GET_BY_TOKEN\""

    override fun invoke(
        project: Project,
        file: PsiFile,
        editor: Editor?,
        decorator: PsiElement,
        fieldStatement: PsiElement
    ) {
        if (decorator !is ES6Decorator) return
        if (fieldStatement !is ES6FieldStatementImpl) return

        TypeScriptAddImportStatementFix(GET_BY_TOKEN, decorator.containingFile).applyFix()
        decorator.delete()

        val field = fieldStatement.children.find { it is TypeScriptField } as? TypeScriptField ?: return
        val type: String = field.children.find { it is TypeScriptSingleType }?.text ?: "any"
        val token = getInjectionTokenFrom(decorator) ?: return

        if (field.hasInitializer()) JSRefactoringUtil.replaceExpressionAndReformat(
            field.initializer!!,
            "$GET_BY_TOKEN<$type>($token)"
        )
        else field.replaceAndReformat(createReplacementForField(project, field, type, token))
    }

    private fun getInjectionTokenFrom(decorator: ES6Decorator): String? {
        val decoratorArgs = decorator.children.first().children.last().children

        // 2 paren + argument = 3 arguments
        return if (decoratorArgs.size == 3) decoratorArgs[1].text
        else null
    }

    private fun createReplacementForField(
        project: Project,
        field: TypeScriptField,
        type: String,
        token: String
    ): TypeScriptField =
        JSChangeUtil.createClassMemberFromText(
            project,
            "${field.text}=$GET_BY_TOKEN<$type>($token)",
            DialectDetector.languageDialectOfElement(field)
        ).psi.children.find { it is TypeScriptField } as TypeScriptField
}