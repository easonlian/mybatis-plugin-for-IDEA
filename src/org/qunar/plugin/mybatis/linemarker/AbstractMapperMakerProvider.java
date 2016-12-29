/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.linemarker;

import com.google.common.collect.Lists;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.mybatis.bean.mapper.Mapper;
import org.qunar.plugin.mybatis.bean.mapper.Statement;
import org.qunar.plugin.mybatis.util.DomElements;
import org.qunar.plugin.util.XmlUtils;

import java.util.Collection;
import java.util.List;

/**
 * mapper maker abstract super class
 *
 * Author: jianyu.lin
 * Date: 2016/11/25 Time: 下午10:19
 */
abstract class AbstractMapperMakerProvider extends RelatedItemLineMarkerProvider {

    /**
     * one mapper xml deal only once
     * @param elements changed elements
     * @param result marker result
     * @param forNavigation navigation
     */
    @Override
    public void collectNavigationMarkers(@NotNull List<PsiElement> elements,
                                         Collection<? super RelatedItemLineMarkerInfo> result, boolean forNavigation) {
        List<PsiElement> myElements = elements.size() > 0 ? chooseElement(elements) : Lists.<PsiElement>newArrayList();
        super.collectNavigationMarkers(myElements, result, forNavigation);
    }

    /**
     * filter duplicate elements request
     * @param elements changed elements
     * @return choose correct element to build markers
     */
    @NotNull
    protected abstract List<PsiElement> chooseElement(@NotNull List<PsiElement> elements);

    /**
     * build mapping method marks
     * @param mapperDom mapper dom
     * @return mark info list
     */
    List<RelatedItemLineMarkerInfo> buildMethodLineMarkers(Mapper mapperDom) {
        List<RelatedItemLineMarkerInfo> lineMarkerInfoList = Lists.newArrayList();
        for (Statement statement : DomElements.collectStatements(mapperDom)) {
            PsiMethod method = XmlUtils.getAttrValue(statement.getId());
            if (method == null || method.getNameIdentifier() == null) {
                continue;
            }
            RelatedItemLineMarkerInfo lineMarker = buildMethodLineMarker(statement, method);
            if (lineMarker != null) {
                lineMarkerInfoList.add(lineMarker);
            }
        }
        return lineMarkerInfoList;
    }

    /**
     * build single mapping method marks
     * @param statement sql statement
     * @return single line marker
     */
    @Nullable
    protected abstract RelatedItemLineMarkerInfo buildMethodLineMarker(@NotNull Statement statement,
                                                                       @NotNull PsiMethod method);
}
