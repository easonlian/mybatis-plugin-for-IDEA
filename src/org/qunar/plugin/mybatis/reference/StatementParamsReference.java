/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.reference;

import com.google.common.collect.Sets;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomManager;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.mybatis.bean.mapper.Statement;
import org.qunar.plugin.mybatis.util.ParamPropertyHelper;

import java.util.Arrays;
import java.util.Set;

/**
 * add inline sql parameter (${} #{}) reference
 *
 * Author: jianyu.lin
 * Date: 2016/12/3 Time: 下午12:40
 */
public class StatementParamsReference extends PsiReferenceBase<XmlTag> {

    private final XmlTag parent;
    private final String text;
    /* add for support tags who adds temp parameters,
     * eg: <foreach ... item="tempParam" >
     **/
    private Set<PsiElement> extraParams = Sets.newHashSet();

    public StatementParamsReference(@NotNull XmlTag xmlTag,
                                    @NotNull XmlTag parentTag, @NotNull TextRange range) {
        super(xmlTag, range, true);
        this.parent = parentTag;
        text = xmlTag.getText().substring(range.getStartOffset(), range.getEndOffset());
    }

    /**
     * add extra params
     * @param extraParams extra params
     * @return this
     */
    public StatementParamsReference extraParams(PsiElement... extraParams) {
        if (extraParams != null) {
            this.extraParams.addAll(Arrays.asList(extraParams));
        }
        return this;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Statement statement = (Statement) DomManager.getDomManager(myElement.getProject()).getDomElement(parent);
        if (statement == null || statement.getId().getValue() == null) {
            return null;
        }
        PsiMethod psiMethod = statement.getId().getValue();
        Set<PsiElement> elements = ParamPropertyHelper.buildParamLookupElements(psiMethod);
        elements.addAll(extraParams);
        for (PsiElement element : elements) {
            if (checkPsiElementWithTargetText(element)) {
                return element;
            }
        }
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    /**
     * check the reference psi element
     * @param element parameter element
     * @return boolean
     */
    private boolean checkPsiElementWithTargetText(PsiElement element) {
        String psiName;
        if (element instanceof PsiAnnotationMemberValue) {  //  @Param
            //  delete first letter " and last one "
            psiName = element.getText().substring(1, element.getTextLength() - 1);
        } else if (element instanceof PsiIdentifier) {      //  method name
            String getterProperty = ParamPropertyHelper.parseGetterMethod(element.getText());
            psiName = getterProperty == null ? element.getText() : getterProperty;
        } else if (element instanceof XmlAttributeValue) {  //  xml attribute
            psiName = ((XmlAttributeValue) element).getValue();
        } else {
            psiName = element.getText();
        }
        return StringUtils.equals(text, psiName);
    }
}
