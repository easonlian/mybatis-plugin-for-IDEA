/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.bean.model;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * mapper xml data obj
 *
 * Author: jianyu.lin
 * Date: 2016/12/23 Time: 下午11:45
 */
public class CreateTableDdl {

    private List<DbColumn> columns = Lists.newArrayList();
    private List<DbForeignKey> foreignKeys = Lists.newArrayList();
    private List<DbIndex> dbIndices = Lists.newArrayList();
    private List<DbKey> dbKeys = Lists.newArrayList();

    public List<DbColumn> getColumns() {
        return columns;
    }

    public List<DbForeignKey> getForeignKeys() {
        return foreignKeys;
    }

    public List<DbIndex> getDbIndices() {
        return dbIndices;
    }

    public List<DbKey> getDbKeys() {
        return dbKeys;
    }

    @Override
    public String toString() {
        return "CreateTableDdl{" +
                "columns=" + columns +
                ", foreignKeys=" + foreignKeys +
                ", dbIndices=" + dbIndices +
                ", dbKeys=" + dbKeys +
                '}';
    }
}
