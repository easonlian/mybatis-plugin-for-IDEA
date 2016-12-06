/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.bean.mapper;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import org.qunar.plugin.mybatis.bean.IdRefStringDomElement;
import org.qunar.plugin.mybatis.converter.TypeAliasConverter;

/**
 * resultMap xml tag
 *
 * Author: jianyu.lin
 * Date: 2016/11/21 Time: 下午4:15
 */
public interface ResultMap extends IdRefStringDomElement {

    @Convert(TypeAliasConverter.class)
    GenericAttributeValue<PsiClass> getType();
}