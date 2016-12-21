/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.service;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.impl.JavaPsiFacadeEx;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

/**
 * Java psi relative service
 * 
 * Author: jianyu.lin
 * Date: 2016/11/23 Time: 上午1:05
 */
public class JavaService {

    private static final Splitter POINT_SPLITTER = Splitter.on('.').omitEmptyStrings();
    private static final Splitter PHYSICAL_SPLITTER = Splitter.on('/').omitEmptyStrings();
    
    private final Project project;
    private final JavaPsiFacade javaPsiFacade;
    private final JavaPsiFacadeEx javaPsiFacadeEx;
//    private final PsiElementFactory elementFactory;
    
    private JavaService(Project project) {
        this.project = project;
        this.javaPsiFacade = JavaPsiFacade.getInstance(project);
        this.javaPsiFacadeEx = JavaPsiFacadeEx.getInstanceEx(project);
//        this.elementFactory = JavaPsiFacade.getElementFactory(project);
    }
    
    public static JavaService getInstance(Project project) {
        return ServiceManager.getService(project, JavaService.class);
    }

    /**
     * search the directory relate to classPath
     * @param javaPath java path
     * @return directories
     */
    public PsiDirectory[] getRelatedDirectories(@SuppressWarnings("SameParameterValue") String javaPath) {
        if (javaPath == null) {
            return PsiDirectory.EMPTY_ARRAY;
        }
        PsiPackage psiPackage = findPackage(javaPath);
        if (psiPackage == null) {
            return PsiDirectory.EMPTY_ARRAY;
        }
        return psiPackage.getDirectories(GlobalSearchScope.projectScope(project));
    }

    /**
     * resolve java class path error indexes
     * @param qualifiedName string path
     * @return error part indexes
     */
    @NotNull
    public List<Pair<String, TextRange>> getQualifiedClassErrorRanges(String qualifiedName) {
        if (qualifiedName == null) {
            return Lists.newArrayList();
        }
        Iterator<String> iterable = POINT_SPLITTER.split(qualifiedName).iterator();
        List<Pair<String, TextRange>> result = Lists.newArrayList();
        String temp = "";
        while (iterable.hasNext()) {
            String level = iterable.next();
            temp += level;
            if (result.size() > 0 || (iterable.hasNext() && findPackage(temp) == null)
                    || (!iterable.hasNext() && findProjectClass(temp) == null)) {
                int index = qualifiedName.lastIndexOf(level) + 1;
                result.add(Pair.createNonNull(level, TextRange.create(index, index + level.length())));
            }
            temp += ".";
        }
        return result;
    }

    /**
     * resolve java package path error indexes
     * @param packagePath string path
     * @return error part indexes
     */
    public List<Pair<String, TextRange>> getClassPathRelatedPathErrorRanges(String packagePath) {
        return getPackagePathErrorRanges(PHYSICAL_SPLITTER.split(packagePath).iterator(), packagePath);
    }

    /**
     * resolve java package path error indexes
     * @param packagePath string path
     * @return error part indexes
     */
    public List<Pair<String, TextRange>> getPackagePathErrorRanges(String packagePath) {
        return getPackagePathErrorRanges(POINT_SPLITTER.split(packagePath).iterator(), packagePath);
    }

    /**
     * resolve java package or class path related directory path error indexes
     * @param packagePath string path
     * @return error part indexes
     */
    private List<Pair<String, TextRange>> getPackagePathErrorRanges(@NotNull Iterator<String> iterable,
                                                                    @NotNull String packagePath) {
        List<Pair<String, TextRange>> result = Lists.newArrayList();
        String temp = "";
        while (iterable.hasNext()) {
            String level = iterable.next();
            temp += level;
            if (result.size() > 0 || findPackage(temp) == null) {
                int index = packagePath.lastIndexOf(level) + 1;
                result.add(Pair.createNonNull(level, TextRange.create(index, index + level.length())));
            }
            temp += ".";
        }
        return result;
    }

    /**
     * search java class with specified scope
     * @param qualifiedName full qualified class name
     * @param scope search scope
     * @return java class obj
     */
    @Nullable
    public PsiClass findClass(final String qualifiedName, final GlobalSearchScope scope) {
        if (qualifiedName == null || scope == null) {
            return null;
        }
        return ApplicationManager.getApplication().runReadAction(new Computable<PsiClass>() {
            @Override
            public PsiClass compute() {
                return javaPsiFacade.findClass(qualifiedName, scope);
            }
        });
    }

    /**
     * search java class with project scope
     * @param qualifiedName full qualified class name
     */
    @Nullable
    public PsiClass findProjectClass(String qualifiedName) {
        return findClass(qualifiedName, GlobalSearchScope.projectScope(project));
    }

    /**
     * search java class with all scope
     * @param qualifiedName full qualified class name
     * @return java class obj
     */
    @Nullable
    public PsiClass findClass(final String qualifiedName) {
        if (qualifiedName == null) {
            return null;
        }
        return ApplicationManager.getApplication().runReadAction(new Computable<PsiClass>() {
            @Override
            public PsiClass compute() {
                return javaPsiFacadeEx.findClass(qualifiedName);
            }
        });
    }

    /**
     * search java package
     * @param qualifiedName full qualified package name
     * @return java package obj
     */
    public PsiPackage findPackage(final String qualifiedName) {
        if (qualifiedName == null) {
            return null;
        }
        return ApplicationManager.getApplication().runReadAction(new Computable<PsiPackage>() {
            @Override
            public PsiPackage compute() {
                return javaPsiFacade.findPackage(qualifiedName);
            }
        });
    }
}
