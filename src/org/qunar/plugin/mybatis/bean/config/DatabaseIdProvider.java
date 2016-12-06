/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.bean.config;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * DatabaseIdProvider node
 *
 * Author: jianyu.lin
 * Date: 2016/11/21 Time: 下午10:17
 */
public interface DatabaseIdProvider extends DomElement {

    @Attribute("type")
    GenericAttributeValue<PsiClass> getType();

    @SubTagList("property")
    List<Property> getProperties();
}
