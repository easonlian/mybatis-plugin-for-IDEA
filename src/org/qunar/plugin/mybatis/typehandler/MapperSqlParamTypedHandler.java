/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.typehandler;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.qunar.plugin.mybatis.contributor.StatementParamsCompletionContributor;
import org.qunar.plugin.mybatis.util.DomElements;

/**
 * allow #{ or ${ active auto completion <br>
 * add statement sql text auto pop with method params<br>
 * to see: {@link StatementParamsCompletionContributor}<br>
 *
 * Author: jianyu.lin
 * Date: 2016/11/28 Time: 下午6:35
 */
public class MapperSqlParamTypedHandler extends TypedHandlerDelegate {

    @Override
    public Result charTyped(char charTyped, Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        if (charTyped == '{' && DomElements.isMapperXmlFile(file)) {
            int offset = editor.getCaretModel().getOffset();
            char preChar = editor.getDocument().getCharsSequence().charAt(offset - 2);
            if (offset >= 2 && (preChar == '#' || preChar == '$')) {
                //noinspection unchecked
                AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, Condition.TRUE);
                PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
            }
            return Result.STOP;
        }
        return super.charTyped(charTyped, project, editor, file);
    }
}
