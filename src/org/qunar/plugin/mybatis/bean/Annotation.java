/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.bean;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * represent mybatis annotation
 *
 * Author: jianyu.lin
 * Date: 2016/11/28 Time: 下午1:29
 */
public enum Annotation {

    Param("org.apache.ibatis.annotations.Param"),
    Insert("org.apache.ibatis.annotations.Insert"),
    Select("org.apache.ibatis.annotations.Select"),
    Delete("org.apache.ibatis.annotations.Delete"),
    Update("org.apache.ibatis.annotations.Update"),
    ;

    Annotation(String qualified) {
        this.qualified = qualified;
    }

    public final String qualified;

    public static Set<String> getStatementNames() {
        return Sets.newHashSet(
                Insert.name().toLowerCase(),
                Select.name().toLowerCase(),
                Delete.name().toLowerCase(),
                Update.name().toLowerCase()
        );
    }
}
