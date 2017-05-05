package org.qunar.plugin.mybatis.query;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.searches.DefinitionsScopedSearch;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.Processor;
import com.intellij.util.QueryExecutor;
import org.jetbrains.annotations.NotNull;
import org.qunar.plugin.mybatis.bean.mapper.Mapper;
import org.qunar.plugin.mybatis.bean.mapper.Statement;
import org.qunar.plugin.mybatis.util.DomElements;
import org.qunar.plugin.mybatis.util.MapperConfHolder;
import org.qunar.plugin.util.XmlUtils;

import java.util.Collection;
import java.util.List;

/**
 * mark mapper xml to be the implementation of mapper class
 * can use 'implementation' key to navigate to xml
 * <p>
 * author: jianyu.lin  Date: 16-11-26.
 */
public class MapperXmlInheritorsSearch implements QueryExecutor<XmlElement, DefinitionsScopedSearch.SearchParameters> {

    /**
     * process inheritor between xml and interface
     *
     * @param queryParameters query parameters
     * @param consumer        processor
     * @return result
     */
    @Override
    public boolean execute(@NotNull DefinitionsScopedSearch.SearchParameters queryParameters,
                           @NotNull Processor<XmlElement> consumer) {
        PsiElement element = queryParameters.getElement();
        //noinspection SimplifiableIfStatement
        if (!(element instanceof PsiClass) && !(element instanceof PsiMethod)) {
            return true;
        }
        return element instanceof PsiClass ?
                processClass((PsiClass) element, consumer) :
                processMethod((PsiMethod) element, consumer);
    }

    /**
     * process class inheritors
     *
     * @param mapperClass mapper class
     * @param consumer    processor
     * @return result
     */
    @SuppressWarnings("SameReturnValue")
    private boolean processClass(@NotNull PsiClass mapperClass,
                                 @NotNull final Processor<XmlElement> consumer) {
        Collection<Mapper> mapperDomElements = MapperConfHolder.INSTANCE.getMapperDomElements(mapperClass);
        for (final Mapper mapperDom : mapperDomElements) {
            if (mapperDom.getXmlElement() != null) {
                consumer.process(mapperDom.getXmlElement());
            }
        }
        return Boolean.TRUE;
    }

    /**
     * process method inheritors
     *
     * @param mapperMethod mapper method
     * @param consumer     processor
     * @return result
     */
    @SuppressWarnings("SameReturnValue")
    private boolean processMethod(@NotNull final PsiMethod mapperMethod,
                                  @NotNull final Processor<XmlElement> consumer) {
        return ApplicationManager.getApplication().runReadAction(new Computable<Boolean>() {
            @Override
            public Boolean compute() {
                if (mapperMethod.getContainingClass() == null) {
                    return Boolean.TRUE;
                }
                Collection<Mapper> mapperDomElements = MapperConfHolder.INSTANCE
                        .getMapperDomElements(mapperMethod.getContainingClass());
                for (final Mapper mapperDom : mapperDomElements) {
                    if (mapperDom.getXmlElement() != null) {
                        List<Statement> statements = DomElements.collectStatements(mapperDom);
                        for (final Statement statement : statements) {
                            PsiMethod psiMethod = XmlUtils.getAttrValue(statement.getId());
                            final XmlAttribute idAttr = statement.getXmlTag().getAttribute("id");
                            if (psiMethod == mapperMethod && idAttr != null) {
                                if (idAttr.getValueElement() != null) {
                                    consumer.process(idAttr.getValueElement());
                                }
                            }
                        }
                    }
                }
                return Boolean.TRUE;
            }
        });
    }
}
