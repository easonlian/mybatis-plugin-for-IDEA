/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.bean.mapper;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.converters.values.BooleanValueConverter;

/**
 * insert xml tag
 *
 * Author: jianyu.lin
 * Date: 2016/11/21 Time: 下午4:15
 */
public interface Insert extends Statement {

    @Convert(BooleanValueConverter.class)
    @Attribute("getUseGeneratedKeys")
    GenericAttributeValue<Boolean> getUseGeneratedKeys();
}