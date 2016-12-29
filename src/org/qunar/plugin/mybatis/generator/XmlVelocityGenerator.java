/*
 * Copyright (c) 2017 Qunar.com. All Rights Reserved.
 */
package org.qunar.plugin.mybatis.generator;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.XmlElementFactory;
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
    private final String fileName;
    @NotNull
    private final String dirPath;
    @NotNull
    private final SqlCreateTableStatement myCreateTableDdl;

    public XmlVelocityGenerator(@NotNull Project project, @NotNull PsiClass mapperClass,
                                @NotNull SqlElement sqlElement, @NotNull String relatedPath) {
        super(project, sqlElement);
        this.mapperClass = mapperClass;
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

        return ApplicationManager.getApplication().runWriteAction(new Computable<XmlFile>() {
            @Override
            public XmlFile compute() {

                CreateTableDdl createTableDdl = DbDdlParser.parseCreateTableDdl(myCreateTableDdl);

                VelocityEngine vmEngine = new VelocityEngine();
                vmEngine.init();
                Template t = vmEngine.getTemplate("template/mapper.vm");


                VelocityContext context = new VelocityContext();
                context.put("columns", myCreateTableDdl.getDeclaredColumns());
                return null;
            }
        });
    }

    /**
     * generate mapper statement
     * @param xmlFile mapper xml file
     */
    private void generateStatement(final XmlFile xmlFile) {
        WriteCommandAction.runWriteCommandAction(project, new Runnable() {
            @Override
            public void run() {
                XmlTag rootTag = xmlFile.getRootTag();
                if (rootTag == null) return;

                @SuppressWarnings("ConstantConditions")
                String qualifiedBeanName = mapperClass.getQualifiedName().substring(0, mapperClass.getName().length() - 3);

                String sql = String.format(
                        "<select id=\"selectList\" parameterType=\"%s\">\n" +
                                "      SELECT * FROM %s\n" +
                                "    </select>", qualifiedBeanName, myCreateTableDdl.getName());
                XmlTag selectListTag = XmlElementFactory.getInstance(project).createTagFromText(sql);
                rootTag.add(selectListTag);

                sql = String.format(
                        "<delete id=\"delete\" parameterType=\"%s\">\n" +
                                "      DELETE FROM %s WHERE id = #{id}\n" +
                                "    </delete>", qualifiedBeanName, myCreateTableDdl.getName());
                XmlTag deleteTag = XmlElementFactory.getInstance(project).createTagFromText(sql);
                rootTag.add(deleteTag);

                sql = String.format(
                        "<insert id=\"insert\" parameterType=\"%s\">\n" +
                                "      INSERT INTO %s(" +
                                ")\n" +
                                "    </insert>", qualifiedBeanName, myCreateTableDdl.getName());
                XmlTag insertOne = XmlElementFactory.getInstance(project).createTagFromText(sql);
                rootTag.add(insertOne);

                CodeStyleManager.getInstance(project).reformat(xmlFile.getRootTag());
            }
        });
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
