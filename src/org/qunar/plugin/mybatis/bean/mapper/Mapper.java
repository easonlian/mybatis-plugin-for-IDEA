/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.bean.mapper;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * mapper root tag
 *
 * Author: jianyu.lin
 * Date: 2016/11/21 Time: 下午3:47
 */
public interface Mapper extends DomElement {

    @Attribute("namespace")
    GenericAttributeValue<PsiClass> getNamespace();

    List<Cache> getCaches();

    List<CacheRef> getCacheRefs();

    @SubTagList("delete")
    List<Delete> getDeletes();
    
    @SubTagList("insert")
    List<Insert> getInserts();

    @SubTagList("select")
    List<Select> getSelects();

    @SubTagList("update")
    List<Update> getUpdates();

    @SubTag("insert")
    Insert addInsert(int index);
    
    @SubTag("select")
    Select addSelect(int index);

    @SubTag("delete")
    Delete addDelete(int index);
    
    @SubTag("update")
    Update addUpdate(int index);

    List<ParameterMap> getParameterMaps();

    @SubTagList("resultMap")
    List<ResultMap> getResultMaps();

    @SubTagList("sql")
    List<Sql> getSqls();
}

