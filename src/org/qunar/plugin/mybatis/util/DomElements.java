package org.qunar.plugin.mybatis.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.sql.psi.SqlFile;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomService;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.mybatis.bean.mapper.Mapper;
import org.qunar.plugin.mybatis.bean.mapper.Statement;
import org.qunar.plugin.mybatis.util.domchecker.DeleteChecker;
import org.qunar.plugin.mybatis.util.domchecker.InsertChecker;
import org.qunar.plugin.mybatis.util.domchecker.SelectChecker;
import org.qunar.plugin.mybatis.util.domchecker.UpdateChecker;
import org.qunar.plugin.service.DomParseService;
import org.qunar.plugin.util.XmlUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * author: jianyu.lin  Date: 16-11-26.
 */
public class DomElements {

    public static final String CONFIG_ROOT_TAG_NAME = "configuration";
    public static final String MAPPER_ROOT_TAG_NAME = "mapper";
    /*
     * hold mapper dom element to make more efficient
     * @see http://www.jetbrains.org/intellij/sdk/docs/reference_guide/frameworks_and_external_apis/xml_dom_api.html?search=xml
     * */
    private static final Map<PsiFile, Mapper> MAPPER_HOLDER = Maps.newConcurrentMap();
    /* is init mapper xmls */
    private static final AtomicBoolean initCheck = new AtomicBoolean(false);

    /**
     * get and cache mapper dom element
     * @return mapper dom element
     */
    @SuppressWarnings("WeakerAccess")
    @NotNull
    public static Collection<Mapper> getMapperDomElementByNamespace(final String namespace) {
        return Collections2.filter(MAPPER_HOLDER.values(), new Predicate<Mapper>() {
            @Override
            public boolean apply(Mapper mapper) {
                if (mapper.getXmlTag() == null || 
                        !MAPPER_ROOT_TAG_NAME.equals(mapper.getXmlTag().getName())) {
                    // clear and reload cache 
                    initCheck.set(false);
                    return false;
                }
                return StringUtils.equals(mapper.getNamespace().getStringValue(), namespace);
            }
        });
    }

    /**
     * get all mapping mapper xml
     * @param mapperClass class
     * @return mapper dom elements
     */
    @NotNull
    public static Collection<Mapper> getMapperDomElements(@NotNull final PsiClass mapperClass) {
        if (!initCheck.get()) {
            return ApplicationManager.getApplication().runReadAction(new Computable<Collection<Mapper>>() {
                @Override
                public Collection<Mapper> compute() {
                    List<DomFileElement<Mapper>> mapperFiles = DomService.getInstance().getFileElements(Mapper.class,
                            mapperClass.getProject(), GlobalSearchScope.projectScope(mapperClass.getProject()));
                    List<Mapper> mapperDomList = Lists.newArrayList();
                    for (DomFileElement<Mapper> mapperFile : mapperFiles) {
                        PsiClass psiClass = XmlUtils.getAttrValue(mapperFile.getRootElement().getNamespace());
                        if (mapperClass == psiClass) {
                            mapperDomList.add(mapperFile.getRootElement());
                        }
                    }
                    return mapperDomList;
                }
            });
        } else {
            return getMapperDomElementByNamespace(mapperClass.getQualifiedName());
        }
    }

    /**
     * get and cache mapper dom element
     * @param psiFile mapper file
     * @return mapper dom element
     */
    @Nullable
    public static Mapper getMapperDomElement(@NotNull PsiFile psiFile) {
        if (MAPPER_HOLDER.containsKey(psiFile)) {
            return MAPPER_HOLDER.get(psiFile);
        } else {
            DomParseService parseService = DomParseService.INSTANCE(psiFile.getProject());
            Mapper mapper = parseService.parseDomElementsByFile(psiFile, Mapper.class);
            if (mapper != null) {
                MAPPER_HOLDER.put(psiFile, mapper);
            }
            return mapper;
        }
    }

    /**
     * checkMapper dom elements
     * @param manager inspection manager
     * @param mapperDom mapper dom element
     * @return problems
     */
    @NotNull
    public static List<ProblemDescriptor> checkMapperDomElement(@NotNull InspectionManager manager,
                                                                @Nullable Mapper mapperDom) {
        if (mapperDom == null) {
            return Lists.newArrayList();
        }
        PsiClass mapperClass = XmlUtils.getAttrValue(mapperDom.getNamespace());
        if (mapperClass == null) {
            return Lists.newArrayList();
        }

        List<ProblemDescriptor> allProblems = Lists.newArrayList();
        allProblems.addAll(new InsertChecker(mapperClass).check(manager, mapperDom.getInserts()));
        allProblems.addAll(new DeleteChecker(mapperClass).check(manager, mapperDom.getDeletes()));
        allProblems.addAll(new SelectChecker(mapperClass).check(manager, mapperDom.getSelects()));
        allProblems.addAll(new UpdateChecker(mapperClass).check(manager, mapperDom.getUpdates()));
        return allProblems;
    }

    /**
     * collection all statements
     * @param mapperDom mapper root dom
     * @return statements
     */
    @NotNull
    public static List<Statement> collectStatements(Mapper mapperDom) {
        if (mapperDom == null) {
            return Lists.newArrayList();
        }
        List<Statement> statements = Lists.newArrayList();
        statements.addAll(mapperDom.getSelects());
        statements.addAll(mapperDom.getInserts());
        statements.addAll(mapperDom.getUpdates());
        statements.addAll(mapperDom.getDeletes());
        return statements;
    }

    /**
     * is mapper xml file
     * @param psiFile file
     * @return boolean
     */
    public static boolean isMapperXmlFile(PsiFile psiFile) {
        return isMybatisFile(psiFile, MAPPER_ROOT_TAG_NAME);
    }

    /**
     * is mapper configuration file
     * @param psiFile file
     * @return boolean
     */
    public static boolean isConfigurationXmlFile(PsiFile psiFile) {
        return isMybatisFile(psiFile, MAPPER_ROOT_TAG_NAME);
    }

    /**
     * is mybatis config file
     * @param psiFile file
     * @param rootTagName root tag name
     * @return boolean
     */
    private static boolean isMybatisFile(PsiFile psiFile, String rootTagName) {
        if (psiFile == null) {
            return false;
        }
        if (psiFile instanceof XmlFile) {
            return isMybatisXmlFile((XmlFile) psiFile, rootTagName);
        }
        return psiFile instanceof SqlFile && isMybatisSqlFile((SqlFile) psiFile, rootTagName);
    }

    /**
     * judge sql file
     * @param sqlFile sql file
     * @param rootTagName root tag name
     * @return boolean
     */
    private static boolean isMybatisSqlFile(SqlFile sqlFile, String rootTagName) {
        PsiFile psiFile = InjectedLanguageUtil.getTopLevelFile(sqlFile);
        return psiFile instanceof XmlFile && isMybatisFile(psiFile, rootTagName);
    }

    /**
     * judge sql file
     * @param xmlFile xml file
     * @param rootTagName root tag name
     * @return boolean
     */
    private static boolean isMybatisXmlFile(XmlFile xmlFile, String rootTagName) {
        return xmlFile.getRootTag() != null &&
                StringUtils.equals(xmlFile.getRootTag().getName(), rootTagName);
    }
}
