/*
 * Copyright (c) 2017 Qunar.com. All Rights Reserved.
 */
package org.qunar.plugin.mybatis.inspection.config;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.mybatis.bean.config.Configuration;
import org.qunar.plugin.mybatis.util.ConfigConfHolder;
import org.qunar.plugin.mybatis.util.DomElements;

import java.util.List;

/**
 * super class of configuration node inspection
 *
 * Author: jianyu.lin
 * Date: 2016/12/19 Time: 下午6:52
 */
public abstract class AbstractConfigInspection extends LocalInspectionTool {

    /**
     * valid config file
     * @param file config file
     * @param manager inspection manager
     * @param isOnTheFly the error show at the end of line
     * @return potential problems
     */
    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file,
                                         @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (!DomElements.isConfigurationXmlFile(file)) {
            return null;
        }
        Configuration configuration = ConfigConfHolder.INSTANCE.getMapperDomElement(file);
        if (configuration == null) {
            return null;
        }
        List<ProblemDescriptor> allProblems = buildProblems(manager, configuration);
        return allProblems.toArray(new ProblemDescriptor[allProblems.size()]);
    }

    /**
     * build problems
     * @param manager manager
     * @param configuration config dom
     * @return problems
     */
    @NotNull
    protected abstract List<ProblemDescriptor> buildProblems(@NotNull InspectionManager manager,
                                                             @NotNull Configuration configuration);
}
