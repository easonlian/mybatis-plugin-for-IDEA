/*
 * Copyright (c) 2017 Qunar.com. All Rights Reserved.
 */
package org.qunar.plugin.mybatis.generator;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.sql.psi.SqlFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.mybatis.ui.CreateMapperXmlDialog;

/**
 * generate mapper sql xml
 *
 * Author: jianyu.lin
 * Date: 2016/12/22 Time: 下午2:59
 */
public class XmlGenerator extends AbstractGenerator {

    @NotNull
    private final PsiClass mapperClass;
    @NotNull
    private final String fileName;
    @NotNull
    private final String dirPath;

    public XmlGenerator(@NotNull Project project, @NotNull PsiClass mapperClass,
                        @NotNull SqlFile sqlFile, @NotNull String relatedPath) {
        super(project, sqlFile);
        this.mapperClass = mapperClass;
        relatedPath = relatedPath.startsWith("/") ? relatedPath.substring(1, relatedPath.length()) :relatedPath;
        int lastIndex = relatedPath.lastIndexOf("/");
        this.dirPath = lastIndex < 0 ? "" : relatedPath.substring(0, lastIndex);
        this.fileName = relatedPath.substring(lastIndex + 1, relatedPath.length());
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
                CreateMapperXmlDialog dialog = new CreateMapperXmlDialog(project, mapperClass);
                XmlFile xmlFile = (XmlFile) dialog.generateMapperFile(fileName + ".xml");
                generateDir.add(xmlFile);
                return xmlFile;
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
