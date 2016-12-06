package org.qunar.plugin.mybatis.reference;

import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.qunar.plugin.mybatis.bean.IdDomElement;
import org.qunar.plugin.mybatis.bean.mapper.Mapper;

import java.util.List;

/**
 * mapper sql tag reference
 *
 * Author: jianyu.lin
 * Date: 16-11-27 Time: 下午1:57
 */
public class MapperAttr2ResultMapReference extends MapperIdAttrRefXmlTagReference {

    public MapperAttr2ResultMapReference(@NotNull XmlAttributeValue resultMapAttrValue) {
        super(resultMapAttrValue);
    }

    @Override
    protected List<? extends IdDomElement> getIdDomElements(@NotNull Mapper mapperDom) {
        return mapperDom.getResultMaps();
    }
}
