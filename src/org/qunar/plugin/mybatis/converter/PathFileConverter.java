/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.converter;

import com.google.common.base.Splitter;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.Converter;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.service.JavaService;

import java.util.List;

/**
 * string dir converter
 *
 * Author: jianyu.lin
 * Date: 2016/11/23 Time: 下午3:41
 */
public class PathFileConverter extends Converter<PsiFile> {

    private static final Splitter PATH_SPLITTER = Splitter.on('/');
    private static final String CLASS_PATH_DIR = "";

    @Nullable
    @Override
    public PsiFile fromString(@Nullable @NonNls String s, ConvertContext context) {
        return parseClassPathRelatedFile(context.getProject(), s);
    }

    @Nullable
    @Override
    public String toString(@Nullable PsiFile psiFile, ConvertContext context) {
        return psiFile == null ? null : psiFile.toString();
    }

    /**
     * parse class path related file by path
     * @param project project object
     * @param filePath path
     * @return psiFile
     */
    public static PsiFile parseClassPathRelatedFile(@NotNull Project project,
                                                    @Nullable @NonNls String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        List<String> pathLevels = PATH_SPLITTER.splitToList(filePath);
        String fileName = pathLevels.get(pathLevels.size() -1);
        PsiFile[] files = FilenameIndex.getFilesByName(project, fileName, GlobalSearchScope.projectScope(project));
        if (files.length == 0) {
            return null;
        }
        PsiDirectory[] directories = JavaService.getInstance(project).getRelatedDirectories(CLASS_PATH_DIR);
        for (PsiFile psiFile : files) {
            for (PsiDirectory dir : directories) {
                String absolutePath = dir.getVirtualFile().getCanonicalPath() + "/" + filePath;
                if (StringUtils.equals(psiFile.getVirtualFile().getCanonicalPath(), absolutePath)) {
                    return psiFile;
                }
            }
        }
        return null;
    }
}
