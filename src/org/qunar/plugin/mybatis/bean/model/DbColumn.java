/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.bean.model;

/**
 * Author: jianyu.lin
 * Date: 2016/12/24 Time: 上午12:03
 */
public class DbColumn {

    private String name;
    private String typeName;

    public String getName() {
        return name;
    }

    public DbColumn setName(String name) {
        this.name = name;
        return this;
    }

    public String getTypeName() {
        return typeName;
    }

    public DbColumn setTypeName(String typeName) {
        this.typeName = typeName;
        return this;
    }

    @Override
    public String toString() {
        return "DbColumn{" +
                "name='" + name + '\'' +
                ", typeName='" + typeName + '\'' +
                '}';
    }
}
