/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.mybatis.converter.PathFileConverter;

/**
 * classPath related file reference
 *
 * Author: jianyu.lin
 * Date: 2016/11/23 Time: 下午2:19
 */
public class ClassPathRelatedFileReference extends PsiReferenceBase<XmlAttributeValue> implements PsiPolyVariantReference {

    public ClassPathRelatedFileReference(@NotNull XmlAttributeValue element) {
        super(element);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] results = multiResolve(true);
        return results.length > 0 ? results[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        PsiFile relatedFile = PathFileConverter.parseClassPathRelatedFile(myElement.getProject(), myElement.getValue());
        return relatedFile == null ? new ResolveResult[]{} :
                new ResolveResult[]{new PsiElementResolveResult(relatedFile.getOriginalElement())};
    }
}
