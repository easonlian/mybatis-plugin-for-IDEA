/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.contributor;

import com.google.common.collect.Lists;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.mybatis.bean.Annotation;
import org.qunar.plugin.mybatis.reference.StatementParamsReference;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * resolve ${} #{} reference
 * <p>
 * Author: jianyu.lin
 * Date: 2016/12/3 Time: 下午12:35
 */
public class StatementParamsReferenceContributor extends PsiReferenceContributor {

    private static final Pattern PATTERN = Pattern.compile("(\\$\\{[a-zA-Z0-9_.]*})|(#\\{[a-zA-Z0-9_.]*})");

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(XmlPatterns.xmlTag().withName(getAllStatementAndSubTags()),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element,
                                                                 @NotNull ProcessingContext context) {
                        XmlTag xmlTag = (XmlTag) element;
                        Pair<XmlTag, PsiElement[]> parentTagPair = findParentStatementElement(xmlTag);
                        if (parentTagPair == null) {
                            return new PsiReference[0];
                        }

                        TextRange[] subTagRanges = getSubTagTextRanges(xmlTag);

                        Matcher matcher = PATTERN.matcher(xmlTag.getText());
                        List<StatementParamsReference> references = Lists.newArrayList();
                        while (matcher.find()) {
                            if (matcher.group().contains(".")) continue;
                            int start = matcher.start() + 2;    //  add 2 space for '#{' or '${'
                            int end = matcher.end() - 1;        //  add 1 space for '}'
                            boolean belong2SubTag = false;
                            for (TextRange subTagRange : subTagRanges) {
                                if (xmlTag.getTextRange().getStartOffset() + start > subTagRange.getStartOffset()
                                        && xmlTag.getTextRange().getStartOffset() + end < subTagRange.getEndOffset()) {
                                    belong2SubTag = true;
                                    break;
                                }
                            }
                            if (belong2SubTag) continue;
                            references.add(new StatementParamsReference(xmlTag,
                                    parentTagPair.first, TextRange.create(start, end)).extraParams(parentTagPair.second));
                        }
                        return references.toArray(new PsiReference[references.size()]);
                    }
                });
    }

    @Nullable
    private Pair<XmlTag, PsiElement[]> findParentStatementElement(XmlTag subTag) {
        if (Annotation.getStatementNames().contains(subTag.getName())) {
            return Pair.create(subTag, new PsiElement[0]);
        }
        List<PsiElement> elements = Lists.newArrayList();
        PsiElement psiElement = subTag;
        while (!(psiElement instanceof XmlDocument) && psiElement != null) {
            if (psiElement instanceof XmlTag) {
                XmlTag parentTag = (XmlTag) psiElement;
                elements.addAll(buildExtraParams(parentTag));
                if (Annotation.getStatementNames().contains(parentTag.getName())) {
                    return Pair.create(parentTag, elements.toArray(new PsiElement[elements.size()]));
                }
            }
            psiElement = psiElement.getParent();
        }
        return null;
    }

    /**
     * get all subTag text range
     * @param xmlTag parent tag
     * @return text ranges
     */
    private TextRange[] getSubTagTextRanges(XmlTag xmlTag) {
        XmlTag[] subTags = xmlTag.getSubTags();
        TextRange[] textRanges = new TextRange[subTags.length];
        for (int i = 0; i < subTags.length; i++) {
            textRanges[i] = subTags[i].getTextRange();
        }
        return textRanges;
    }

    @NotNull
    private List<PsiElement> buildExtraParams(@NotNull XmlTag xmlTag) {
        List<PsiElement> elements = Lists.newArrayList();
        if (xmlTag.getName().equals("foreach")) {
            //noinspection ConstantConditions
            if (xmlTag.getAttribute("item") != null
                    && xmlTag.getAttribute("item").getValueElement() != null) {
                //noinspection ConstantConditions
                elements.add(xmlTag.getAttribute("item").getValueElement());
            }
        }
        return elements;
    }

    private String[] getAllStatementAndSubTags() {
        Set<String> tagNames = Annotation.getStatementNames();
        tagNames.add("if");
        tagNames.add("where");
        tagNames.add("when");
        tagNames.add("otherwise");
        tagNames.add("foreach");
        return tagNames.toArray(new String[tagNames.size()]);
    }
}
