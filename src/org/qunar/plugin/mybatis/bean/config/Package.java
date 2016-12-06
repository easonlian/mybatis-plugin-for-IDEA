/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.bean.config;

import com.intellij.psi.PsiPackage;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.PsiPackageConverter;
import org.jetbrains.annotations.NotNull;

/**
 * TypeAliases node
 *
 * Author: jianyu.lin
 * Date: 2016/11/21 Time: 下午10:17
 */
public interface Package extends DomElement {

    @NotNull
    @Attribute("name")
    @Convert(value = PsiPackageConverter.class)
    GenericAttributeValue<PsiPackage> getName();
}
