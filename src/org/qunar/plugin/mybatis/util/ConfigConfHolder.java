/*
 * Copyright (c) 2017 Qunar.com. All Rights Reserved.
 */
package org.qunar.plugin.mybatis.util;

import org.jetbrains.annotations.NotNull;
import org.qunar.plugin.mybatis.bean.config.Configuration;
import org.qunar.plugin.util.ConfHolder;

/**
 * Author: jianyu.lin
 * Date: 2016/12/19 Time: 下午6:07
 */
public class ConfigConfHolder extends ConfHolder<Configuration> {

    public static final ConfigConfHolder INSTANCE = new ConfigConfHolder(Configuration.class);

    private ConfigConfHolder(@NotNull Class<Configuration> clazz) {
        super(clazz);
    }
}
