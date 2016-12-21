/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.bean.config;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;

/**
 * Configuration node
 *
 * Author: jianyu.lin
 * Date: 2016/11/21 Time: 下午10:17
 */
public interface Configuration extends DomElement {

    //  详见: http://mybatis.org/dtd/mybatis-3-config.dtd
    //  org.apache.ibatis.builder.xml.XMLConfigBuilder

    @SubTag("typeAliases")
    TypeAliases getTypeAliases();

    @SubTag("mappers")
    Mappers getMappers();
}
