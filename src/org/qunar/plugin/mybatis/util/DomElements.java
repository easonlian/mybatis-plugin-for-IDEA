package org.qunar.plugin.mybatis.util;

import com.google.common.collect.Lists;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.sql.psi.SqlFile;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.qunar.plugin.mybatis.bean.mapper.Mapper;
import org.qunar.plugin.mybatis.bean.mapper.Statement;
import org.qunar.plugin.mybatis.util.domchecker.DeleteChecker;
import org.qunar.plugin.mybatis.util.domchecker.InsertChecker;
import org.qunar.plugin.mybatis.util.domchecker.SelectChecker;
import org.qunar.plugin.mybatis.util.domchecker.UpdateChecker;
import org.qunar.plugin.util.XmlUtils;

import java.util.List;

/**
 * author: jianyu.lin  Date: 16-11-26.
 */
public class DomElements {

    /**
     * checkMapper dom elements
     * @param manager inspection manager
     * @param mapperDom mapper dom element
     * @return problems
     */
    @NotNull
    public static List<ProblemDescriptor> checkMapperDomElement(@NotNull InspectionManager manager,
                                                                @NotNull Mapper mapperDom) {
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
        return isMybatisFile(psiFile, MapperConfHolder.INSTANCE.rootTagName);
    }

    /**
     * is mapper configuration file
     * @param psiFile file
     * @return boolean
     */
    public static boolean isConfigurationXmlFile(PsiFile psiFile) {
        return isMybatisFile(psiFile, ConfigConfHolder.INSTANCE.rootTagName);
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
