/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.qschedule.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: jianyu.lin
 * Date: 2016/11/20 Time: 下午2:57
 */
public class QscheduleHelper {

    /* qschedule task execute parameter class qualified name */
    private static final String METHOD_PARAM_QUALIFIED_NAME = "qunar.tc.schedule.Parameter";
    /* qschedule task execute parameter class simple name */
    public static final String METHOD_PARAM_SIMPLE_NAME = "Parameter";
    /* this method names which aren't completing */
    private static final Set<String> EXCLUDE_METHOD_NAMES = Sets.newHashSet("Object");

    static {
        Method[] methods = Object.class.getDeclaredMethods();
        for (Method method : methods) {
            EXCLUDE_METHOD_NAMES.add(method.getName());
        }
    }

    /**
     * 查找方法关联功能中类中名称满足qschedule配置的方法
     *
     * @param psiClass   java类
     * @param methodName method属性
     * @return task入口方法
     */
    @Nullable
    public static PsiMethod findRefMatchedMethod(PsiClass psiClass, String methodName) {
        List<PsiMethod> taskPsiMethods = findMatchedMethod(psiClass, methodName, true);
        return taskPsiMethods.isEmpty() ? null : taskPsiMethods.get(0);
    }

    /**
     * 查找快速修复功能类中名称满足qschedule配置的方法 名称匹配
     *
     * @param psiClass   java类
     * @param methodName method属性
     * @return task入口方法
     */
    @NotNull
    public static List<PsiMethod> findFixMatchedMethod(PsiClass psiClass, String methodName) {
        return findMatchedMethod(psiClass, methodName, false);
    }

    /**
     * 查找快速修复功能类中名称满足qschedule配置的方法 模糊匹配
     *
     * @param psiClass java类
     * @return task入口方法
     */
    @NotNull
    public static List<PsiMethod> findFixMatchedMethod(PsiClass psiClass) {
        List<PsiMethod> list = findMatchedMethod(psiClass, null, false);
        List<PsiMethod> filtered = Lists.newArrayList();
        for (PsiMethod psiMethod : list) {
            if (!EXCLUDE_METHOD_NAMES.contains(psiMethod.getName())) {
                filtered.add(psiMethod);
            }
        }
        return filtered;
    }

    /**
     * 查找类中名称满足qschedule配置的方法
     *
     * @param psiClass   java类
     * @param methodName method属性
     * @param findOne    是否只需要一个方法
     * @return task入口方法
     */
    @NotNull
    private static List<PsiMethod> findMatchedMethod(PsiClass psiClass, String methodName, boolean findOne) {
        PsiMethod[] psiMethods = psiClass.getAllMethods();
        Map<String, PsiMethod> taskPsiMethodMap = Maps.newHashMap();
        for (PsiMethod psiMethod : psiMethods) {
            PsiParameter[] parameters = psiMethod.getParameterList().getParameters();
            //  筛选无参方法和含有 qunar.tc.schedule.Parameter一个参数的方法
            //  详见qschedule逻辑: qunar.tc.qschedule.config.spring.TaskBean#resolveMethod
            if (parameters.length > 1 || (parameters.length == 1 &&
                    !parameters[0].getType().getCanonicalText().equals(METHOD_PARAM_QUALIFIED_NAME))) {
                continue;
            }
            if (methodName == null) {
                putPsiMethod(taskPsiMethodMap, psiMethod);
            } else if (StringUtils.equals(psiMethod.getName(), methodName)) {
                putPsiMethod(taskPsiMethodMap, psiMethod);
                //  qschedule会优先选择无参方法
                //  详见qschedule逻辑: qunar.tc.qschedule.config.spring.TaskBean#resolveMethod
                if (findOne && parameters.length == 0) {
                    return Lists.newArrayList(psiMethod);
                }
            }
        }
        return Lists.newArrayList(taskPsiMethodMap.values());
    }

    private static void putPsiMethod(Map<String, PsiMethod> methodMap, PsiMethod psiMethod) {
        if (methodMap.containsKey(psiMethod.getName())) {
            if (psiMethod.getParameterList().getParameters().length != 0) {
                return;
            }
        }
        methodMap.put(psiMethod.getName(), psiMethod);
    }
}
