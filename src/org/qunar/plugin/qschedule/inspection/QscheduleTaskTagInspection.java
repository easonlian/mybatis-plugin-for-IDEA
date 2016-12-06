/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.qschedule.inspection;

import com.google.common.collect.Lists;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.spring.model.SpringBeanPointer;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.qschedule.util.QscheduleHelper;
import org.qunar.plugin.service.EditorService;
import org.qunar.plugin.util.Javas;
import org.qunar.plugin.util.Springs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * qschedule配置文件method属性验证
 *
 * Author: jianyu.lin
 * Date: 2016/11/18 Time: 下午11:54
 */
public class QscheduleTaskTagInspection extends LocalInspectionTool {

    private static final Logger logger = LoggerFactory.getLogger(QscheduleTaskTagInspection.class);
    /* spring root tag name */
    private static final String ROOT_TAG_NAME = "beans";
    /* qschedule task tag name */
    private static final String TASK_TAG_NAME = "qschedule:task";
    private static final String DEFAULT_METHOD_VALUE = "run";
    /* qschedule config attributes */
    private static final String METHOD_ATTRIBUTE = "method";
    private static final String REF_ATTRIBUTE = "ref";

    /**
     * 校验整个文件
     *
     * @param file       正在分析的file
     * @param manager    分析管理器
     * @param isOnTheFly {@inheritDoc}
     * @return 检测到的异常
     */
    @Nullable
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file,
                                         @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (!(file instanceof XmlFile)) {
            return null;
        }
        XmlFile xmlFile = (XmlFile) file;
        XmlTag rootTag = xmlFile.getRootTag();
        if (rootTag == null || !StringUtils.equals(ROOT_TAG_NAME, rootTag.getName())) {
            return null;
        }
        List<PsiElement> targetElements = Lists.newArrayList();
        findTargetElements(targetElements, xmlFile.getChildren());
        return buildProblemDescriptor(manager, targetElements);
    }

    /**
     * 校验qschedule配置
     *
     * @param manager     提示管理器
     * @param psiElements qschedule配置
     * @return 异常配置
     */
    private ProblemDescriptor[] buildProblemDescriptor(@NotNull InspectionManager manager,
                                                       List<PsiElement> psiElements) {
        Map<String, SpringBeanPointer> springBeanPointerMap = Springs.findAllSpringBeans(manager.getProject());

        List<ProblemDescriptor> problemDescriptors = Lists.newArrayList();
        for (PsiElement psiElement : psiElements) {
            XmlTag xmlTag = (XmlTag) psiElement;
            String refAttr = xmlTag.getAttributeValue(REF_ATTRIBUTE);
            if (refAttr == null) {
                continue;
            }
            SpringBeanPointer springBean = springBeanPointerMap.get(refAttr);
            if (springBean == null) {
                //  notify the ref error
                ProblemDescriptor problemDescriptor = manager.createProblemDescriptor(psiElement, buildRefTextRange(xmlTag),
                        String.format("'ref'指向的'%s'不存在该 springBean", refAttr), ProblemHighlightType.ERROR, true, LocalQuickFix.EMPTY_ARRAY);
                problemDescriptors.add(problemDescriptor);
            } else {
                String methodAttrValue = xmlTag.getAttributeValue(METHOD_ATTRIBUTE);
                if (methodAttrValue == null) {
                    //  default find 'run' method
                    methodAttrValue = DEFAULT_METHOD_VALUE;
                }
                methodAttrValue = methodAttrValue.trim();
                try {
                    List<PsiMethod> psiMethods = QscheduleHelper.findFixMatchedMethod(springBean.getBeanClass());
                    if (!hasTargetMethod(psiMethods, methodAttrValue)) {
                        String errorMsg = String.format("Task类不包含无参或只含'%s'参的'%s'方法",
                                methodAttrValue, QscheduleHelper.METHOD_PARAM_SIMPLE_NAME);
                        ProblemDescriptor problemDescriptor = manager.createProblemDescriptor(psiElement,
                                buildMethodTextRange(xmlTag), errorMsg, ProblemHighlightType.ERROR, true,
                                buildMethodQuickFix(psiMethods, methodAttrValue, springBean.getBeanClass()));
                        problemDescriptors.add(problemDescriptor);
                    }
                } catch (Exception e) {
                    logger.error("qschedule 'method' attr valid error, methodAttrValue:{}", methodAttrValue, e);
                }
            }
        }
        return problemDescriptors.toArray(new ProblemDescriptor[problemDescriptors.size()]);
    }

    /**
     * 创建method属性快速修复提示
     *
     * @param methods         已存在方法
     * @param methodAttrValue 可新增的方法名
     * @param psiClass        java类型
     * @return quickFix数组
     */
    private LocalQuickFix[] buildMethodQuickFix(List<PsiMethod> methods,
                                                String methodAttrValue, PsiClass psiClass) {
        LocalQuickFix createMethodQuickFix = null;
        if (Javas.matchJavaClassMethodName(methodAttrValue)) {
            createMethodQuickFix = new CreateMethodQuickFix(methodAttrValue, psiClass);
        }
        int size = createMethodQuickFix == null ? methods.size() : methods.size() + 1;
        LocalQuickFix[] quickFices = new LocalQuickFix[size];
        int i = 0;
        if (createMethodQuickFix != null) {
            quickFices[i++] = createMethodQuickFix;
        }
        for (PsiMethod method : methods) {
            quickFices[i++] = new ExistMethodQuickFix(method);
        }
        return quickFices;
    }

    /**
     * method属性异常文字位置
     *
     * @param xmlTag 配置项
     * @return 异常显示位置
     */
    private TextRange buildMethodTextRange(XmlTag xmlTag) {
        return buildTextRange(xmlTag, METHOD_ATTRIBUTE);
    }

    /**
     * ref属性异常文字位置
     *
     * @param xmlTag 配置项
     * @return 异常显示位置
     */
    private TextRange buildRefTextRange(XmlTag xmlTag) {
        return buildTextRange(xmlTag, REF_ATTRIBUTE);
    }

    /**
     * 不包含method属性,在qschedule:task上显示error
     * 否则在method属性上显示error
     *
     * @param xmlTag qschedule配置
     * @return 文字range
     */
    private TextRange buildTextRange(XmlTag xmlTag, String attrName) {
        XmlAttribute xmlAttr = xmlTag.getAttribute(attrName);
        if (xmlAttr == null) {
            return new TextRange(1, TASK_TAG_NAME.length() + 1);
        } else {
            TextRange xmlRange = xmlTag.getTextRange();
            TextRange methodRange = xmlAttr.getTextRange();
            int startOffset = methodRange.getStartOffset() - xmlRange.getStartOffset() + 8;
            return new TextRange(startOffset, startOffset + methodRange.getLength() - 9);
        }
    }

    /**
     * 遍历xml树查找qschedule标签
     *
     * @param targetElements target集合
     * @param psiElements    dom节点children
     */
    private void findTargetElements(@NotNull List<PsiElement> targetElements,
                                    @NotNull PsiElement[] psiElements) {
        for (PsiElement psiElement : psiElements) {
            if (psiElement instanceof XmlTag && StringUtils
                    .equals(((XmlTag) psiElement).getName(), TASK_TAG_NAME)) {
                targetElements.add(psiElement);
            } else {
                findTargetElements(targetElements, psiElement.getChildren());
            }
        }
    }

    /**
     * 是否包含目标方法
     * @param psiMethods 所有可以作为task入口的方法
     * @param targetName 配置的方法名
     * @return 是否满足
     */
    private boolean hasTargetMethod(List<PsiMethod> psiMethods, String targetName) {
        for (PsiMethod psiMethod : psiMethods) {
            if (StringUtils.equals(psiMethod.getName(), targetName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create method attr quick fix
     */
    private static class CreateMethodQuickFix implements LocalQuickFix {

        private final String method;
        private final String qualifiedClass;

        CreateMethodQuickFix(String method, PsiClass psiClass) {
            this.method = method;
            this.qualifiedClass = psiClass.getQualifiedName();
        }

        @Nls
        @NotNull
        @Override
        public String getName() {
            String[] parts = qualifiedClass.split("\\.");
            return String.format("在%s类中创建'%s'方法", parts[parts.length - 1], method);
        }

        @Nls
        @NotNull
        @Override
        public String getFamilyName() {
            return getName();
        }

        /**
         * 快速修复
         * @param project 工程
         * @param descriptor 问题封装
         */
        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement psiElement = descriptor.getPsiElement();
            XmlTag taskTag = (XmlTag) psiElement;
            XmlAttribute refAttr = taskTag.getAttribute(REF_ATTRIBUTE);
            if (refAttr == null || StringUtils.isBlank(refAttr.getValue())) {
                return;
            }
            SpringBeanPointer springBean = Springs.findSpringBeanByName(psiElement.getProject(), refAttr.getValue());
            if (springBean == null || springBean.getBeanClass() == null) {
                return;
            }
            PsiClass beanClass = springBean.getBeanClass();
            //  创建method属性、光标移动到method属性前
            taskTag.setAttribute(METHOD_ATTRIBUTE, method);
            //  添加对应方法、光标移动到方法名前
            PsiMethod newMethod = JavaPsiFacade.getElementFactory(project).createMethod(method, PsiType.VOID);
            beanClass.addBefore(newMethod, beanClass.getLastChild());
            int offset = findInsertMethod(beanClass, newMethod).getTextOffset();
            EditorService.INSTANCE(project).scrollTo(beanClass.getContainingFile(), offset);
        }

        /**
         * 查找新插入的方法, 没找到返回类的名称节点
         * @param beanClass 类型
         * @param newMethod 插入的类型
         * @return 查找的元素
         */
        private PsiElement findInsertMethod(PsiClass beanClass, PsiMethod newMethod) {
            for (PsiMethod psiMethod : beanClass.getAllMethods()) {
                if (StringUtils.equals(psiMethod.getName(), newMethod.getName())
                        && psiMethod.getReturnType() == newMethod.getReturnType()) {
                    return psiMethod;
                }
            }
            return beanClass;
        }
    }

    /**
     * Exist method attr quick fix
     */
    private static class ExistMethodQuickFix implements LocalQuickFix {

        private final String method;

        ExistMethodQuickFix(PsiMethod method) {
            this.method = method.getName();
        }

        @Nls
        @NotNull
        @Override
        public String getName() {
            return String.format("使用已有方法 '%s' 填充method属性", method);
        }

        @Nls
        @NotNull
        @Override
        public String getFamilyName() {
            return getName();
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            try {
                XmlTag taskElement = (XmlTag) descriptor.getPsiElement();
                XmlAttribute methodAttr = taskElement.getAttribute(METHOD_ATTRIBUTE);
                if (methodAttr != null) {
                    methodAttr.setValue(method);
                } else {
                    taskElement.setAttribute(METHOD_ATTRIBUTE, method);
                }
            } catch (Exception e) {
                logger.error("qschedule config ref class error", e);
            }
        }
    }
}
