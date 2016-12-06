package org.qunar.plugin.mybatis.contributor;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.StandardPatterns;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.qunar.plugin.mybatis.reference.MapperInclude2SqlReference;

/**
 * mapper statement sql reference contributor
 *
 * Author: jianyu.lin
 * Date: 16-11-27 Time: 下午4:39
 */
public class MapperInclude2SqlReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        ElementPattern<XmlAttributeValue> attrValuePattern = XmlPatterns.xmlAttributeValue()
                .withParent(XmlPatterns.xmlAttribute("refid").withParent(XmlPatterns.xmlTag().withName("include")))
                .withValue(StandardPatterns.string().notNull().longerThan(0));
        registrar.registerReferenceProvider(attrValuePattern, new PsiReferenceProvider() {
            @NotNull
            @Override
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                return new PsiReference[]{new MapperInclude2SqlReference((XmlAttributeValue) element)};
            }
        });
    }
}
