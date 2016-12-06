/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.util.domchecker;

import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;
import org.qunar.plugin.mybatis.bean.mapper.Insert;

/**
 * Author: jianyu.lin
 * Date: 2016/11/30 Time: 下午6:37
 */
public class InsertChecker extends StatementChecker<Insert> {

    public InsertChecker(@NotNull PsiClass mapperClass) {
        super(mapperClass);
    }
}