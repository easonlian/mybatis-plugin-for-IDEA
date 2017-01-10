/*
 * Copyright (c) 2017 Qunar.com. All Rights Reserved.
 */
package org.qunar.plugin.mybatis.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomService;
import org.jetbrains.annotations.NotNull;
import org.qunar.plugin.mybatis.bean.mapper.Mapper;
import org.qunar.plugin.util.ConfHolder;
import org.qunar.plugin.util.XmlUtils;

import java.util.Collection;
import java.util.List;

/**
 * mapper dom cache util
 *
 * Author: jianyu.lin
 * Date: 2016/12/19 Time: 下午6:07
 */
public class MapperConfHolder extends ConfHolder<Mapper> {

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
                if (getAllDomElements().isEmpty()) {
                    initAllMappers(mapperClass.getProject());
                }
                Collection<Mapper> mappers = filterMapperDom(getAllDomElements(), mapperClass);
                if (mappers.isEmpty()) {
                    initAllMappers(mapperClass.getProject());
                }
                return filterMapperDom(getAllDomElements(), mapperClass);
            }
        });
    }

    /**
     * filter mapper dom
     * @param allDocuments 所有mapper dom
     * @param targetClass mapper class
     * @return mapping mappers
     */
    private Collection<Mapper> filterMapperDom(Collection<Mapper> allDocuments, final PsiClass targetClass) {
        return Collections2.filter(allDocuments, new Predicate<Mapper>() {
            @Override
            public boolean apply(final Mapper mapper) {
                return ApplicationManager.getApplication().runReadAction(new Computable<Boolean>() {
                    @Override
                    public Boolean compute() {
                        if (mapper.getXmlTag() == null || !rootTagName.equals(mapper.getXmlTag().getName())) {
                            return false;
                        }
                        mapper.getNamespace();
                        PsiClass psiClass = XmlUtils.getAttrValue(mapper.getNamespace());
                        return psiClass == targetClass;
                    }
                });
            }
        });
    }

    /**
     * reload all mappers
     * @param project current project
     */
    private void initAllMappers(Project project) {
        List<DomFileElement<Mapper>> mapperFiles = DomService.getInstance()
                .getFileElements(Mapper.class, project, GlobalSearchScope.projectScope(project));
        for (DomFileElement<Mapper> mapperFile : mapperFiles) {
            PsiClass psiClass = XmlUtils.getAttrValue(mapperFile.getRootElement().getNamespace());
            if (psiClass != null) {
                holder.put(mapperFile.getFile(), mapperFile.getRootElement());
            }
        }
    }
}
