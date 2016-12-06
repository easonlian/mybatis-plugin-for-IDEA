/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.dubbo.contributor;

import com.google.common.collect.Lists;
import com.intellij.openapi.util.TextRange;
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
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.qunar.plugin.dubbo.reference.PropertyHolderReference;

import java.util.List;

/**
 * property holder reference contributor
 *
 * Author: jianyu.lin
 * Date: 2016/12/4 Time: 下午3:26
 */
public class PropertyHolderReferenceContributor extends PsiReferenceContributor {

    private static final String[] TAG_NAMES = new String[]{"dubbo:provider", "dubbo:registry", "dubbo:service", "dubbo:reference"};
    private static final String PATTERN_STRING = "\\$\\{[a-zA-Z0-9_.-]*\\}";

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                XmlPatterns.xmlTag().withName(TAG_NAMES).withParent(XmlPatterns.xmlTag().withName("beans")),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element,
                                                                 @NotNull ProcessingContext context) {
                        XmlTag dubboTag = (XmlTag) element;
                        List<PsiReference> psiReferences = Lists.newArrayList();
                        for (XmlAttribute attr : dubboTag.getAttributes()) {
                            if (StringUtils.isBlank(attr.getValue())
                                    || attr.getValueElement() == null
                                    || !attr.getValue().matches(PATTERN_STRING)) {
                                continue;
                            }
                            XmlAttributeValue attrValue = attr.getValueElement();
                            psiReferences.add(new PropertyHolderReference(attrValue, TextRange.create(3, attrValue.getTextLength() - 2)));
                        }
                        return psiReferences.toArray(new PsiReference[psiReferences.size()]);
                    }
                });
    }
}
