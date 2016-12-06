/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.bean.config;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * Configuration node
 *
 * Author: jianyu.lin
 * Date: 2016/11/21 Time: 下午10:17
 */
public interface Configuration extends DomElement {

    //  详见: http://mybatis.org/dtd/mybatis-3-config.dtd
    //  org.apache.ibatis.builder.xml.XMLConfigBuilder

    @SubTag("settings")
    Settings getSettings();

    @SubTagList("properties")
    Properties getProperties();

    @SubTag("typeAliases")
    TypeAliases getTypeAliases();

    @SubTag("typeHandlers")
    TypeHandlers getTypeHandlers();

    @SubTag("objectFactory")
    ObjectFactory getObjectFactory();

    @SubTag("objectFactory")
    ObjectWrapperFactory getObjectWrapperFactory();

    @SubTag("reflectorFactory")
    ReflectorFactory getReflectorFactory();

    @SubTag("plugins")
    Plugins getPlugins();

    @SubTag("environments")
    Environments getEnvironments();

    @SubTagList("databaseIdProvider")
    List<Property> getDatabaseIdProvider();

    @SubTag("mappers")
    Mappers getMappers();
}
