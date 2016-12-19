/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.ui;

import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.ui.JBColor;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.bean.ModuleSetting;
import org.qunar.plugin.util.Icons;
import org.qunar.plugin.util.Javas;
import org.qunar.plugin.util.Modules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;

/**
 * directory choose and file name type dialog
 *
 * Author: jianyu.lin
 * Date: 2016/12/2 Time: 上午10:16
 */
public class CreateMapperXmlDialog extends DialogWrapper {

    private static final Logger logger = LoggerFactory.getLogger(CreateMapperXmlDialog.class);
    private static final String TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
            "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n" +
            "<mapper namespace=\"%s\">\n</mapper>";

    private final Project project;
    private final PsiClass psiClass;
    private PsiFile physicalMapperFile;
    private ModuleSetting moduleSetting;

    private JPanel myContentPane;
    private JTextField myMapperNameField;
    private JComboBox<String> myDirectoryComboBox;

    public CreateMapperXmlDialog(@NotNull Project project, @NotNull PsiClass psiClass) {
        super(project, true);
        this.project = project;
        this.psiClass = psiClass;
        init();
        //  init my swing components
        setResizable(false);
        initDirectoryComboBox();
        initMapperNameField();
    }

    /**
     * init directory comboBox
     */
    private void initDirectoryComboBox() {
        //  add directories into comboBox
        if (project.getBasePath() == null) return;
        moduleSetting = Modules.getModuleSettingByElement(psiClass);
        for (VirtualFile virtualFile : moduleSetting.getAllResourceSources()) {
            //noinspection ConstantConditions
            String relatedPath = virtualFile.getPath().replaceFirst(project.getBasePath(), "");
            myDirectoryComboBox.addItem(relatedPath);
            if (myDirectoryComboBox.getSelectedIndex() < 0) {
                myDirectoryComboBox.setSelectedIndex(0);
            }
        }
    }

    /**
     * init mapper name field
     */
    private void initMapperNameField() {
        //  init myMapperNameField
        if (myDirectoryComboBox.getItemCount() > 0) {
            myMapperNameField.setText(buildDefaultMapperName(psiClass.getName()));
            myMapperNameField.setToolTipText("type mapper name with dir path related to classpath");
            myMapperNameField.requestFocus();
            myMapperNameField.addInputMethodListener(new InputMethodListener() {
                @Override
                public void inputMethodTextChanged(InputMethodEvent event) {
                    checkAbsoluteMapperName();
                }
                @Override
                public void caretPositionChanged(InputMethodEvent event) {
                    //  do nothing
                }
            });
        } else {
            myMapperNameField.setEnabled(false);
            myMapperNameField.setText("");
            myMapperNameField.setToolTipText("the dir base is empty, can't create file");
        }
    }

    /**
     * check whether the file exist already
     * @return boolean
     */
    private boolean checkAbsoluteMapperName() {
        VirtualFile exist = LocalFileSystem.getInstance().findFileByPath(getAbsoluteMapperPath());
        if (exist != null && !exist.isDirectory()) {
            Balloon errorMsg = JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder("Can't create file 'src/main/resource/mybatis/mapper/Supplier.xml'. File already exists",
                            Icons.ERROR_ICON, JBColor.background(), new HyperlinkListener() {
                        @Override
                        public void hyperlinkUpdate(HyperlinkEvent e) {
                            System.out.println(e);
                        }
                    }).setFadeoutTime(1500).createBalloon();
            errorMsg.showInCenterOf(myMapperNameField);
            return false;
        }
        return true;
    }

    @Override
    protected void doOKAction() {
        if (!checkAbsoluteMapperName()) {
            return;
        }

        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(getAbsoluteDirPath());
        if (virtualFile != null && virtualFile.isDirectory()) {
            if (getMapperName() == null) {
                return;     //  not close the window
            }
            String[] parts = getMapperName().split("/");
            try {
                PsiFile rawMapperFile = generateMapperFile(parts[parts.length - 1]);
                VirtualFile subVirtualFile = virtualFile;
                for (int i = 0; i < parts.length - 1; i++) {
                    VirtualFile existDir = subVirtualFile.findChild(parts[i]);
                    if (existDir == null || !existDir.isDirectory()) {
                        subVirtualFile = subVirtualFile.createChildDirectory("_" + parts[i] + "_", parts[i]);
                    }
                }
                PsiDirectory psiDirectory = PsiDirectoryFactory.getInstance(project).createDirectory(subVirtualFile);
                physicalMapperFile = (PsiFile) psiDirectory.add(rawMapperFile);
            } catch (Exception e) {
                logger.error("create mapper error, mapperName:{}", getMapperName(), e);
            }
        }
        super.doOKAction();
    }

    private String getAbsoluteMapperPath() {
        return getAbsoluteDirPath() + "/" + getMapperName();
    }

    @NotNull
    private String getAbsoluteDirPath() {
        return project.getBasePath() + myDirectoryComboBox.getSelectedItem();
    }

    /**
     * generate mapper xml file with template string
     * @param fileName name of psi file
     * @return mapper psi file
     */
    @NotNull
    private PsiFile generateMapperFile(@NotNull String fileName) {
        String mapperContent = String.format(TEMPLATE, psiClass.getQualifiedName());
        PsiFile rawFile = PsiFileFactory.getInstance(project)
                .createFileFromText(XMLLanguage.INSTANCE, mapperContent);
        rawFile.setName(fileName);
        return rawFile;
    }

    /**
     * get typed mapper name
     * @return mapper name
     */
    @Nullable
    private String getMapperName() {
        String mapperName = myMapperNameField.getText().trim();
        mapperName = mapperName.startsWith("/") ? mapperName.substring(1, mapperName.length()) : mapperName;
        return StringUtils.isEmpty(mapperName) ? null : mapperName + ".xml";
    }

    /**
     * return null if the creation is failed
     * @return mapper file
     */
    @Nullable
    public PsiFile getNewMapperFile() {
        return physicalMapperFile;
    }

    @NotNull
    private String buildDefaultMapperName(String className) {
        if (className == null) return "";
        return Javas.getFirstLowerFileName(className);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel mainPane = new JPanel(new GridLayout());
        mainPane.add(myContentPane);
        return mainPane;
    }
}
