/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.util.domchecker;

import com.intellij.psi.PsiClass;
import org.qunar.plugin.mybatis.bean.mapper.Delete;

/**
 * Author: jianyu.lin
 * Date: 2016/11/30 Time: 下午6:38
 */
public class DeleteChecker extends StatementChecker<Delete> {

    public DeleteChecker(PsiClass mapperClass) {
        super(mapperClass);
    }
}