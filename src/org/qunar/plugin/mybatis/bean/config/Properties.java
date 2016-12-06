/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.bean.config;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Property node
 *
 * Author: jianyu.lin
 * Date: 2016/11/21 Time: 下午10:17
 */
public interface Properties extends DomElement {

    @Nullable
    @Attribute("resource")
    GenericAttributeValue<String> getResource();

    @Nullable
    @Attribute("url")
    GenericAttributeValue<String> getUrl();

    @SubTagList("property")
    List<Property> getProperties();
}
