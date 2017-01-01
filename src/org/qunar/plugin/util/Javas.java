/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.util;

import com.google.common.collect.Sets;
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
     * get simple name with first letter lower
     * @param rawName name
     * @return pretty simple name
     */
    public static String getFirstLowerFileName(@NotNull String rawName) {
        return rawName.substring(0, 1).toLowerCase() + rawName.substring(1, rawName.length());
    }

    /**
     * transform underline to upper letter
     * @param rawName raw string
     * @return transformed string
     */
    public static String transUnderline2UpperLetter(@NotNull String rawName) {
        if (StringUtils.isEmpty(rawName)) {
            return rawName;
        }
        StringBuilder builder = new StringBuilder();
        int len = rawName.length();
        for (int i = 0; i < len; i++) {
            if (rawName.charAt(i) == '_') {
                if (i != len - 1) {
                    builder.append((char) (rawName.charAt(++i) - 32));
                }
            } else {
                builder.append(rawName.charAt(i));
            }
        }
        return builder.toString();
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
