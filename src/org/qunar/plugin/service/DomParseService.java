/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.service;

import com.google.common.collect.Lists;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Dom文件工具服务
 *
 * Author: jianyu.lin
 * Date: 2016/11/21 Time: 下午10:49
 */
public class DomParseService {

    private final Project project;

    private DomParseService(Project project) {
        this.project = project;
    }

    public static DomParseService INSTANCE(Project project) {
        return ServiceManager.getService(project, DomParseService.class);
    }

    /**
     * 根据文件解析dom
     * @param psiFile 文件
     * @param clazz 转换类型
     * @param <T> dom类型
     * @return dom对象
     */
    @Nullable
    public <T extends DomElement> T parseDomElementsByFile(@Nullable PsiFile psiFile, Class<T> clazz) {
        if (psiFile == null || clazz == null) {
            return null;
        }
        if (!(psiFile instanceof XmlFile)) {
            return null;
        }
        XmlFile xmlFile = (XmlFile) psiFile;
        if (xmlFile.getRootTag() == null) {
            return null;
        }
        if (!checkRootTagName(clazz.getSimpleName(), xmlFile.getRootTag().getName())) {
            return null;
        }
        return parseDomElement(xmlFile, clazz);
    }

    /**
     * 根据文件名解析dom
     * @param fileName 文件名
     * @param clazz 转换类型
     * @param <T> dom类型
     * @return dom对象
     */
    @NotNull
    public <T extends DomElement> List<T> parseDomElementsByName(String fileName, Class<T> clazz) {
        if (StringUtils.isBlank(fileName) || clazz == null) {
            return Lists.newArrayList();
        }
        PsiFile[] psiFiles = FilenameIndex.getFilesByName(project, fileName, GlobalSearchScope.projectScope(project));
        List<T> domElements = Lists.newArrayList();
        for (PsiFile psiFile : psiFiles) {
            T domElement = parseDomElementsByFile(psiFile, clazz);
            if (domElement != null) {
                domElements.add(domElement);
            }
        }
        return domElements;
    }

    /**
     * 检测类名和根节点是否对应
     * @param className 类名
     * @param rootTag 根tag
     * @return 是否对应
     */
    private boolean checkRootTagName(String className, String rootTag) {
        if (StringUtils.equals(className, rootTag)) {
            return true;
        }
        String firstLower = className.substring(0, 1).toLowerCase() + className.substring(1, className.length());
        return StringUtils.equals(firstLower, rootTag);
    }

    /**
     * 根据文件解析dom成数据对象
     * @param xmlFile 文件
     * @param clazz 转换类型
     * @param <T> dom类型
     * @return dom数据对象
     */
    private <T extends DomElement> T parseDomElement(XmlFile xmlFile, Class<T> clazz) {
        DomManager domManager = DomManager.getDomManager(project);
        DomFileElement<T> fileElement = domManager.getFileElement(xmlFile, clazz);
        return fileElement == null ? null : fileElement.getRootElement();
    }
}
