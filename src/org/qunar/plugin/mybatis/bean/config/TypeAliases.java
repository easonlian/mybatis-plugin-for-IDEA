/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.bean.config;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * TypeAliases node
 *
 * Author: jianyu.lin
 * Date: 2016/11/21 Time: 下午10:17
 */
public interface TypeAliases extends DomElement {

    @SubTagList("typeAlias")
    List<TypeAlias> getTypeAlias();

    @SubTagList("package")
    List<Package> getPackages();
}
