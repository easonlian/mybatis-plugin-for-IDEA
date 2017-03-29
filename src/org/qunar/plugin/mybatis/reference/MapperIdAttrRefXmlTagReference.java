package org.qunar.plugin.mybatis.reference;

import com.google.common.collect.Lists;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.mybatis.bean.IdDomElement;
import org.qunar.plugin.mybatis.bean.mapper.Mapper;
import org.qunar.plugin.service.DomParseService;
import org.qunar.plugin.util.XmlUtils;

import java.util.List;

/**
 * mapper id attribute to id required tag reference
 *
 * Author: jianyu.lin
 * Date: 16-11-27 Time: 下午1:57
 */
abstract class MapperIdAttrRefXmlTagReference extends PsiReferenceBase<XmlAttributeValue> {

    MapperIdAttrRefXmlTagReference(@NotNull XmlAttributeValue resultMapAttrValue) {
        super(resultMapAttrValue);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        for (IdDomElement result : getIdDomElements()) {
            XmlAttribute resultMapIdAttr = result.getXmlTag().getAttribute("id");
            if (ObjectUtils.equals(XmlUtils.getAttrValue(result.getId()), myElement.getValue())
                    && resultMapIdAttr != null) {
                return resultMapIdAttr.getValueElement();
            }
        }
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<LookupElement> lookupElements = Lists.newArrayList();
        for (IdDomElement idDomElement : getIdDomElements()) {
            if (XmlUtils.getAttrValue(idDomElement.getId()) != null) {
                //noinspection ConstantConditions
                lookupElements.add(LookupElementBuilder.create(XmlUtils.getAttrValue(idDomElement.getId())));
            }
        }
        return lookupElements.toArray();
    }

    @NotNull
    private List<? extends IdDomElement> getIdDomElements() {
        if (myElement == null || StringUtils.isBlank(myElement.getValue())) {
            return Lists.newArrayList();
        }
        Mapper mapperDom = DomParseService.INSTANCE(myElement.getProject())
                .parseDomElementsByFile(myElement.getContainingFile(), Mapper.class);
        if (mapperDom == null) {
            return Lists.newArrayList();
        }
        return getIdDomElements(mapperDom);
    }

    protected abstract List<? extends IdDomElement> getIdDomElements(@NotNull Mapper mapperDom);
}
