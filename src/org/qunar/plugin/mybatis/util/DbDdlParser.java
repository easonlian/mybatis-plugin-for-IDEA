/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.util;

import com.intellij.database.model.DasTypedObject;
import com.intellij.database.model.MultiRef;
import com.intellij.sql.psi.SqlColumnDefinition;
import com.intellij.sql.psi.SqlCreateTableStatement;
import com.intellij.sql.psi.SqlTableKeyDefinition;
import org.jetbrains.annotations.NotNull;
import org.qunar.plugin.mybatis.bean.model.DbColumn;
import org.qunar.plugin.mybatis.bean.model.CreateTableDdl;
import org.qunar.plugin.mybatis.bean.model.DbKey;
import org.qunar.plugin.service.JavaService;
import org.qunar.plugin.util.Javas;

/**
 * psi ddl util
 *
 * Author: jianyu.lin
 * Date: 2016/12/24 Time: 上午12:23
 */
public class DbDdlParser {

    /**
     * parse create table ddl
     * @param createTableStatement psi create ddl
     * @return plain model
     */
    public static CreateTableDdl parseCreateTableDdl(@NotNull SqlCreateTableStatement createTableStatement) {
        CreateTableDdl createTableDdl = new CreateTableDdl(createTableStatement.getName());
        for (SqlColumnDefinition columnDefinition : createTableStatement.getDeclaredColumns()) {
            DbColumn dbColumn = new DbColumn();
            dbColumn.setName(columnDefinition.getName());
            dbColumn.setPropertyName(Javas.transUnderline2UpperLetter(columnDefinition.getName()));
            dbColumn.setTypeName(columnDefinition.getDataType().typeName);
            createTableDdl.getColumns().add(dbColumn);
        }
        for (SqlTableKeyDefinition keyDefinition : createTableStatement.getDeclaredKeys()) {
            DbKey dbKey = new DbKey();
            dbKey.setKeyType(DbKey.KeyType.valueOf(keyDefinition.getClass()));
            MultiRef.It<? extends DasTypedObject> it = keyDefinition.getColumnsRef().iterate();
            while (it.hasNext()) {
                dbKey.getRefColumns().add(it.next());
            }
            createTableDdl.getDbKeys().add(dbKey);
        }
        return createTableDdl;
    }
}
