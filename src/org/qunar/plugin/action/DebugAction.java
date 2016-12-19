/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.qunar.plugin.util.Modules;

/**
 *
 *
 * Author: jianyu.lin
 * Date: 2016/11/21 Time: 上午11:35
 */
public class DebugAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        Document document = e.getData(CommonDataKeys.EDITOR).getDocument();
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        System.out.println(Modules.getModuleSettingByElement(psiFile));
        System.out.println(project + "\n");
    }
}
