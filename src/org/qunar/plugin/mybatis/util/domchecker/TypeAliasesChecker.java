/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.util.domchecker;

import com.google.common.collect.Lists;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.xml.XmlAttributeValue;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.qunar.plugin.mybatis.bean.config.Package;
import org.qunar.plugin.mybatis.bean.config.TypeAlias;
import org.qunar.plugin.mybatis.bean.config.TypeAliases;
import org.qunar.plugin.util.DomChecker;
import org.qunar.plugin.util.Inspections;
import org.qunar.plugin.util.XmlUtils;

import java.util.List;

/**
 * Author: jianyu.lin
 * Date: 2016/11/30 Time: 下午7:25
 */
public class TypeAliasesChecker implements DomChecker<TypeAliases> {

    /**
     * reference to 'org.apache.ibatis.builder.xml.XMLConfigBuilder#aliasesElement'
     * @param manager     inspection manager
     * @param typeAliasesList typeAliases nodes
     * @return check result
     */
    @Override
    @NotNull
    public List<ProblemDescriptor> check(@NotNull InspectionManager manager, @NotNull List<TypeAliases> typeAliasesList) {
        List<ProblemDescriptor> problemDescriptors = Lists.newArrayList();
        for (TypeAliases typeAliases : typeAliasesList) {
            problemDescriptors.addAll(buildPackageProblems(manager, typeAliases.getPackages()));
            problemDescriptors.addAll(buildTypeAliasProblems(manager, typeAliases.getTypeAlias()));
        }
        return problemDescriptors;
    }

    /**
     * build typeAlias nodes problem descriptor
     *
     * @param manager     inspection manager
     * @param typeAliases package nodes
     * @return problems
     */
    private List<ProblemDescriptor> buildTypeAliasProblems(@NotNull InspectionManager manager,
                                                           @NotNull List<TypeAlias> typeAliases) {
        List<ProblemDescriptor> problemDescriptors = Lists.newArrayList();
        for (TypeAlias typeAlias : typeAliases) {
            problemDescriptors.addAll(Inspections.buildPsiClassProblems(manager, typeAlias.getType()));
        }
        return problemDescriptors;
    }

    /**
     * build package nodes problem descriptor
     *
     * @param manager  inspection manager
     * @param packages package nodes
     * @return problems
     */
    private List<ProblemDescriptor> buildPackageProblems(@NotNull InspectionManager manager,
                                                         @NotNull List<Package> packages) {
        List<ProblemDescriptor> problemDescriptors = Lists.newArrayList();
        for (Package packageItem : packages) {
            PsiPackage psiPackage = XmlUtils.getAttrValue(packageItem.getName());
            if (psiPackage != null) {
                continue;
            }
            XmlAttributeValue xmlAttributeValue = packageItem.getName().getXmlAttributeValue();
            if (xmlAttributeValue == null || StringUtils.isBlank(xmlAttributeValue.getValue())) {
                continue;
            }
            problemDescriptors.addAll(Inspections.buildPackagePathProblems(manager, xmlAttributeValue));
        }
        return problemDescriptors;
    }
}