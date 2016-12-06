/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.util.domchecker;

import com.google.common.collect.Lists;
import com.intellij.codeInsight.daemon.impl.quickfix.CreateMethodQuickFix;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.xml.XmlAttributeValue;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.mybatis.bean.mapper.Statement;
import org.qunar.plugin.util.DomChecker;
import org.qunar.plugin.util.Javas;

import java.util.List;

/**
 * Author: jianyu.lin
 * Date: 2016/11/30 Time: 下午6:35
 */
abstract class StatementChecker<T extends Statement> implements DomChecker<T> {

    protected final PsiClass mapperClass;

    StatementChecker(@NotNull PsiClass mapperClass) {
        this.mapperClass = mapperClass;
    }

    @Override
    @NotNull
    public List<ProblemDescriptor> check(@NotNull InspectionManager manager,
                                         @NotNull List<T> sqlElements) {
        List<ProblemDescriptor> problems = Lists.newArrayList();
        for (T element : sqlElements) {
            ProblemDescriptor idProblem = checkId(manager, element);
            if (idProblem != null) {
                problems.add(idProblem);
            }
            problems.addAll(checkSpecialNodes(manager, element));
        }
        return problems;
    }

    @Nullable
    private ProblemDescriptor checkId(@NotNull InspectionManager manager,
                                      @NotNull T element) {
        if (element.getId() == null || element.getId().getValue() != null
                || element.getId().getXmlAttributeValue() == null) {
            return null;
        }
        XmlAttributeValue idAttrValue = element.getId().getXmlAttributeValue();
        String methodName = idAttrValue.getValue();
        LocalQuickFix[] quickFixes = LocalQuickFix.EMPTY_ARRAY;
        if (Javas.matchJavaClassMethodName(methodName)) {
            String signature = "public void " + methodName + "()";
            CreateMethodQuickFix quickFix = CreateMethodQuickFix.createFix(mapperClass, signature, null);
            quickFixes = new LocalQuickFix[]{quickFix};
        }
        String errMsg = String.format("Cannot resolve method '%s' in Class %s", methodName, mapperClass.getName());
        TextRange errorRange = StringUtils.isEmpty(methodName) ?
                TextRange.create(0, 2) : TextRange.create(1, idAttrValue.getTextLength() - 1);
        return manager.createProblemDescriptor(idAttrValue,
                errorRange, errMsg, ProblemHighlightType.ERROR, true, quickFixes);
    }

    @NotNull
    @SuppressWarnings("unused")
    protected List<ProblemDescriptor> checkSpecialNodes(@NotNull InspectionManager manager,
                                              @NotNull T sqlElement) {
        return Lists.newArrayList();
    }
}