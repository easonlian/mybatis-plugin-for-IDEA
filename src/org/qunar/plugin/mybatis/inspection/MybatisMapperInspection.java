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
import org.qunar.plugin.mybatis.bean.mapper.Mapper;
import org.qunar.plugin.mybatis.util.DomElements;
import org.qunar.plugin.mybatis.util.MapperConfHolder;
import org.qunar.plugin.util.Inspections;

import java.util.List;

/**
 * mybatis mapper xml inspection
 * <p>
 * Author: jianyu.lin
 * Date: 2016/11/22 Time: 下午8:13
 */
public class MybatisMapperInspection extends LocalInspectionTool {

    /**
     * valid config file
     * @param file       config file
     * @param manager    inspection manager
     * @param isOnTheFly the error show at the end of line
     * @return potential problems
     */
    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file,
                                         @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (!(DomElements.isMapperXmlFile(file))) {
            return null;
        }
        Mapper mapper = MapperConfHolder.INSTANCE.getMapperDomElement(file);
        if (mapper == null || mapper.getNamespace() == null) {
            return null;
        }

        List<ProblemDescriptor> allProblems = Lists.newArrayList();
        //  validate namespace attribute
        allProblems.addAll(Inspections.buildPsiClassProblems(manager, mapper.getNamespace()));
        //  validate mapper statement nodes
        allProblems.addAll(DomElements.checkMapperDomElement(manager, mapper));
        return allProblems.toArray(new ProblemDescriptor[allProblems.size()]);
    }
}








