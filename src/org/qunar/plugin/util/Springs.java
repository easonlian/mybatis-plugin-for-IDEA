/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.spring.SpringManager;
import com.intellij.spring.contexts.model.SpringModel;
import com.intellij.spring.model.SpringBeanPointer;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.service.JavaService;

import java.util.Map;
import java.util.Set;

/**
 * spring utils
 *
 * Author: jianyu.lin
 * Date: 2016/11/19 Time: 下午11:38
 */
public class Springs {

    /**
     * 查找所有springBeans
     * @param project 当前工程
     * @return springBeans
     */
    @NotNull
    public static Map<String, SpringBeanPointer> findAllSpringBeans(@NotNull Project project) {
        Set<SpringBeanPointer> beanPointers = findSpringBeansByPredicate(project, new Predicate<SpringBeanPointer>() {
            @Override
            public boolean apply(SpringBeanPointer springBeanPointer) {
                return springBeanPointer.getSpringBean().getBeanName() != null;
            }
        });

        Map<String, SpringBeanPointer> beanPointerMap = Maps.newHashMap();
        for (SpringBeanPointer beanPointer : beanPointers) {
            beanPointerMap.put(beanPointer.getSpringBean().getBeanName(), beanPointer);
        }
        return beanPointerMap;
    }

    /**
     * find all spring bean of specified class type
     * @param project current project
     * @param qualifiedName bean class qualified name
     * @return all spring beans of the class
     */
    @NotNull
    public static Set<SpringBeanPointer> findSpringBeansByType(@NotNull Project project,
                                                               @SuppressWarnings("SameParameterValue") @Nullable String qualifiedName) {
        final PsiClass psiClass = JavaService.getInstance(project).findClass(qualifiedName);
        return findSpringBeansByPredicate(project, new Predicate<SpringBeanPointer>() {
            @Override
            public boolean apply(SpringBeanPointer springBeanPointer) {
                return psiClass != null && springBeanPointer.getBeanClass() == psiClass;
            }
        });
    }

    /**
     * find spring bean by name
     * @param project current project
     * @param beanName bean name
     * @return springBean
     */
    public static SpringBeanPointer findSpringBeanByName(@NotNull Project project,
                                                         @Nullable final String beanName) {
        Set<SpringBeanPointer> springBeanPointers = findSpringBeansByPredicate(project, new Predicate<SpringBeanPointer>() {
            @Override
            public boolean apply(SpringBeanPointer springBeanPointer) {
                return StringUtils.equals(springBeanPointer.getSpringBean().getBeanName(), beanName);
            }
        });
        return springBeanPointers.isEmpty() ? null : springBeanPointers.iterator().next();
    }

    /**
     * find all springBeans
     * @param project 当前工程
     * @return springBeans
     */
    @NotNull
    private static Set<SpringBeanPointer> findSpringBeansByPredicate(@NotNull Project project,
                                                                    @NotNull Predicate<SpringBeanPointer> predicate) {
        try {
            Set<SpringBeanPointer> springBeanPointers = Sets.newHashSet();
            SpringManager springManager = SpringManager.getInstance(project);
            Module[] modules = ModuleManager.getInstance(project).getModules();
            for (Module module : modules) {
                for (final SpringModel springModel : springManager.getAllModels(module)) {
                    for (SpringBeanPointer springBeanPointer : springModel.getAllCommonBeans()) {
                        if (predicate.apply(springBeanPointer)) {
                            springBeanPointers.add(springBeanPointer);
                        }
                    }
                }
            }
            return springBeanPointers;
        } catch (Exception e) {
            return Sets.newHashSet();
        }
    }
}
