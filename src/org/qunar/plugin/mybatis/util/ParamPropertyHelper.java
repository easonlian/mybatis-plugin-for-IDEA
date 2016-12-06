/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.util;

import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.FileIndexFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.mybatis.bean.Annotation;
import org.qunar.plugin.service.JavaService;

import java.util.List;
import java.util.Locale;

/**
 * type alias util class
 * 
 * Author: jianyu.lin
 * Date: 2016/12/3 Time: 下午2:43
 */
public class ParamPropertyHelper {

    /**
     * build param lookup elements
     * @param refMethod ref method
     * @return lookup items
     */
    @NotNull
    public static List<PsiElement> buildParamLookupElements(@NotNull PsiMethod refMethod) {
        List<PsiElement> elements = Lists.newArrayList();
        PsiParameter firstParam = null;
        //  add @Param
        for (PsiParameter methodParam : refMethod.getParameterList().getParameters()) {
            firstParam = methodParam;
            PsiModifierList modifierList = methodParam.getModifierList();
            if (modifierList == null) {
                continue;
            }
            PsiAnnotation paramAnnotation = modifierList.findAnnotation(Annotation.Param.qualified);
            if (paramAnnotation == null) {
                continue;
            }
            PsiAnnotationMemberValue valueMember = paramAnnotation.findDeclaredAttributeValue("value");
            if (valueMember == null || StringUtils.isBlank(valueMember.getText())) {
                continue;
            }
            elements.add(valueMember);
        }
        //  parse first parameter
        if (firstParam == null) {
            return elements;
        }
        return buildFirstParamLookupElements(firstParam);
    }

    /**
     * resolve first parameter's all get methods
     * @param firstParam first parameter
     * @return lookup items
     */
    @NotNull
    private static List<PsiElement> buildFirstParamLookupElements(@NotNull PsiParameter firstParam) {
        String qualifiedName = getParameterQualifiedName(firstParam);
        PsiClass paramClass = JavaService.getInstance(firstParam.getProject())
                .findClass(qualifiedName, GlobalSearchScope.allScope(firstParam.getProject()));
        if (paramClass == null) {
            return Lists.newArrayList();
        }
        paramClass = findJavaSourcePsiClass(paramClass);
        return buildFirstParamLookupElements(firstParam, paramClass);
    }

    /**
     * resolve Xyz.class => Xyz.java
     * @param paramClass psi class
     * @return psi class
     */
    @NotNull
    private static PsiClass findJavaSourcePsiClass(@NotNull PsiClass paramClass) {
        Project project = paramClass.getProject();
        if (FileIndexFacade.getInstance(project).isInLibraryClasses(paramClass.getContainingFile().getVirtualFile())) {
            PsiFile[] files = FilenameIndex.getFilesByName(paramClass.getProject(),
                    paramClass.getName() + ".java", ProjectScope.getLibrariesScope(paramClass.getProject()));
            for (PsiFile file : files) {
                if (!(file instanceof PsiJavaFile)) continue;
                PsiJavaFile psiJavaFile = (PsiJavaFile) file;
                for (PsiClass psiClass : psiJavaFile.getClasses()) {
                    if (psiClass.getQualifiedName() != null &&
                            psiClass.getQualifiedName().equals(paramClass.getQualifiedName())) {
                        return psiClass;
                    }
                }
            }
        }
        return paramClass;
    }

    /**
     * fix Issue #2 : primitive class don't has java class reference
     * @param firstParam first param of mapper method
     * @return qualified class name
     */
    @Nullable
    private static String getParameterQualifiedName(@NotNull PsiParameter firstParam) {
        if (firstParam.getTypeElement() == null) {
            return null;
        }
        if (firstParam.getTypeElement().getType() instanceof PsiPrimitiveType) {
            return ((PsiPrimitiveType) firstParam.getType()).getBoxedTypeName();
        } else {
            PsiReference psiReference = firstParam.getTypeElement().getInnermostComponentReferenceElement();
            if (psiReference == null) {
                return null;
            }
            return ((PsiJavaCodeReferenceElement) psiReference).getQualifiedName();
        }
    }

    /**
     * resolve first parameter's all get methods
     * @param paramClass class
     * @return lookup items
     */
    @NotNull
    private static List<PsiElement> buildFirstParamLookupElements(@NotNull PsiParameter firstParam,
                                                                  @Nullable PsiClass paramClass) {
        List<PsiElement> elements = Lists.newArrayList();
        if (firstParam.getNameIdentifier() != null) {
            elements.add(firstParam.getNameIdentifier());
        }
        elements.add(firstParam.getNameIdentifier());
        if (paramClass == null || paramClass.getQualifiedName() == null
                || paramClass.getQualifiedName().startsWith("java")) {
            return elements;
        }
        for (PsiMethod psiMethod : paramClass.getAllMethods()) {
            String propertyName = parseGetterMethod(psiMethod.getName());
            if (propertyName != null || !psiMethod.getName().equals("getClass")) {
                PsiElement element = psiMethod.getNameIdentifier() == null ? psiMethod : psiMethod.getNameIdentifier();
                elements.add(element);
            }
        }
        return elements;
    }

    /**
     * parse getter setting method
     * @param name method name
     * @return property name
     */
    @Nullable
    public static String parseGetterMethod(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else if (name.startsWith("get")) {
            name = name.substring(3);
        } else {
            return null;
        }
        if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }
        return name;
    }
}
