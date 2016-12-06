/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.util;

import com.google.common.collect.Lists;
import com.intellij.codeInsight.daemon.quickFix.CreateClassOrPackageFix;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.ClassKind;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.GenericAttributeValue;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.service.JavaService;

import java.util.List;

/**
 * Inspection util class
 *
 * Author: jianyu.lin
 * Date: 2016/11/24 Time: 下午9:50
 */
public class Inspections {

    /**
     * build qualified java class problems
     * @param manager inspection manager
     * @param psiClassAttr java class
     * @return problems
     */
    @NotNull
    public static List<ProblemDescriptor> buildPsiClassProblems(@NotNull InspectionManager manager,
                                                                @Nullable GenericAttributeValue<PsiClass> psiClassAttr) {
        if (psiClassAttr == null || psiClassAttr.getValue() != null) {
            return Lists.newArrayList();
        }
        XmlAttributeValue typeAttributeValue = psiClassAttr.getXmlAttributeValue();
        if (typeAttributeValue == null || StringUtils.isBlank(typeAttributeValue.getValue())) {
            return Lists.newArrayList();
        }
        List<Pair<String, TextRange>> errors = JavaService.getInstance(manager.getProject())
                .getQualifiedClassErrorRanges(typeAttributeValue.getValue());
        List<ProblemDescriptor> problems = Lists.newArrayListWithCapacity(errors.size());
        for (Pair<String, TextRange> error : errors) {
            boolean isClassError = typeAttributeValue.getValue().endsWith(error.getFirst());
            String errMsg = String.format("Cannot resolve %s '%s'", isClassError ? "class" : "package", error.getFirst());
            LocalQuickFix[] quickFixes = LocalQuickFix.EMPTY_ARRAY;
            if (isClassError && Javas.matchJavaClassMethodName(error.getFirst())) {
                PsiPackage existParentPackage = findBasePackage(manager.getProject(), typeAttributeValue.getValue(), errors.size());
                if (existParentPackage != null) {
                    quickFixes = new LocalQuickFix[]{CreateClassOrPackageFix.createFix(
                            typeAttributeValue.getValue(),
                            GlobalSearchScope.projectScope(manager.getProject()),
                            existParentPackage,
                            existParentPackage,
                            ClassKind.INTERFACE, null, null
                    )};
                }
            }
            problems.add(manager.createProblemDescriptor(typeAttributeValue,
                    error.getSecond(), errMsg, ProblemHighlightType.ERROR, true, quickFixes));
        }
        return problems;
    }

    /**
     * build java package path related path problems
     * @param manager inspection manager
     * @param pathAttr path xml attribute value
     * @return problems
     */
    public static List<ProblemDescriptor> buildPackagePathProblems(@NotNull InspectionManager manager,
                                                                   @NotNull XmlAttributeValue pathAttr) {
        String packagePath = pathAttr.getValue();
        List<Pair<String, TextRange>> errors = JavaService.getInstance(manager.getProject()).getPackagePathErrorRanges(packagePath);
        return buildPackagePathProblems(manager, pathAttr, errors);
    }

    /**
     * build class path related path problems
     * @param manager inspection manager
     * @param pathAttr path xml attribute value
     * @return problems
     */
    public static List<ProblemDescriptor> buildClassPathRelatedPathProblems(@NotNull InspectionManager manager,
                                                                            @NotNull XmlAttributeValue pathAttr) {
        String packagePath = pathAttr.getValue();
        List<Pair<String, TextRange>> errors = JavaService.getInstance(manager.getProject()).getClassPathRelatedPathErrorRanges(packagePath);
        return buildPackagePathProblems(manager, pathAttr, errors);
    }

    private static List<ProblemDescriptor> buildPackagePathProblems(@NotNull InspectionManager manager,
                                                                    XmlAttributeValue pathAttr,
                                                                    List<Pair<String, TextRange>> errors) {
        List<ProblemDescriptor> problemDescriptors = Lists.newArrayList();
        for (Pair<String, TextRange> error : errors) {
            String errMsg = String.format("Cannot resolve package '%s'", error.getFirst());
            problemDescriptors.add(manager.createProblemDescriptor(pathAttr, error.getSecond(),
                    errMsg, ProblemHighlightType.ERROR, true, LocalQuickFix.EMPTY_ARRAY));
        }
        return problemDescriptors;
    }

    /**
     * find base package of qualified class
     * @param qualifiedName qualified class name
     * @return package
     */
    private static PsiPackage findBasePackage(@NotNull Project project,
                                              @NotNull String qualifiedName, int errorCount) {
        String[] parts = qualifiedName.split("\\.");
        int len = parts.length - errorCount;
        StringBuilder pathBuilder = new StringBuilder("");
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                pathBuilder.append(".");
            }
            pathBuilder.append(parts[i]);
        }
        return JavaService.getInstance(project).findPackage(pathBuilder.toString());
    }
}
