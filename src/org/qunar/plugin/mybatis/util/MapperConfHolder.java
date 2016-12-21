/*
 * Copyright (c) 2017 Qunar.com. All Rights Reserved.
 */
package org.qunar.plugin.mybatis.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomService;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.qunar.plugin.mybatis.bean.mapper.Mapper;
import org.qunar.plugin.util.ConfHolder;
import org.qunar.plugin.util.XmlUtils;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author: jianyu.lin
 * Date: 2016/12/19 Time: 下午6:07
 */
public class MapperConfHolder extends ConfHolder<Mapper> {

    /* is init mapper xmls */
    private static final AtomicBoolean initCheck = new AtomicBoolean(false);
    public static final MapperConfHolder INSTANCE = new MapperConfHolder(Mapper.class);

    private MapperConfHolder(@NotNull Class<Mapper> clazz) {
        super(clazz);
    }

    /**
     * get all mapping mapper xml
     * @param mapperClass class
     * @return mapper dom elements
     */
    @NotNull
    public Collection<Mapper> getMapperDomElements(@NotNull final PsiClass mapperClass) {
        return ApplicationManager.getApplication().runReadAction(new Computable<Collection<Mapper>>() {
            @Override
            public Collection<Mapper> compute() {
                if (!initCheck.get()) {
                    List<DomFileElement<Mapper>> mapperFiles = DomService.getInstance().getFileElements(Mapper.class,
                            mapperClass.getProject(), GlobalSearchScope.projectScope(mapperClass.getProject()));
                    for (DomFileElement<Mapper> mapperFile : mapperFiles) {
                        PsiClass psiClass = XmlUtils.getAttrValue(mapperFile.getRootElement().getNamespace());
                        if (mapperClass == psiClass) {
                            holder.put(mapperFile.getFile(), mapperFile.getRootElement());
                        }
                    }
                    initCheck.set(true);
                }
                return getMapperDomElementByNamespace(mapperClass.getQualifiedName());
            }
        });
    }

    /**
     * get and cache mapper dom element
     * @return mapper dom element
     */
    @NotNull
    private Collection<Mapper> getMapperDomElementByNamespace(final String namespace) {
        return Collections2.filter(MapperConfHolder.INSTANCE.getAllDomElements(), new Predicate<Mapper>() {
            @Override
            public boolean apply(Mapper mapper) {
                if (mapper.getXmlTag() == null ||
                        !MapperConfHolder.INSTANCE.rootTagName.equals(mapper.getXmlTag().getName())) {
                    // clear and reload cache
                    initCheck.set(false);
                    return false;
                }
                return StringUtils.equals(mapper.getNamespace().getStringValue(), namespace);
            }
        });
    }
}
