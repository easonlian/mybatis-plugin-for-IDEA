/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.service;

import com.google.common.collect.Maps;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.mybatis.util.TypeAliasResolver;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Author: jianyu.lin
 * Date: 2016/11/25 Time: 下午6:11
 */
public class TypeAliasService {

    private static final Map<String, Class<?>> DEFAULT_CLASS_MAPPING = Maps.newHashMap();

    static {
        DEFAULT_CLASS_MAPPING.put("string", String.class);
        DEFAULT_CLASS_MAPPING.put("byte", Byte.class);
        DEFAULT_CLASS_MAPPING.put("long", Long.class);
        DEFAULT_CLASS_MAPPING.put("short", Short.class);
        DEFAULT_CLASS_MAPPING.put("int", Integer.class);
        DEFAULT_CLASS_MAPPING.put("integer", Integer.class);
        DEFAULT_CLASS_MAPPING.put("double", Double.class);
        DEFAULT_CLASS_MAPPING.put("float", Float.class);
        DEFAULT_CLASS_MAPPING.put("boolean", Boolean.class);

        DEFAULT_CLASS_MAPPING.put("byte[]", Byte.class);
        DEFAULT_CLASS_MAPPING.put("long[]", Long.class);
        DEFAULT_CLASS_MAPPING.put("short[]", Short.class);
        DEFAULT_CLASS_MAPPING.put("int[]", Integer.class);
        DEFAULT_CLASS_MAPPING.put("integer[]", Integer.class);
        DEFAULT_CLASS_MAPPING.put("double[]", Double.class);
        DEFAULT_CLASS_MAPPING.put("float[]", Float.class);
        DEFAULT_CLASS_MAPPING.put("boolean[]", Boolean.class);

        DEFAULT_CLASS_MAPPING.put("_byte", byte.class);
        DEFAULT_CLASS_MAPPING.put("_long", long.class);
        DEFAULT_CLASS_MAPPING.put("_short", short.class);
        DEFAULT_CLASS_MAPPING.put("_int", int.class);
        DEFAULT_CLASS_MAPPING.put("_integer", int.class);
        DEFAULT_CLASS_MAPPING.put("_double", double.class);
        DEFAULT_CLASS_MAPPING.put("_float", float.class);
        DEFAULT_CLASS_MAPPING.put("_boolean", boolean.class);

        DEFAULT_CLASS_MAPPING.put("_byte[]", Byte.class);
        DEFAULT_CLASS_MAPPING.put("_long[]", Long.class);
        DEFAULT_CLASS_MAPPING.put("_short[]", Short.class);
        DEFAULT_CLASS_MAPPING.put("_int[]", Integer.class);
        DEFAULT_CLASS_MAPPING.put("_integer[]", Integer.class);
        DEFAULT_CLASS_MAPPING.put("_double[]", Double.class);
        DEFAULT_CLASS_MAPPING.put("_float[]", Float.class);
        DEFAULT_CLASS_MAPPING.put("_boolean[]", Boolean.class);

        DEFAULT_CLASS_MAPPING.put("date", Date.class);
        DEFAULT_CLASS_MAPPING.put("decimal", BigDecimal.class);
        DEFAULT_CLASS_MAPPING.put("bigdecimal", BigDecimal.class);
        DEFAULT_CLASS_MAPPING.put("biginteger", BigInteger.class);
        DEFAULT_CLASS_MAPPING.put("object", Object.class);

        DEFAULT_CLASS_MAPPING.put("date[]", Date.class);
        DEFAULT_CLASS_MAPPING.put("decimal[]", BigDecimal.class);
        DEFAULT_CLASS_MAPPING.put("bigdecimal[]", BigDecimal.class);
        DEFAULT_CLASS_MAPPING.put("biginteger[]", BigInteger.class);
        DEFAULT_CLASS_MAPPING.put("object[]", Object.class);

        DEFAULT_CLASS_MAPPING.put("map", Map.class);
        DEFAULT_CLASS_MAPPING.put("hashmap", HashMap.class);
        DEFAULT_CLASS_MAPPING.put("list", List.class);
        DEFAULT_CLASS_MAPPING.put("arraylist", ArrayList.class);
        DEFAULT_CLASS_MAPPING.put("collection", Collection.class);
        DEFAULT_CLASS_MAPPING.put("iterator", Iterator.class);

        DEFAULT_CLASS_MAPPING.put("ResultSet", ResultSet.class);
    }

    private final Project project;

    private TypeAliasService(Project project) {
        this.project = project;
    }

    public static TypeAliasService INSTANCE(Project project) {
        return ServiceManager.getService(project, TypeAliasService.class);
    }

    /**
     * get psiClass through alias
     * @param typeAliasName alias
     * @return mapping class node
     */
    @Nullable
    public PsiClass getAliasClassByName(String typeAliasName) {
        if (StringUtils.isBlank(typeAliasName)) {
            return null;
        }
        if (DEFAULT_CLASS_MAPPING.containsKey(typeAliasName)) {
            return JavaService.getInstance(project).findClass(DEFAULT_CLASS_MAPPING.get(typeAliasName).getName());
        } else if (!typeAliasName.contains(".") && DEFAULT_CLASS_MAPPING.containsKey(typeAliasName.toLowerCase())) {
            return JavaService.getInstance(project).findClass(DEFAULT_CLASS_MAPPING.get(typeAliasName.toLowerCase()).getName());
        } else {
            return JavaService.getInstance(project).findClass(TypeAliasResolver.getAllTypeAlias(project).get(typeAliasName));
        }
    }
}
