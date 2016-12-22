/*
 * Copyright (c) 2017 Qunar.com. All Rights Reserved.
 */
package org.qunar.plugin.mybatis.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.sql.psi.SqlFile;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.mybatis.generator.JavaGenerator;
import org.qunar.plugin.mybatis.generator.XmlGenerator;

/**
 * generator sql action
 *
 * Author: jianyu.lin
 * Date: 2016/12/22 Time: 上午11:22
 */
public class SqlGenerateAction extends AnAction {

    /**
     * click action of right click menu
     * @param e event
     */
    @Override
    public void actionPerformed(AnActionEvent e) {
        //Get required data keys
        final Project project = e.getData(CommonDataKeys.PROJECT);
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (project == null || editor == null) {
            return;
        }

        SqlFile sqlFile = getCurrentEditFile(project, editor);
        if (sqlFile == null) return;

        System.out.println(sqlFile);

        String qualifiedName = "org.qunar.temp.hi.TestClass";

        JavaGenerator javaGenerator = new JavaGenerator(project, sqlFile, qualifiedName);
        PsiClass generateClass = javaGenerator.generate();
        System.out.println(generateClass);

        if (generateClass == null) {
            return;
        }

        String relatedPath = "mybatis/mapper";
        XmlGenerator xmlGenerator = new XmlGenerator(project, generateClass, sqlFile, relatedPath);
        XmlFile xmlFile = xmlGenerator.generate();
        System.out.println(xmlFile);
    }

    /**
     * {@inheritDoc}
     * @param e event
     */
    @Override
    public void update(AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        SqlFile sqlFile = getCurrentEditFile(project, editor);
        if (sqlFile == null) {
            e.getPresentation().setVisible(false);
        }
    }

    /**
     * get current edit file
     * @param project current project
     * @param editor current editor
     * @return psi file
     */
    @Nullable
    private SqlFile getCurrentEditFile(Project project, Editor editor) {
        VirtualFile virtualFile = ((EditorEx) editor).getVirtualFile();
        PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        if (!(psiFile instanceof SqlFile)) return null;
        return (SqlFile) psiFile;
    }
}
