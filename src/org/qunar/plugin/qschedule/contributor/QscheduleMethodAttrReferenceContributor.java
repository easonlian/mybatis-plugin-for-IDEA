/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.qschedule.contributor;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.qunar.plugin.qschedule.reference.QscheduleMethodAttrReference;

/**
 * qschedule method属性关联注册器
 *
 * Author: jianyu.lin
 * Date: 2016/11/20 Time: 上午12:19
 */
public class QscheduleMethodAttrReferenceContributor extends PsiReferenceContributor {

    /* qschedule task tag name */
    private static final String TASK_TAG_NAME = "qschedule:task";
    /* qschedule config attributes */
    private static final String METHOD_ATTRIBUTE = "method";

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        ElementPattern<XmlTag> taskTagPattern = XmlPatterns.xmlTag().withName(TASK_TAG_NAME);
        ElementPattern<XmlAttribute> methodAttrPattern = XmlPatterns.xmlAttribute(METHOD_ATTRIBUTE).withParent(taskTagPattern);
        registrar.registerReferenceProvider(XmlPatterns.xmlAttributeValue().notEmpty().withParent(methodAttrPattern),
                new PsiReferenceProvider() {

                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        return new PsiReference[]{new QscheduleMethodAttrReference((XmlAttributeValue) element)};
                    }
                });
    }
}
