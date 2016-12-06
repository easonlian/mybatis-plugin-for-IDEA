/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.qschedule.reference;

import com.google.common.collect.Lists;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.spring.model.SpringBeanPointer;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.qschedule.util.QscheduleHelper;
import org.qunar.plugin.util.Springs;

import java.util.List;

/**
 * qschedule method属性关联及自动提示
 *
 * Author: jianyu.lin
 * Date: 2016/11/19 Time: 下午10:23
 */
public class QscheduleMethodAttrReference extends PsiReferenceBase<XmlAttributeValue> {

    /* qschedule config attributes */
    private static final String REF_ATTRIBUTE = "ref";
    /* qschedule default method name */
    private static final String DEFAULT_METHOD_NAME = "run";

    private final XmlTag taskTag;

    public QscheduleMethodAttrReference(@NotNull XmlAttributeValue element) {
        super(element);
        this.taskTag = ((XmlAttribute) myElement.getParent()).getParent();
    }

    /**
     * 解析关联属性
     * @return 1:1关联关系
     */
    @Nullable
    @Override
    public PsiElement resolve() {
        String refAttrPlanValue = taskTag.getAttributeValue(REF_ATTRIBUTE);
        SpringBeanPointer springBean = Springs.findSpringBeanByName(myElement.getProject(), refAttrPlanValue);
        if (springBean == null || springBean.getBeanClass() == null) {
            return null;
        }
        return QscheduleHelper.findRefMatchedMethod(springBean.getBeanClass(), myElement.getValue());
    }

    /**
     * 获取自动提示属性
     * @return 提示项
     */
    @NotNull
    @Override
    public Object[] getVariants() {
        String refAttrValue = taskTag.getAttributeValue(REF_ATTRIBUTE);
        SpringBeanPointer springBean = Springs.findSpringBeanByName(myElement.getProject(), refAttrValue);
        if (springBean == null) {
            return new Object[0];
        }
        List<PsiMethod> psiMethods = QscheduleHelper.findFixMatchedMethod(springBean.getBeanClass());
        List<LookupElement> variants = Lists.newArrayList();
        for (PsiMethod psiMethod : psiMethods) {
            if (!StringUtils.equals(DEFAULT_METHOD_NAME, psiMethod.getName())) {
                variants.add(LookupElementBuilder.create(psiMethod));
            }
        }
        return variants.toArray();
    }
}
