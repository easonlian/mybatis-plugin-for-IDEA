/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.intention.statement;

import com.google.common.collect.Sets;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.qunar.plugin.mybatis.bean.mapper.Mapper;
import org.qunar.plugin.mybatis.bean.mapper.Statement;

import java.util.Set;

/**
 * generate insert node in xml
 *
 * Author: jianyu.lin
 * Date: 2016/12/1 Time: 下午8:14
 */
public class InsertGenerateIntention extends StatementGenerateIntention {

    private static final Set<String> KEYWORDS = Sets.newHashSet(
            "insert", "add", "append", "addictive"
    );

    @NotNull
    @Override
    protected Statement generateStatement(@NotNull Mapper mapperDom) {
        return mapperDom.addInsert(mapperDom.getInserts().size());
    }
    
    @NotNull
    @Override
    protected Set<String> getSubGeneratorKeywords() {
        return KEYWORDS;
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Generate Mapper Insert Statement";
    }
}
