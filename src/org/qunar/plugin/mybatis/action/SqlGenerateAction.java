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
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.sql.psi.SqlCreateTableStatement;
import com.intellij.sql.psi.SqlElement;
import com.intellij.sql.psi.SqlFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.mybatis.generator.JavaGenerator;
import org.qunar.plugin.mybatis.generator.XmlVelocityGenerator;

import java.util.List;

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
        SqlElement sqlElement = getFirstCreateTableStatement(sqlFile.getDdl());
        if (sqlElement == null) return;

        System.out.println(sqlFile);

        String qualifiedName = "org.qunar.temp.dao.SupplierDao";

        JavaGenerator javaGenerator = new JavaGenerator(project, sqlElement, qualifiedName);
        Pair<PsiClass, PsiClass> classPair = javaGenerator.generate();

        if (classPair == null) {
            return;
        }

        String relatedPath = "mybatis/mapper/temp/SupplierMapper";
        XmlVelocityGenerator xmlGenerator = new XmlVelocityGenerator(project,
                classPair.getFirst(), classPair.getSecond(), sqlElement, relatedPath);
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
        } else {
            SqlElement sqlElement = getFirstCreateTableStatement(sqlFile.getDdl());
            if (sqlElement == null) {
                e.getPresentation().setVisible(false);
            }
        }
    }

    /**
     * peek first create table sql
     * @param sqlElements ddl list
     * @return create table ddl
     */
    @Nullable
    private SqlElement getFirstCreateTableStatement(@NotNull List<SqlElement> sqlElements) {
        for (SqlElement sqlElement : sqlElements) {
            if (sqlElement instanceof SqlCreateTableStatement) {
                return sqlElement;
            }
        }
        return null;
    }

    /**
     * get current edit file
     * @param project current project
     * @param editor current editor
     * @return psi file
     */
    @Nullable
    private SqlFile getCurrentEditFile(Project project, Editor editor) {
        if (project == null || editor == null) return null;
        VirtualFile virtualFile = ((EditorEx) editor).getVirtualFile();
        PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        if (!(psiFile instanceof SqlFile)) return null;
        return (SqlFile) psiFile;
    }
}
