/*
 * Copyright (c) 2017 Qunar.com. All Rights Reserved.
 */
package org.qunar.plugin.mybatis.generator;

import com.intellij.codeInsight.daemon.impl.quickfix.CreateClassKind;
import com.intellij.codeInsight.daemon.impl.quickfix.CreateFromUsageUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CreateClassUtil;
import com.intellij.sql.psi.SqlElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.service.EditorService;
import org.qunar.plugin.service.JavaService;

import static com.intellij.psi.util.CreateClassUtil.DEFAULT_CLASS_TEMPLATE;

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
    private final String beanName;
    private final String daoName;

    public JavaGenerator(@NotNull Project project,
                         @NotNull SqlElement sqlElement, @NotNull String qualifiedName) {
        super(project, sqlElement);
        int lastIndex = qualifiedName.lastIndexOf(".");
        this.packageName = lastIndex < 0 ? "" : qualifiedName.substring(0, lastIndex);
        this.daoName = qualifiedName.substring(lastIndex + 1, qualifiedName.length());
        this.beanName = daoName.substring(0, daoName.length() - 3);
    }

    /**
     * {@inheritDoc}
     * @return psi class
     */
    @Nullable
    @Override
    public Pair<PsiClass, PsiClass> generate() {
        VirtualFile dirVirtualFile = moduleSetting.getJavaSources().get(0);
        PsiDirectory baseDir = PsiManager.getInstance(project).findDirectory(dirVirtualFile);
        final PsiDirectory generateDir = createOrFindDirectory(baseDir);

        return ApplicationManager.getApplication().runWriteAction(new Computable<Pair<PsiClass, PsiClass>>() {
            @Override
            public Pair<PsiClass, PsiClass> compute() {
                final PsiClass daoClass = CreateFromUsageUtils.createClass(CreateClassKind.INTERFACE,
                        generateDir, daoName, generateDir.getManager(), generateDir, null, null);

                if (daoClass == null) {
                    return null;
                }
                PsiClass beanClass = CreateClassUtil.createClassFromCustomTemplate(
                        generateDir, moduleSetting.getModule(), beanName, DEFAULT_CLASS_TEMPLATE);
                if (beanClass == null) {
                    return null;
                }
                generateMethods(daoClass, beanClass);
                EditorService.getInstance(project).scrollTo(daoClass);
                return Pair.create(daoClass, beanClass);
            }
        });
    }

    /**
     * generate common mapper method
     * @param daoClass dao class
     * @param beanClass bean class
     */
    private void generateMethods(@NotNull final PsiClass daoClass, @NotNull final PsiClass beanClass) {
        WriteCommandAction.runWriteCommandAction(project, new Runnable() {
            @Override
            public void run() {

                PsiElementFactory elementFactory = JavaService.getInstance(project).elementFactory;

                PsiClass javaList = JavaService.getInstance(project).findClass("java.util.List", GlobalSearchScope.allScope(project));
                if (javaList != null) {
                    PsiImportStatement importStatement = elementFactory.createImportStatement(javaList);
                    //noinspection ConstantConditions
                    ((PsiJavaFile) daoClass.getParent()).getImportList().add(importStatement);
                }

                String methodStr = String.format("int insert(%s bean);", beanClass.getName());
                PsiMethod insertMethod = elementFactory.createMethodFromText(methodStr, null);
                daoClass.add(insertMethod);

                methodStr = String.format("int delete(%s bean);", beanClass.getName());
                PsiMethod deleteMethod = elementFactory.createMethodFromText(methodStr, null);
                daoClass.add(deleteMethod);

                methodStr = String.format("%s selectOne(%s bean);", beanClass.getName(), beanClass.getName());
                PsiMethod selectOneMethod = elementFactory.createMethodFromText(methodStr, null);
                daoClass.add(selectOneMethod);

                methodStr = String.format("List<%s> selectList(%s bean);", beanClass.getName(), beanClass.getName());
                PsiMethod selectListMethod = elementFactory.createMethodFromText(methodStr, null);
                daoClass.add(selectListMethod);

                CodeStyleManager.getInstance(project).reformat(daoClass);
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
