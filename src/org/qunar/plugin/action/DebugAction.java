/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.qunar.plugin.service.EditorService;

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
        if (project == null) { return; }
    }
}
