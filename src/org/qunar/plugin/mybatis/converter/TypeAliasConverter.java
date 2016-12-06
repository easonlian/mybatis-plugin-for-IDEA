/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.converter;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.PsiClassConverter;
import org.jetbrains.annotations.NotNull;
import org.qunar.plugin.mybatis.reference.TypeAliasNameReference;
import org.qunar.plugin.service.JavaService;
import org.qunar.plugin.service.TypeAliasService;

/**
 * sql node parameter type attribute converter
 *
 * Author: jianyu.lin
 * Date: 2016/11/25 Time: 下午2:30
 */
public class TypeAliasConverter extends PsiClassConverter implements CustomReferenceConverter<PsiClass> {

    @Override
    public PsiClass fromString(String parameterType, ConvertContext context) {
        if (parameterType.contains(".")) {
            return super.fromString(parameterType, context);
        }
        PsiClass qualifiedClass = JavaService.getInstance(context.getProject()).findClass(parameterType);
        return qualifiedClass != null ? qualifiedClass :
                TypeAliasService.INSTANCE(context.getProject()).getAliasClassByName(parameterType);
    }

    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue<PsiClass> genericDomValue,
                                           PsiElement element, ConvertContext context) {
        XmlAttributeValue attributeValue = (XmlAttributeValue) element;
        if (attributeValue.getValue() == null || attributeValue.getValue().contains(".")) {
            return super.createReferences(genericDomValue, element, context);
        }
        return new PsiReference[]{new TypeAliasNameReference(attributeValue)};
    }
}
