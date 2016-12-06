/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.serialization.JDomSerializationUtil;
import org.jetbrains.jps.model.serialization.PathMacroUtil;
import org.qunar.plugin.bean.ModuleSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jetbrains.jps.model.serialization.java.JpsJavaModelSerializerExtension.URL_ATTRIBUTE;
import static org.jetbrains.jps.model.serialization.module.JpsModuleRootModelSerializer.IS_TEST_SOURCE_ATTRIBUTE;
import static org.jetbrains.jps.model.serialization.module.JpsModuleRootModelSerializer.TYPE_ATTRIBUTE;


/**
 * module utils
 * <p>
 * Author: jianyu.lin
 * Date: 2016/12/5 Time: 下午2:23
 */
public class Modules {

    private static final Logger logger = LoggerFactory.getLogger(Modules.class);

    /**
     * return the module which the element belongs to
     * @param element psi element
     * @return module
     */
    @Nullable
    public static Module getCurrentModuleByElement(@NotNull PsiElement element) {
        return ModuleUtilCore.findModuleForPsiElement(element);
    }

    @SuppressWarnings("ConstantConditions")
    @NotNull
    public static ModuleSetting getModuleSettingByElement(@NotNull PsiElement element) {
        ModuleSetting setting = new ModuleSetting();
        try {
            Module currentModule = getCurrentModuleByElement(element);
            if (currentModule == null) return null;
            setting.setModule(currentModule);
            Element moduleDom = JDOMUtil.load(currentModule.getModuleFile().getCanonicalFile().getInputStream());
            Element moduleManager = JDomSerializationUtil.findComponent(moduleDom, "NewModuleRootManager");
            for (Element sourceFolder : moduleManager.getChild("content").getChildren("sourceFolder")) {
                VirtualFile basePathDir = parseSourceFolderPath(currentModule, sourceFolder.getAttributeValue(URL_ATTRIBUTE));
                String isTestAttr = sourceFolder.getAttributeValue(IS_TEST_SOURCE_ATTRIBUTE);
                if (isTestAttr == null) {
                    String typeAttr = sourceFolder.getAttributeValue(TYPE_ATTRIBUTE);
                    if ("java-resource".equals(typeAttr)) {
                        setting.addResourceSources(basePathDir);
                    } else if ("java-test-resource".equals(typeAttr)) {
                        setting.addResourceTestSources(basePathDir);
                    }
                } else {
                    if (Boolean.parseBoolean(isTestAttr)) {
                        setting.addJavaTestSources(basePathDir);
                    } else {
                        setting.addJavaSources(basePathDir);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("parse module.iml error", e);
        }
        return setting;
    }

    @Nullable
    private static VirtualFile parseSourceFolderPath(Module currentModule, String urlAttr) {
        String moduleDir = PathMacroUtil.getModuleDir(currentModule.getModuleFilePath());
        if (moduleDir == null) return null;
        String absolutePath = urlAttr.replaceFirst("\\$" + PathMacroUtil.MODULE_DIR_MACRO_NAME + "\\$", moduleDir);
        return VirtualFileManager.getInstance().findFileByUrl(absolutePath);
    }
}
