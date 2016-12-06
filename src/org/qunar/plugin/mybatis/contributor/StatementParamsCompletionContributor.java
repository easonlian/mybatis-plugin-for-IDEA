/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.contributor;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.mybatis.bean.Annotation;
import org.qunar.plugin.mybatis.bean.mapper.Statement;
import org.qunar.plugin.mybatis.util.DomElements;
import org.qunar.plugin.mybatis.util.ParamPropertyHelper;
import org.qunar.plugin.util.Icons;
import org.qunar.plugin.util.XmlUtils;

import java.util.List;
import java.util.Set;

/**
 * support statement text auto completion with <code>#{}</code> or <code>${}</code><br>
 * 
 * Author: jianyu.lin
 * Date: 2016/11/28 Time: 下午1:36
 */
public class StatementParamsCompletionContributor extends CompletionContributor {

    private static final Set<String> STATEMENT_TAG_NAMES = Annotation.getStatementNames();

    /**
     * fill all parameter into auto completion's result
     * @param parameters context params
     * @param result complete result
     */
    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters,
                                       @NotNull CompletionResultSet result) {

        if (!(DomElements.isMapperXmlFile(parameters.getOriginalFile()))) {
            return;
        }
        int offset = InjectedLanguageUtil.getTopLevelEditor(parameters.getEditor()).getCaretModel().getOffset();
        PsiFile psiFile = InjectedLanguageUtil.getTopLevelFile(parameters.getPosition());
        if (!checkPrefix(psiFile, offset) || !(psiFile instanceof XmlFile)) {
            return;
        }

        //  mapper xml offset
        XmlFile mapperXml = (XmlFile) psiFile;

        XmlElement currentItem = (XmlElement) mapperXml.findElementAt(offset);
        XmlTag statementTag = findStatementXmlTag(currentItem);
        DomElement domItem = DomManager.getDomManager(mapperXml.getProject()).getDomElement(statementTag);
        if (!(domItem instanceof Statement)) {
            return;
        }
        Statement statement = (Statement) domItem;
        PsiMethod refMethod = XmlUtils.getAttrValue(statement.getId());
        if (refMethod == null) {
            return;
        }
        result.addAllElements(buildParamLookupElements(refMethod));
        result.stopHere();
    }


    private List<LookupElement> buildParamLookupElements(@NotNull PsiMethod psiMethod) {
        List<PsiElement> elements = ParamPropertyHelper.buildParamLookupElements(psiMethod);
        return Lists.transform(elements, new Function<PsiElement, LookupElement>() {
            @Override
            public LookupElement apply(PsiElement element) {
                return LookupElementBuilder.create(element, getLookupString(element)).withIcon(Icons.JAVA_PARAMETER_ICON);
            }
        });
    }

    @NotNull
    private String getLookupString(PsiElement element) {
        if (element instanceof PsiAnnotationMemberValue) {
            return element.getText().substring(1, element.getTextLength() - 1);
        }
        if (element instanceof PsiIdentifier) {
            String getterProperty = ParamPropertyHelper.parseGetterMethod(element.getText());
            return getterProperty == null ? element.getText() : getterProperty;
        }
        return element.getText();
    }

    /**
     * check 10 chars prefix
     * @param psiFile origin file
     * @param offset text offset
     * @return prefix check
     */
    private boolean checkPrefix(PsiFile psiFile, int offset) {
        String allText = psiFile.getText();
        for (int i = offset; i >= offset - 20; i--) {
            if (allText.charAt(i) == '{'
                    && (allText.charAt(i - 1) == '#' || allText.charAt(i - 1) == '$')) {
                return true;
            }
        }
        return false;
    }

    /**
     * find statement parent node
     * @param currentItem xml node
     * @return parent node of statement type
     */
    @Nullable
    private XmlTag findStatementXmlTag(XmlElement currentItem) {
        XmlElement parentItem = (XmlElement) currentItem.getParent();
        while (parentItem != null && !(parentItem instanceof XmlDocument)) {
            if ((parentItem instanceof XmlTag)
                    && STATEMENT_TAG_NAMES.contains(((XmlTag) parentItem).getName())) {
                return (XmlTag) parentItem;
            } else {
                parentItem = (XmlElement) parentItem.getParent();
            }
        }
        return null;
    }
}
