/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.bean.model;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Author: jianyu.lin
 * Date: 2016/12/24 Time: 上午12:10
 */
public class DbKey {

    private List<String> refColumns = Lists.newArrayList();
    private KeyType keyType;

    public enum KeyType {
        PRIMARY_KEY("com.intellij.sql.psi.impl.SqlPrimaryKeyDefinitionImpl"),
        UNIQUE_KEY("com.intellij.sql.psi.impl.SqlUniqueKeyDefinitionImpl"),
        ;
        public final String impl;

        KeyType(String impl) {
            this.impl = impl;
        }

        public static KeyType valueOf(Class<?> keyClazz) {
            for (KeyType keyType : KeyType.values()) {
                if (keyType.impl.equals(keyClazz.getName())) {
                    return keyType;
                }
            }
            return null;
        }
    }

    public List<String> getRefColumns() {
        return refColumns;
    }

    public KeyType getKeyType() {
        return keyType;
    }

    public DbKey setKeyType(KeyType keyType) {
        this.keyType = keyType;
        return this;
    }

    @Override
    public String toString() {
        return "DbKey{" +
                "refColumns=" + refColumns +
                ", keyType=" + keyType +
                '}';
    }
}
