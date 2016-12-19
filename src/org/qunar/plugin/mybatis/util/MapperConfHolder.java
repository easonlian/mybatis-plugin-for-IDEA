/*
 * Copyright (c) 2017 Qunar.com. All Rights Reserved.
 */
package org.qunar.plugin.mybatis.util;

import org.jetbrains.annotations.NotNull;
import org.qunar.plugin.mybatis.bean.mapper.Mapper;
import org.qunar.plugin.util.ConfHolder;

/**
 * Author: jianyu.lin
 * Date: 2016/12/19 Time: 下午6:07
 */
public class MapperConfHolder extends ConfHolder<Mapper> {

    public static final MapperConfHolder INSTANCE = new MapperConfHolder(Mapper.class);

    private MapperConfHolder(@NotNull Class<Mapper> clazz) {
        super(clazz);
    }
}
