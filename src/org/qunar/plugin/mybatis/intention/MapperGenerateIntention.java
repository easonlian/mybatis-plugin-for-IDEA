/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.mybatis.bean.mapper.Mapper;
import org.qunar.plugin.mybatis.ui.CreateMapperXmlDialog;
import org.qunar.plugin.mybatis.util.MapperConfHolder;
import org.qunar.plugin.service.EditorService;

/**
 * generate new mapper xml
 *
 * Author: jianyu.lin
 * Date: 2016/12/1 Time: 下午7:26
 */
public class MapperGenerateIntention implements IntentionAction {
    
    @Nls
    @NotNull
    @Override
    public String getText() {
        return getFamilyName();
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Generate Mybatis Mapper Xml";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return getCurrentClass(editor, file) != null;
    }

    @Override
    public void invoke(@NotNull final Project project, Editor editor, final PsiFile file) throws IncorrectOperationException {
        final PsiClass psiClass = getCurrentClass(editor, file);
        if (psiClass == null) {
            return;
        }
        new WriteCommandAction(project) {
            @Override
            protected void run(@NotNull Result result) throws Throwable {
                Module module = ModuleUtil.findModuleForPsiElement(file);
                if (module == null) return;
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        CreateMapperXmlDialog dialog = new CreateMapperXmlDialog(project, psiClass);
                        if (dialog.showAndGet()) {
                            PsiFile mapperFile = dialog.getNewMapperFile();
                            if (mapperFile == null) return;
                            Mapper mapperDom = MapperConfHolder.INSTANCE.getDomElement(mapperFile);
                            if (mapperDom == null) return;
                            EditorService.getInstance(project).scrollTo(mapperDom.getXmlTag());
                        }
                    }
                });
            }
        }.execute();
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    /**
     * get the caret point method
     * @param editor current editor
     * @param file current file
     * @return psi method
     */
    @Nullable
    private PsiClass getCurrentClass(@Nullable Editor editor, @Nullable PsiFile file) {
        if (editor == null || file == null
                || !(file instanceof PsiJavaFile)) {
            return null;
        }
        PsiJavaFile javaFile = (PsiJavaFile) file;
        int offset = editor.getCaretModel().getOffset();
        PsiElement psiElement = javaFile.findElementAt(offset);
        if (!(psiElement instanceof PsiIdentifier)
                || !(psiElement.getParent() instanceof PsiClass)) {
            return null;
        }
        if (javaFile.getClasses().length == 0) {
            return null;
        }
        PsiClass psiClass = javaFile.getClasses()[0];
        if (!psiClass.isInterface()
                && !MapperConfHolder.INSTANCE.getMapperDomElements(psiClass).isEmpty()) {
            return null;
        }
        return psiClass;
    }
}
