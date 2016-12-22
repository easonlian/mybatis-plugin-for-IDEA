/*
 * Copyright (c) 2017 Qunar.com. All Rights Reserved.
 */
package org.qunar.plugin.mybatis.generator;

import com.intellij.codeInsight.daemon.impl.quickfix.CreateClassKind;
import com.intellij.codeInsight.daemon.impl.quickfix.CreateFromUsageUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.sql.psi.SqlFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.bean.ModuleSetting;
import org.qunar.plugin.service.EditorService;
import org.qunar.plugin.util.Modules;

import java.util.List;

/**
 * generate mapper java interface
 *
 * Author: jianyu.lin
 * Date: 2016/12/22 Time: 下午2:59
 */
public class JavaGenerator extends AbstractGenerator {

    /* qualified class name */
    @NotNull
    private final String packageName;
    private final String javaName;

    public JavaGenerator(@NotNull Project project,
                         @NotNull SqlFile sqlFile, @NotNull String qualifiedName) {
        super(project, sqlFile);
        int lastIndex = qualifiedName.lastIndexOf(".");
        this.packageName = lastIndex < 0 ? "" : qualifiedName.substring(0, lastIndex);
        this.javaName = qualifiedName.substring(lastIndex + 1, qualifiedName.length());
    }

    /**
     * {@inheritDoc}
     * @return psi class
     */
    @Nullable
    @Override
    public PsiClass generate() {
        VirtualFile dirVirtualFile = moduleSetting.getJavaSources().get(0);
        PsiDirectory baseDir = PsiManager.getInstance(project).findDirectory(dirVirtualFile);
        final PsiDirectory generateDir = createOrFindDirectory(baseDir);

        return ApplicationManager.getApplication().runWriteAction(new Computable<PsiClass>() {
            @Override
            public PsiClass compute() {
//                PsiClass createdClass = CreateClassUtil.createClassFromCustomTemplate(
//                        generateDir, moduleSetting.getModule(), javaName, DEFAULT_CLASS_TEMPLATE);
                PsiClass createdClass = CreateFromUsageUtils.createClass(CreateClassKind.INTERFACE,
                        generateDir, javaName, generateDir.getManager(), generateDir, null, null);
                if (createdClass == null) {
                    return null;
                }
                EditorService.getInstance(project).scrollTo(createdClass);
                System.out.println(createdClass.getContainingFile().getVirtualFile().getPath());
                return createdClass;
            }
        });
    }

    /**
     * create or find directory with given base dir
     * @param baseDir class path based dir
     * @return psi directory
     */
    private PsiDirectory createOrFindDirectory(final PsiDirectory baseDir) {
        return ApplicationManager.getApplication().runWriteAction(new Computable<PsiDirectory>() {
            @Override
            public PsiDirectory compute() {
                PsiDirectory directory = baseDir;
                for (String path : packageName.split("\\.")) {
                    PsiDirectory subDirectory = directory.findSubdirectory(path);
                    if (subDirectory == null) {
                        subDirectory = directory.createSubdirectory(path);
                    }
                    directory = subDirectory;
                }
                return directory;
            }
        });
    }
}
