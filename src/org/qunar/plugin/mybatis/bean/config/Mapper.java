/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.bean.config;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.mybatis.converter.PathFileConverter;

/**
 * Mapper node
 *
 * Author: jianyu.lin
 * Date: 2016/11/21 Time: 下午10:17
 */
public interface Mapper extends DomElement {

    @Nullable
    @Convert(value = PathFileConverter.class)
    GenericAttributeValue<PsiFile> getResource();

    @Nullable
    GenericAttributeValue<String> getUrl();

    @Nullable
    @Attribute("class")
    GenericAttributeValue<PsiClass> getClazz();
}
