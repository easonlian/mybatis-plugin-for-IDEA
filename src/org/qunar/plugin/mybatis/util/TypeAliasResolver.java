package org.qunar.plugin.mybatis.util;

import com.google.common.collect.Maps;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.spring.model.SpringBeanPointer;
import com.intellij.spring.model.utils.SpringPropertyUtils;
import com.intellij.spring.model.xml.beans.SpringPropertyDefinition;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.qunar.plugin.mybatis.bean.config.Configuration;
import org.qunar.plugin.mybatis.bean.config.Package;
import org.qunar.plugin.mybatis.bean.config.TypeAlias;
import org.qunar.plugin.service.DomParseService;
import org.qunar.plugin.service.JavaService;
import org.qunar.plugin.util.Springs;
import org.qunar.plugin.util.Strings;
import org.qunar.plugin.util.XmlUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * type alias resolver
 *
 * author: jianyu.lin  Date: 16-11-26.
 */
public enum TypeAliasResolver {

    /**
     * type aliases defined in mybatis configuration xml
     */
    @SuppressWarnings("unused")
    MYBATIS_CONFIG {
        @Override
        @NotNull
        public Map<String, String> resolve(@NotNull Project project) {
            Map<String, String> typeAliasMap = Maps.newHashMap();
            DomParseService parseService = DomParseService.INSTANCE(project);
            for (SpringBeanPointer springBean : TypeAliasResolver.getSqlSessionFactoryBeans(project)) {
                SpringPropertyDefinition configLocation = SpringPropertyUtils.
                        findPropertyByName(springBean.getSpringBean(), MYBATIS_SPRING_CONFIG_LOCATION);
                if (configLocation == null || configLocation.getValueElement() == null
                        || configLocation.getValueElement().getValue() == null) {
                    continue;
                }
                @SuppressWarnings("unchecked")
                Collection<PsiFile> psiFiles = (Collection<PsiFile>) configLocation.getValueElement().getValue();
                for (PsiFile configFile : psiFiles) {
                    Configuration configuration = parseService.parseDomElementsByFile(configFile, Configuration.class);
                    if (configuration == null || configuration.getTypeAliases() == null) {
                        continue;
                    }
                    //  add package sub files alias
                    typeAliasMap.putAll(buildPackageTypeAliasMaps(project, configuration));
                    //  add class alias
                    typeAliasMap.putAll(buildClassTypeAliasMaps(configuration));
                }
            }
            return typeAliasMap;
        }

        @NotNull
        private Map<String, String> buildClassTypeAliasMaps(@NotNull Configuration configuration) {
            Map<String, String> packageTypeAliasMap = Maps.newHashMap();
            for (TypeAlias typeAlias : configuration.getTypeAliases().getTypeAlias()) {
                if (typeAlias.getType().getValue() == null) {
                    continue;
                }
                PsiClass psiClass = typeAlias.getType().getValue();
                if (psiClass != null) {
                    String alias = XmlUtils.getAttrValue(typeAlias.getAlias());
                    packageTypeAliasMap.put(alias == null ? psiClass.getQualifiedName() : alias, psiClass.getQualifiedName());
                }
            }
            return packageTypeAliasMap;
        }

        @NotNull
        private Map<String, String> buildPackageTypeAliasMaps(@NotNull Project project,
                                                              @NotNull Configuration configuration) {
            Map<String, String> packageTypeAliasMap = Maps.newHashMap();
            for (Package javaPackage : configuration.getTypeAliases().getPackages()) {
                if (javaPackage.getName().getValue() == null) {
                    continue;
                }
                PsiPackage psiPackage = javaPackage.getName().getValue();
                for (PsiFile psiFile : psiPackage.getFiles(GlobalSearchScope.projectScope(project))) {
                    if (!psiFile.isPhysical() || psiFile.getFileType() != JavaFileType.INSTANCE
                            || javaPackage.getName().getXmlAttribute() == null) {
                        continue;
                    }
                    String qualifiedClassName = javaPackage.getName().getXmlAttribute().getValue()
                            + "." + psiFile.getName() + JavaFileType.DOT_DEFAULT_EXTENSION;
                    PsiClass psiClass =JavaService.getInstance(project).findProjectClass(qualifiedClassName);
                    if (psiClass != null) {
                        packageTypeAliasMap.put(qualifiedClassName, qualifiedClassName);
                    }
                }
            }
            return packageTypeAliasMap;
        }
    },
    /**
     * type aliases defined in spring xml with SqlSessionFactoryBean
     */
    @SuppressWarnings("unused")
    SPRING_CONFIG {
        @NotNull
        @Override
        public Map<String, String> resolve(@NotNull Project project) {
            Map<String, String> typeAliasMap = Maps.newHashMap();
            for (SpringBeanPointer springBean : TypeAliasResolver.getSqlSessionFactoryBeans(project)) {

                SpringPropertyDefinition typeAliases = SpringPropertyUtils.
                        findPropertyByName(springBean.getSpringBean(), MYBATIS_SPRING_CONFIG_TYPE_ALIASES);
                if (typeAliases != null && typeAliases.getValueElement() != null
                        && typeAliases.getValueElement().getValue() != null) {
                    String typeAliasesValue = (String) typeAliases.getValueElement().getValue();
                    PsiClass psiClass = JavaService.getInstance(project).findClass(typeAliasesValue);
                    if (psiClass != null) {
                        typeAliasMap.put(psiClass.getQualifiedName(), psiClass.getQualifiedName());
                    }
                }

                SpringPropertyDefinition typeAliasesPackage = SpringPropertyUtils.
                        findPropertyByName(springBean.getSpringBean(), MYBATIS_SPRING_CONFIG_TYPE_ALIASES_PACKAGE);
                if (typeAliasesPackage != null && typeAliasesPackage.getValueElement() != null
                        && typeAliasesPackage.getValueElement().getValue() != null) {
                    @SuppressWarnings("unchecked")
                    String typeAliasesPackageValue = (String) typeAliasesPackage.getValueElement().getValue();
                    if (StringUtils.isNotBlank(typeAliasesPackageValue)) {
                        String[] typeAliasPackageArray = Strings.tokenizeToStringArray(typeAliasesPackageValue, CONFIG_LOCATION_DELIMITERS);
                        for (String packageToScan : typeAliasPackageArray) {
                            PsiPackage psiPackage = JavaService.getInstance(project).findPackage(packageToScan);
                            if (psiPackage == null) {
                                continue;
                            }
                            for (PsiClass childClass : psiPackage.getClasses()) {
                                if (StringUtils.isBlank(childClass.getQualifiedName())) {
                                    continue;
                                }
                                typeAliasMap.put(childClass.getQualifiedName(), childClass.getQualifiedName());
                            }
                        }
                    }
                }
            }
            return typeAliasMap;
        }
    },
    /**
     * default type aliases of mybatis, such as: int, string
     */
    @SuppressWarnings("unused")
    DEFAULT {
        @Override
        @NotNull
        public Map<String, String> resolve(@NotNull Project project) {
            //  for detail, see: org.apache.ibatis.type.TypeAliasRegistry
            return new HashMap<String, String>() {
                {
                    put("string", String.class.getName());
                    put("byte", Byte.class.getName());
                    put("long", Long.class.getName());
                    put("short", Short.class.getName());
                    put("int", Integer.class.getName());
                    put("integer", Integer.class.getName());
                    put("double", Double.class.getName());
                    put("float", Float.class.getName());
                    put("boolean", Boolean.class.getName());

//                    put("byte[]", Byte[].class.getName());
//                    put("long[]", Long[].class.getName());
//                    put("short[]", Short[].class.getName());
//                    put("int[]", Integer[].class.getName());
//                    put("integer[]", Integer[].class.getName());
//                    put("double[]", Double[].class.getName());
//                    put("float[]", Float[].class.getName());
//                    put("boolean[]", Boolean[].class.getName());

//                    put("_byte", byte.class.getName());
//                    put("_long", long.class.getName());
//                    put("_short", short.class.getName());
//                    put("_int", int.class.getName());
//                    put("_integer", int.class.getName());
//                    put("_double", double.class.getName());
//                    put("_float", float.class.getName());
//                    put("_boolean", boolean.class.getName());
//
//                    put("_byte[]", byte[].class.getName());
//                    put("_long[]", long[].class.getName());
//                    put("_short[]", short[].class.getName());
//                    put("_int[]", int[].class.getName());
//                    put("_integer[]", int[].class.getName());
//                    put("_double[]", double[].class.getName());
//                    put("_float[]", float[].class.getName());
//                    put("_boolean[]", boolean[].class.getName());

                    put("date", Date.class.getName());
                    put("decimal", BigDecimal.class.getName());
                    put("bigdecimal", BigDecimal.class.getName());
                    put("biginteger", BigInteger.class.getName());
                    put("object", Object.class.getName());

//                    put("date[]", Date[].class.getName());
//                    put("decimal[]", BigDecimal[].class.getName());
//                    put("bigdecimal[]", BigDecimal[].class.getName());
//                    put("biginteger[]", BigInteger[].class.getName());
//                    put("object[]", Object[].class.getName());

                    put("map", Map.class.getName());
                    put("hashmap", HashMap.class.getName());
                    put("list", List.class.getName());
                    put("arraylist", ArrayList.class.getName());
                    put("collection", Collection.class.getName());
                    put("iterator", Iterator.class.getName());

                    put("ResultSet", ResultSet.class.getName());
                }
            };
        }
    },
    ;

    /**
     * gather all type alias of the project
     * @param project project object
     * @return all type aliases <code>Map&lt;alias, qualifiedClassName&gt;</code>
     */
    @NotNull
    public static Map<String, String> getAllTypeAlias(Project project) {
        if (project == null) {
            return Maps.newHashMap();
        }
        Map<String, String> typeAliasMap = Maps.newLinkedHashMap();
        for (TypeAliasResolver resolver : TypeAliasResolver.values()) {
            typeAliasMap.putAll(resolver.resolve(project));
        }
        return typeAliasMap;
    }

    /**
     * get all sqlSessionFactoryBean
     * @param project current project
     * @return spring bean
     */
    @NotNull
    private static Set<SpringBeanPointer> getSqlSessionFactoryBeans(@NotNull final Project project) {
        return Springs.findSpringBeansByType(project, MYBATIS_SPRING_CONFIG_CLASS_NAME);
    }

    private static final String MYBATIS_SPRING_CONFIG_CLASS_NAME = "org.mybatis.spring.SqlSessionFactoryBean";
    private static final String MYBATIS_SPRING_CONFIG_LOCATION = "configLocation";
    private static final String MYBATIS_SPRING_CONFIG_TYPE_ALIASES = "typeAliases";
    private static final String MYBATIS_SPRING_CONFIG_TYPE_ALIASES_PACKAGE = "typeAliasesPackage";

    private static final String CONFIG_LOCATION_DELIMITERS = ",; \t\n";

    /**
     * find all type aliases defined in this project
     * @param project project object
     * @return all type aliases <code>Map&lt;alias, qualifiedClassName&gt;</code>
     */
    @NotNull
    public abstract Map<String, String> resolve(@NotNull Project project);
}
