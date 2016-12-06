/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.bean.config;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

/**
 * Environments node
 *
 * Author: jianyu.lin
 * Date: 2016/11/21 Time: 下午10:17
 */
public interface Environment extends DomElement {

    @NotNull
    @Attribute("id")
    GenericAttributeValue<String> getId();

    @SubTag("transactionManager")
    TransactionManager getTransactionManager();

    @SubTag("dataSource")
    DataSource getDataSource();
}
