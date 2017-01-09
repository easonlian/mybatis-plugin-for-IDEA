/*
 * Copyright (c) 2017 Qunar.com. All Rights Reserved.
 */
package org.qunar.plugin.mybatis.generator;

import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.sql.psi.SqlCreateTableStatement;
import com.intellij.sql.psi.SqlElement;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.mybatis.bean.model.CreateTableDdl;
import org.qunar.plugin.mybatis.util.DbDdlParser;
import org.qunar.plugin.mybatis.util.MapperConfHolder;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;

/**
 * generate mapper sql xml
 *
 * Author: jianyu.lin
 * Date: 2016/12/22 Time: 下午2:59
 */
public class XmlVelocityGenerator extends AbstractGenerator {

    @NotNull
    private final PsiClass mapperClass;
    @NotNull
    private final PsiClass beanClass;
    @NotNull
    private final String fileName;
    @NotNull
    private final String dirPath;
    @NotNull
    private final SqlCreateTableStatement myCreateTableDdl;

    public XmlVelocityGenerator(@NotNull Project project, @NotNull PsiClass mapperClass, @NotNull PsiClass beanClass,
                                @NotNull SqlElement sqlElement, @NotNull String relatedPath) {
        super(project, sqlElement);
        this.mapperClass = mapperClass;
        this.beanClass = beanClass;
        relatedPath = relatedPath.startsWith("/") ? relatedPath.substring(1, relatedPath.length()) :relatedPath;
        int lastIndex = relatedPath.lastIndexOf("/");
        this.dirPath = lastIndex < 0 ? "" : relatedPath.substring(0, lastIndex);
        this.fileName = relatedPath.substring(lastIndex + 1, relatedPath.length());
        this.myCreateTableDdl = (SqlCreateTableStatement) sqlElement;
    }

    /**
     * {@inheritDoc}
     * @return psi xml
     */
    @Nullable
    @Override
    public XmlFile generate() {
        VirtualFile dirVirtualFile = moduleSetting.getResourceSources().get(0);
        PsiDirectory baseDir = PsiManager.getInstance(project).findDirectory(dirVirtualFile);
        final PsiDirectory generateDir = createOrFindDirectory(baseDir);

        return WriteCommandAction.runWriteCommandAction(project, new Computable<XmlFile>() {
            @Override
            public XmlFile compute() {
                try {
                    VelocityEngine vmEngine = new VelocityEngine();
                    Properties properties = new Properties();
                    properties.load(getAbstractPathStream("velocity/velocity.properties"));
                    //  override path attribute
                    properties.put("file.resource.loader.path", "");
                    vmEngine.init(properties);

                    Template t = vmEngine.getTemplate(getAbstractPathText("velocity/template/mapper.vm"));
                    CreateTableDdl createTableDdl = DbDdlParser.parseCreateTableDdl(myCreateTableDdl);
                    VelocityContext context = new VelocityContext();
                    context.put("empty", "");
                    context.put("namespace", mapperClass.getQualifiedName());
                    context.put("beanClass", beanClass.getQualifiedName());
                    context.put("dbName", createTableDdl.getDbName());
                    context.put("columns", createTableDdl.getColumns());
                    context.put("indexes", createTableDdl.getDbIndices());
                    context.put("keys", createTableDdl.getDbKeys());
                    context.put("foreignKeys", createTableDdl.getForeignKeys());

                    StringWriter writer = new StringWriter();
                    t.merge(context, writer);
                    String xml = writer.toString();
                    XmlFile xmlFile = (XmlFile) PsiFileFactory.getInstance(project)
                            .createFileFromText(XMLLanguage.INSTANCE, xml);
                    //noinspection ConstantConditions
                    XmlTag rootTag = (XmlTag) CodeStyleManager.getInstance(project).reformat(xmlFile.getRootTag(), true);
                    rootTag.getContainingFile().setName(fileName + ".xml");
                    xmlFile = (XmlFile) generateDir.add(rootTag.getContainingFile());
                    MapperConfHolder.INSTANCE.getDomElement(xmlFile);
                    return xmlFile;
                } catch (Exception e) {
                    logger.error("mapper generator error", e);
                    return null;
                }
            }
        });
    }

    /**
     * class path related file path
     * @param relatedPath file path related to class path
     * @return absolute path
     */
    @NotNull
    private String getAbstractPathText(String relatedPath) {
        //noinspection ConstantConditions
        return getClass().getClassLoader().getResource(relatedPath).getPath();
    }

    /**
     * class path related file path
     * @param relatedPath file path related to class path
     * @return absolute path
     */
    @NotNull
    private InputStream getAbstractPathStream(String relatedPath) {
        //noinspection ConstantConditions
        return getClass().getClassLoader().getResourceAsStream(relatedPath);
    }

    /**
     * create or find directory with given base dir
     * @param baseDir class path based dir
     * @return psi directory
     */
    private PsiDirectory createOrFindDirectory(final PsiDirectory baseDir) {
        return ApplicationManager.getApplication().runWriteAction(new Computable<PsiDirectory>() {
            @Override
            public PsiDirectory compute() {
                PsiDirectory directory = baseDir;
                System.out.println(baseDir);
                for (String path : dirPath.split("/")) {
                    PsiDirectory subDirectory = directory.findSubdirectory(path);
                    if (subDirectory == null) {
                        subDirectory = directory.createSubdirectory(path);
                    }
                    directory = subDirectory;
                }
                return directory;
            }
        });
    }
}
