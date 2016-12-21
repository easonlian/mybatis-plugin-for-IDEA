/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.bean.mapper;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.TagValue;
import org.qunar.plugin.mybatis.bean.IdRefMethodDomElement;
import org.qunar.plugin.mybatis.converter.TypeAliasConverter;

/**
 * create mapper sql super class
 *
 * Author: jianyu.lin
 * Date: 2016/11/25 Time: 上午11:11
 */
public interface Statement extends IdRefMethodDomElement {

    @Convert(TypeAliasConverter.class)
    @Attribute("parameterType")
    GenericAttributeValue<PsiClass> getParameterType();
    
    @TagValue
    void setStringValue(@SuppressWarnings("SameParameterValue") String s);
}
