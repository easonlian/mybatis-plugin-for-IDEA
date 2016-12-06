/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.converter;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.scope.processor.MethodResolveProcessor;
import com.intellij.psi.xml.XmlTag;
import com.intellij.spring.model.converters.PsiMethodConverter;
import com.intellij.util.xml.ConvertContext;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.service.JavaService;

/**
 * take advantages of <code>PsiMethodConverter</code>
 * to parse id attribute of mybatis mapper xml's sub tag
 * <br/>
 * Author: jianyu.lin
 * Date: 2016/11/25 Time: 上午1:55
 * @since v1.2.2
 */
public class MapperMethodConverter extends PsiMethodConverter {

    @Override
    protected PsiMethod[] getMethodCandidates(String methodIdentificator, PsiClass psiClass) {
        return MethodResolveProcessor.findMethod(psiClass, methodIdentificator);
    }

    @Nullable
    @Override
    protected PsiClass getPsiClass(ConvertContext context) {
        XmlTag rootTag = context.getFile().getRootTag();
        if (rootTag == null || rootTag.getAttribute("namespace") == null) {
            return null;
        }
        String qualifiedMapperClass = rootTag.getAttributeValue("namespace");
        return JavaService.getInstance(context.getProject()).findProjectClass(qualifiedMapperClass);
    }

    @Override
    protected String getMethodIdentificator(PsiMethod method) {
        return method.getName();
    }
}
