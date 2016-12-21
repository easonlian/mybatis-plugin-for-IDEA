/*
 * Copyright (c) 2017 Qunar.com. All Rights Reserved.
 */
package org.qunar.plugin.util;

import com.google.common.collect.Maps;
import com.intellij.psi.PsiFile;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.service.DomParseService;

import java.util.Collection;
import java.util.Map;

/**
 * cache config xml files of framework
 *
 * Author: jianyu.lin
 * Date: 2016/12/19 Time: 下午5:26
 */
public abstract class ConfHolder<T extends DomElement> {

    /*
     * hold mapper dom element to make more efficient
     * @see http://www.jetbrains.org/intellij/sdk/docs/reference_guide/frameworks_and_external_apis/xml_dom_api.html?search=xml
     * */
    protected final Map<PsiFile, T> holder = Maps.newConcurrentMap();
    public final String rootTagName;
    private final Class<T> clazz;

    protected ConfHolder(Class<T> clazz) {
        this.clazz = clazz;
        this.rootTagName = Javas.getFirstLowerFileName(clazz.getSimpleName());
    }

    /**
     * get and cached dom element
     * @param psiFile xml file
     * @return mapper dom element
     */
    @Nullable
    public T getDomElement(@NotNull PsiFile psiFile) {
        if (holder.containsKey(psiFile)) {
            return holder.get(psiFile);
        } else {
            DomParseService parseService = DomParseService.INSTANCE(psiFile.getProject());
            T dom = parseService.parseDomElementsByFile(psiFile, clazz);
            if (dom != null) { holder.put(psiFile, dom); }
            return dom;
        }
    }

    /**
     * get all cached dom elements
     * @return dom elements
     */
    public Collection<T> getAllDomElements() {
        return holder.values();
    }
}
