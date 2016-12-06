/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.bean.config;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

/**
 * Property node
 *
 * Author: jianyu.lin
 * Date: 2016/11/21 Time: 下午10:17
 */
public interface Property extends DomElement {

    @NotNull
    @Attribute("name")
    GenericAttributeValue<String> getName();

    @NotNull
    @Attribute("value")
    GenericAttributeValue<String> getValue();
}
