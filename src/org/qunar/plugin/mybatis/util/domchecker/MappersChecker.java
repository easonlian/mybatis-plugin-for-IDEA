/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.util.domchecker;

import com.google.common.collect.Lists;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.paths.GlobalPathReferenceProvider;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.GenericAttributeValue;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.qunar.plugin.mybatis.bean.config.Mapper;
import org.qunar.plugin.mybatis.bean.config.Mappers;
import org.qunar.plugin.util.DomChecker;
import org.qunar.plugin.util.Inspections;

import java.util.List;

/**
 * Author: jianyu.lin
 * Date: 2016/11/30 Time: 下午7:22
 */
public class MappersChecker implements DomChecker<Mappers> {

    /**
     * reference to 'org.apache.ibatis.builder.xml.XMLConfigBuilder#mapperElement'
     * @param manager inspection manager
     * @param mappersList mappers nodes
     * @return check result
     */
    @Override
    @NotNull
    public List<ProblemDescriptor> check(@NotNull InspectionManager manager, @NotNull List<Mappers> mappersList) {
        List<ProblemDescriptor> problemDescriptors = Lists.newArrayList();
        for (Mappers mappers : mappersList) {
            for (Mapper mapper : mappers.getMappers()) {
                problemDescriptors.addAll(Inspections.buildPsiClassProblems(manager, mapper.getClazz()));
                problemDescriptors.addAll(buildUrlAttrProblems(manager, mapper.getUrl()));
            }
        }
        return problemDescriptors;
    }

    /**
     * valid resource attribute
     * @param manager      inspection manager
     * @param resourceAttr resource attribute
     * @return problems
     */
    @NotNull
    @SuppressWarnings("unused")
    private List<ProblemDescriptor> buildResourceAttrProblems(@NotNull InspectionManager manager,
                                                              GenericAttributeValue<PsiFile> resourceAttr) {
        if (resourceAttr == null || resourceAttr.getXmlAttribute() == null) {
            return Lists.newArrayList();
        }
        XmlAttributeValue xmlAttrValue = resourceAttr.getXmlAttributeValue();
        if (resourceAttr.getValue() != null || xmlAttrValue == null) {
            return Lists.newArrayList();
        }
        return Inspections.buildClassPathRelatedPathProblems(manager, xmlAttrValue);
    }

    /**
     * valid url attribute
     * @param manager inspection manager
     * @param urlAttr url attribute
     * @return problems
     */
    @NotNull
    private List<ProblemDescriptor> buildUrlAttrProblems(@NotNull InspectionManager manager,
                                                         GenericAttributeValue<String> urlAttr) {
        if (urlAttr == null || StringUtils.isBlank(urlAttr.getValue())) {
            return Lists.newArrayList();
        }
        if (!GlobalPathReferenceProvider.isWebReferenceUrl(urlAttr.getValue())) {
            String errMsg = String.format("Cannot resolve url '%s'", urlAttr.getValue());
            XmlAttributeValue urlAttrValue = urlAttr.getXmlAttributeValue();
            if (urlAttrValue == null) {
                return Lists.newArrayList();
            }
            TextRange textRange = TextRange.create(1, urlAttrValue.getTextLength());
            return Lists.newArrayList(manager.createProblemDescriptor(urlAttrValue, textRange,
                    errMsg, ProblemHighlightType.ERROR, true, LocalQuickFix.EMPTY_ARRAY));
        }
        return Lists.newArrayList();
    }
}
