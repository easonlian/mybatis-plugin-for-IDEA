/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.contributor;

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
import org.qunar.plugin.mybatis.reference.ClassPathRelatedFileReference;
import org.qunar.plugin.mybatis.util.DomElements;


/**
 * related file path reference contributor
 *
 * Author: jianyu.lin
 * Date: 2016/11/23 Time: 下午2:39
 */
public class RelatedFilePathReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        ElementPattern<XmlTag> rootTagPattern = XmlPatterns.xmlTag().withName(DomElements.CONFIG_ROOT_TAG_NAME);
        ElementPattern<XmlTag> mappersTagPattern = XmlPatterns.xmlTag().withName("mappers").withParent(rootTagPattern);
        ElementPattern<XmlTag> mapperTagPattern = XmlPatterns.xmlTag().withName("mapper").withParent(mappersTagPattern);
        ElementPattern<XmlAttribute> methodAttrPattern = XmlPatterns.xmlAttribute("resource").withParent(mapperTagPattern);
        registrar.registerReferenceProvider(XmlPatterns.xmlAttributeValue()
                .notEmpty().withParent(methodAttrPattern), new PsiReferenceProvider() {
            @NotNull
            @Override
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                XmlAttributeValue resourceAttrValue = (XmlAttributeValue) element;
                return new PsiReference[]{new ClassPathRelatedFileReference(resourceAttrValue)};
            }
        });
    }
}
