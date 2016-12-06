/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.util;

import com.intellij.util.xml.GenericAttributeValue;

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
    public static <T> T getAttrValue(GenericAttributeValue<T> attributeValue) {
        return attributeValue == null ? null : attributeValue.getValue();
    }
}
