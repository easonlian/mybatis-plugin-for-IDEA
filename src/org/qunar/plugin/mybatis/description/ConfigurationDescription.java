/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.description;

import com.intellij.util.xml.DomFileDescription;
import org.qunar.plugin.mybatis.bean.config.Configuration;
import org.qunar.plugin.mybatis.util.ConfigConfHolder;

/**
 * mybatis configuration xml description
 *
 * Author: jianyu.lin
 * Date: 2016/11/22 Time: 上午12:17
 */
public class ConfigurationDescription extends DomFileDescription<Configuration> {

    public ConfigurationDescription() {
        super(Configuration.class, ConfigConfHolder.INSTANCE.rootTagName);
    }
}
