/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.intention.statement;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.mybatis.bean.mapper.Mapper;
import org.qunar.plugin.mybatis.bean.mapper.Statement;
import org.qunar.plugin.mybatis.util.DomElements;
import org.qunar.plugin.service.EditorService;
import org.qunar.plugin.util.CodeFormatter;

import java.util.Collection;
import java.util.Set;

/**
 * generate statement int mapping mapper xml
 *
 * Author: jianyu.lin
 * Date: 2016/12/1 Time: 下午7:26
 */
abstract class StatementGenerateIntention implements IntentionAction {

    /**
     * return same as family name
     * @return family name
     */
    @Nls
    @NotNull
    @Override
    public String getText() {
        return getFamilyName();
    }

    /**
     * check method reference to mapper xml
     * @param project current project
     * @param editor current editor
     * @param file current file
     * @return boolean
     */
    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {

        PsiMethod method = getCurrentMethod(editor, file);
        if (method == null || method.getContainingClass() == null) {
            return false;
        }
        @SuppressWarnings("ConstantConditions")
        Collection<Mapper> mapperDomList = DomElements.getMapperDomElements(method.getContainingClass());
        if (mapperDomList.size() != 1) {
            return false;
        }
        Mapper mapperDom = mapperDomList.iterator().next();
        for (Statement statement : DomElements.collectStatements(mapperDom)) {
            if (statement.getId().getValue() == method) {
                return false;
            }
        }
        return checkKeywords(method.getName());
    }

    /**
     * do generate action
     * @param project current project
     * @param editor current editor
     * @param file current file
     * @throws IncorrectOperationException any
     */
    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiMethod method = getCurrentMethod(editor, file);
        if (method == null || method.getContainingClass() == null) {
            return;
        }
        @SuppressWarnings("ConstantConditions")
        Collection<Mapper> mapperDomList = DomElements.getMapperDomElements(method.getContainingClass());
        if (mapperDomList.size() != 1) {
            return;
        }
        generateStatement(project, method, mapperDomList.iterator().next());
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    /**
     * check intention key words
     * @param methodName current method name
     * @return boolean
     */
    private boolean checkKeywords(String methodName) {
        Set<String> keywords = getSubGeneratorKeywords();
        for (String keyword : keywords) {
            if (methodName.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * get the caret point method
     * @param editor current editor
     * @param file current file
     * @return psi method
     */
    @Nullable
    private PsiMethod getCurrentMethod(@Nullable Editor editor, @Nullable PsiFile file) {
        if (editor == null || file == null
                || !(file instanceof PsiJavaFile)) {
            return null;
        }
        PsiJavaFile javaFile = (PsiJavaFile) file;
        int offset = editor.getCaretModel().getOffset();
        PsiElement psiElement = javaFile.findElementAt(offset);
        if (!(psiElement instanceof PsiIdentifier)
                || !(psiElement.getParent() instanceof PsiMethod)) {
            return null;
        }
        return (PsiMethod) psiElement.getParent();
    }

    /**
     * generate statement in mapper xml
     * @param project current project
     * @param mapperDom mapper dom
     */
    private void generateStatement(@NotNull final Project project,
                                   @NotNull final PsiMethod method, @NotNull final Mapper mapperDom) {
        final XmlTag rootTag = mapperDom.getXmlTag();
        if (rootTag == null || mapperDom.getNamespace().getValue() == null) {
            return;
        }
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                //  generate new statement
                Statement statement = generateStatement(mapperDom); 
                statement.getId().setStringValue(method.getName());
                statement.setStringValue("\n");
                CodeFormatter.format(statement.getXmlTag());
                //  navigate to new statement
                //noinspection ConstantConditions
                EditorService.getInstance(project).scrollTo(statement.getId().getXmlAttributeValue());
            }
        });
    }
    
    /**
     * generated dom element
     * @return new dom element
     */
    @NotNull
    protected abstract Statement generateStatement(@NotNull Mapper mapperDom);

    /**
     * get sub generator
     * @return key words
     */
    @NotNull
    protected abstract Set<String> getSubGeneratorKeywords();
}
