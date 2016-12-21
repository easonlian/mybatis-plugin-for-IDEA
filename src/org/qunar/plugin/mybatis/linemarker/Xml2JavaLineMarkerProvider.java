/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.linemarker;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.mybatis.bean.mapper.Mapper;
import org.qunar.plugin.mybatis.bean.mapper.Statement;
import org.qunar.plugin.mybatis.util.DomElements;
import org.qunar.plugin.mybatis.util.MapperConfHolder;
import org.qunar.plugin.util.Icons;

import java.util.Collection;

/**
 * navigate from mapper xml 2 java interface
 *
 * Author: jianyu.lin
 * Date: 2016/11/25 Time: 下午7:08
 */
public class Xml2JavaLineMarkerProvider extends AbstractMapperMakerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element,
                                            Collection<? super RelatedItemLineMarkerInfo> result) {
        if (!(element instanceof XmlFile) || !DomElements.isMapperXmlFile(element.getContainingFile())) {
            return;
        }

        Mapper mapperDom = MapperConfHolder.INSTANCE.getDomElement(element.getContainingFile());
        if (mapperDom == null || mapperDom.getNamespace() == null
                || mapperDom.getNamespace().getValue() == null) {
            return;
        }

        //  mark mapping mapper tag
        PsiClass mapperClass = mapperDom.getNamespace().getValue();
        NavigationGutterIconBuilder<PsiElement> builder =
                NavigationGutterIconBuilder.create(Icons.MYBATIS_2_JAVA_ICON)
                        .setTarget(mapperClass).setTooltipText("Navigate to mapper java interface: " + mapperClass.getName());
        result.add(builder.createLineMarkerInfo(mapperDom.getXmlTag()));

        //  mark mapping statement tag
        result.addAll(buildMethodLineMarkers(mapperDom));
    }

    /**
     * {@inheritDoc}
     * @param statement sql statement
     * @return marker
     */
    @Nullable
    @Override
    protected RelatedItemLineMarkerInfo buildMethodLineMarker(@NotNull Statement statement,
                                                              @NotNull PsiMethod method) {
        NavigationGutterIconBuilder<PsiElement> builder =
                NavigationGutterIconBuilder.create(Icons.MYBATIS_2_JAVA_ICON).setTarget(method.getNameIdentifier())
                        .setTooltipText("Navigate to mapper java method: " + method.getName());
        return builder.createLineMarkerInfo(statement.getXmlTag());
    }
}
