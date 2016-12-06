/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.inspection;

import com.google.common.collect.Lists;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.mybatis.bean.config.Configuration;
import org.qunar.plugin.mybatis.util.DomElements;
import org.qunar.plugin.mybatis.util.domchecker.MappersChecker;
import org.qunar.plugin.mybatis.util.domchecker.TypeAliasesChecker;
import org.qunar.plugin.service.DomParseService;

import java.util.List;

/**
 * mybatis configuration xml inspection
 * <p>
 * Author: jianyu.lin
 * Date: 2016/11/22 Time: 下午8:13
 */
public class MybatisConfigInspection extends LocalInspectionTool {

    /**
     * valid config file
     *
     * @param file       config file
     * @param manager    inspection manager
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
        DomParseService parseService = DomParseService.INSTANCE(manager.getProject());
        Configuration configuration = parseService.parseDomElementsByFile(file, Configuration.class);
        if (configuration == null) {
            return null;
        }

        List<ProblemDescriptor> allProblems = Lists.newArrayList();
        allProblems.addAll(new TypeAliasesChecker().check(manager, Lists.newArrayList(configuration.getTypeAliases())));
        allProblems.addAll(new MappersChecker().check(manager, Lists.newArrayList(configuration.getMappers())));
        return allProblems.toArray(new ProblemDescriptor[allProblems.size()]);
    }
}








