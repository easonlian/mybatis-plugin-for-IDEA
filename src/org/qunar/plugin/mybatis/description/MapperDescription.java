/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.description;

import com.intellij.util.xml.DomFileDescription;
import org.qunar.plugin.mybatis.bean.mapper.Mapper;
import org.qunar.plugin.mybatis.util.DomElements;

/**
 * mybatis mapper xml description
 *
 * Author: jianyu.lin
 * Date: 2016/11/22 Time: 上午12:17
 */
public class MapperDescription extends DomFileDescription<Mapper> {

    public MapperDescription() {
        super(Mapper.class, DomElements.MAPPER_ROOT_TAG_NAME);
    }
}
