/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.Nullable;

/**
 * Author: jianyu.lin
 * Date: 2016/11/25 Time: 上午1:24
 */
public class XmlUtils {

    /**
     * get attribute value
     * @param attributeValue attribute object
     * @param <T> value type
     * @return value type instance
     */
    @Nullable
    public static <T> T getAttrValue(final GenericAttributeValue<T> attributeValue) {
        return ApplicationManager.getApplication().runReadAction(new Computable<T>() {
            @Override
            public T compute() {
                return attributeValue == null ? null : attributeValue.getValue();
            }
        });
    }
}
