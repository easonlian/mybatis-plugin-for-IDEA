/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.service;

import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 编辑区域工具
 *
 * Author: jianyu.lin
 * Date: 2016/11/21 Time: 下午1:56
 */
public class EditorService {

    private final FileEditorManager fileEditorManager;

    private EditorService(Project project) {
        this.fileEditorManager = FileEditorManager.getInstance(project);
    }

    /**
     * 实例方法
     * @param project 工程对象
     * @return 获取实例
     */
    public static EditorService getInstance(Project project) {
        return ServiceManager.getService(project, EditorService.class);
    }
    
    @Nullable
    public Editor getCurrentEditor() {
        return fileEditorManager.getSelectedTextEditor();
    }

    /**
     * 导航跳转、移动caret
     * @param element 目标元素
     * @param offset caret相对位置
     */
    public void scrollTo(@NotNull PsiElement element, int offset) {
        NavigationUtil.activateFileWithPsiElement(element, true);
        Editor editor = fileEditorManager.getSelectedTextEditor();
        if (null != editor) {
            editor.getCaretModel().moveToOffset(offset);
            editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
        }
    }

    /**
     * 导航跳转、移动caret
     * @param element 目标元素
     */
    public void scrollTo(@NotNull PsiElement element) {
        scrollTo(element, element.getTextOffset());
    }
}
