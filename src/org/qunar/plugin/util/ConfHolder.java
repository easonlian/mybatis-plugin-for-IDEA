/*
 * Copyright (c) 2017 Qunar.com. All Rights Reserved.
 */
package org.qunar.plugin.util;

import com.google.common.collect.Maps;
import com.intellij.psi.PsiFile;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.mybatis.bean.mapper.Mapper;
import org.qunar.plugin.service.DomParseService;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * cache config xml files of framework
 *
 * Author: jianyu.lin
 * Date: 2016/12/19 Time: 下午5:26
 */
public abstract class ConfHolder<T extends DomElement> {

    private final Map<PsiFile, T> holder = Maps.newConcurrentMap();
    private final AtomicBoolean initCheck = new AtomicBoolean(false);
    private final String rootTagName;
    private final Class<T> clazz;

    public ConfHolder(Class<T> clazz) {
        this.clazz = clazz;
        DomRoot domRoot = clazz.getAnnotation(DomRoot.class);
        this.rootTagName = domRoot == null ? Javas.getFirstLowerFileName(clazz.getSimpleName()) : domRoot.value();
    }

    /**
     * get and cache mapper dom element
     * @param psiFile mapper file
     * @return mapper dom element
     */
    @Nullable
    public T getMapperDomElement(@NotNull PsiFile psiFile) {
        if (holder.containsKey(psiFile)) {
            return holder.get(psiFile);
        } else {
            DomParseService parseService = DomParseService.INSTANCE(psiFile.getProject());
            T dom = parseService.parseDomElementsByFile(psiFile, clazz);
            if (dom != null) { holder.put(psiFile, dom); }
            return dom;
        }
    }
}
