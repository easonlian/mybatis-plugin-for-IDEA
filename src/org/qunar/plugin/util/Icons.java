/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.util;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * all icons used in this plugin
 *
 * Author: jianyu.lin
 * Date: 2016/11/25 Time: 下午7:29
 */
public interface Icons {

    Icon MYBATIS_2_XML_ICON = IconLoader.getIcon("/icons/mybatis/navigateDown.png");
    Icon MYBATIS_2_JAVA_ICON = IconLoader.getIcon("/icons/mybatis/navigateUp.png");
    Icon JAVA_PARAMETER_ICON = IconLoader.getIcon("/nodes/parameter.png");
    Icon JAVA_METHOD_ICON = IconLoader.getIcon("/nodes/method.png");
    Icon XML_TAG_ICON = IconLoader.getIcon("/nodes/tag.png");
    Icon ERROR_ICON = IconLoader.getIcon("/general/error.png");
}
