/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.util;

import com.google.common.collect.Sets;
import com.intellij.psi.PsiClass;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * java or class utils
 *
 * Author: jianyu.lin
 * Date: 2016/11/25 Time: 上午11:28
 */
public class Javas {

    /* java method name pattern */
    private static final String JAVA_METHOD_NAME_REGEX = "^[A-Za-z_$][A-Za-z_$\\d]*";
    /* this method names which aren't completing */
    private static final Set<String> EXCLUDE_METHOD_NAMES = Sets.newHashSet("Object");

    static {
        Method[] methods = Object.class.getDeclaredMethods();
        for (Method method : methods) {
            EXCLUDE_METHOD_NAMES.add(method.getName());
        }
    }

    /**
     * get simple class name with first letter lower
     * @param psiClass psi class
     * @return pretty simple name
     */
    public static String getFirstLowerClassName(@NotNull PsiClass psiClass) {
        return getFirstLowerFileName(psiClass.getName());
    }

    /**
     * get simple name with first letter lower
     * @param rawName name
     * @return pretty simple name
     */
    public static String getFirstLowerFileName(@NotNull String rawName) {
        return rawName.substring(0, 1).toLowerCase() + rawName.substring(1, rawName.length());
    }

    /**
     * check a string matches java method name rules
     * @param methodName method name string
     * @return check result
     */
    public static boolean matchJavaClassMethodName(@Nullable String methodName) {
        return StringUtils.isNotBlank(methodName) && methodName.matches(JAVA_METHOD_NAME_REGEX);
    }
}
