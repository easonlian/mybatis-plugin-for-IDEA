/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.dubbo.reference;

import com.google.common.collect.Sets;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.spring.model.SpringBeanPointer;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.util.Springs;

import java.util.Set;

/**
 * add dubbo spring config ${aa.bb} reference to spring place holder and properties file<br>
 * for example:<br>
 * &lt;dubbo:registry address="${supplier.dubbo.address}" ...<br>
 * &lt;dubbo:provider threads="${supplier.dubbo.count}" ...<br>
 *
 * Author: jianyu.lin
 * Date: 2016/12/4 Time: 下午3:17
 */
public class PropertyHolderReference extends PsiReferenceBase<XmlAttributeValue> implements PsiPolyVariantReference {

    private static final String SPRING_PLACE_HOLDER_CLASS = "org.springframework.beans.factory.config.PropertyPlaceholderConfigurer";

    private final String refText;

    public PropertyHolderReference(@NotNull XmlAttributeValue element, @NotNull TextRange innerRange) {
        super(element, innerRange);
        refText = element.getText().substring(innerRange.getStartOffset(), innerRange.getEndOffset());
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] multiResult = multiResolve(true);
        return multiResult.length > 0 ? multiResult[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Set<PsiElementResolveResult> resolveResults = Sets.newHashSet();

        Set<SpringBeanPointer> placeHolderBeans = Springs
                .findSpringBeansByType(myElement.getProject(), SPRING_PLACE_HOLDER_CLASS);
        for (SpringBeanPointer placeHolderBean : placeHolderBeans) {
            if (placeHolderBean.getPsiElement() == null) continue;
            PsiElement psiElement = placeHolderBean.getPsiElement().getNavigationElement();
            if (psiElement == null || !(psiElement instanceof XmlTag)) continue;
            XmlAttribute locationAttr = ((XmlTag) psiElement).getAttribute("location");
            if (locationAttr == null || locationAttr.getValueElement() == null) continue;
            XmlAttributeValue locationAttrValue = locationAttr.getValueElement();
            PsiReference[] references = locationAttrValue.getReferences();

            for (PsiReference reference : references) {
                if (reference instanceof FileReference) {
                    for (ResolveResult resolveResult : ((FileReference) reference).multiResolve(true)) {
                        if (!(resolveResult.getElement() instanceof PsiFile)
                                || ((PsiFile) resolveResult.getElement()).isDirectory()
                                || !StringUtils.equals("Properties", resolveResult.getElement().getLanguage().getDisplayName())) continue;
                        PsiFile propertiesFile = (PsiFile) resolveResult.getElement();
                        if (propertiesFile.getChildren().length == 0) continue;
                        PsiElement[] elements = propertiesFile.getChildren()[0].getChildren();
                        for (PsiElement element : elements) {
                            if (element.getText().startsWith(refText + "=")) {
                                resolveResults.add(new PsiElementResolveResult(element));
                            }
                        }
                    }
                }
            }
        }
        return resolveResults.toArray(new ResolveResult[resolveResults.size()]);
    }
}
