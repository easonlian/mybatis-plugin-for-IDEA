/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.inspection.config;

import com.google.common.collect.Lists;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import org.jetbrains.annotations.NotNull;
import org.qunar.plugin.mybatis.bean.config.Configuration;
import org.qunar.plugin.mybatis.util.domchecker.TypeAliasesChecker;

import java.util.List;

/**
 * mybatis configuration xml inspection
 * <p>
 * Author: jianyu.lin
 * Date: 2016/11/22 Time: 下午8:13
 */
public class TypeAliasesInspection extends AbstractConfigInspection {

    /**
     * {@inheritDoc}
     * @param manager manager
     * @param configuration config dom
     * @return problems
     */
    @NotNull
    @Override
    protected List<ProblemDescriptor> buildProblems(@NotNull InspectionManager manager,
                                                    @NotNull Configuration configuration) {
        return new TypeAliasesChecker().check(manager, Lists.newArrayList(configuration.getTypeAliases()));
    }
}








