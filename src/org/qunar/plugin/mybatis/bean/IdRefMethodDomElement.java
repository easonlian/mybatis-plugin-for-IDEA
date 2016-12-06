package org.qunar.plugin.mybatis.bean;

import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import org.qunar.plugin.mybatis.converter.MapperMethodConverter;

/**
 * dom element has required id attribute
 *
 * Author: jianyu.lin
 * Date: 16-11-29 Time: 下午8:00
 */
public interface IdRefMethodDomElement extends IdDomElement {

    @Convert(MapperMethodConverter.class)
    GenericAttributeValue<PsiMethod> getId();
}
