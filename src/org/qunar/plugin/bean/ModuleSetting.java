/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.bean;

import com.google.common.collect.Lists;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * resole part of module setting
 * 
 * Author: jianyu.lin
 * Date: 2016/12/5 Time: 下午7:51
 */
public class ModuleSetting {
    
    private Module module; 
    
    private List<VirtualFile> javaSources = Lists.newArrayList();
    private List<VirtualFile> javaTestSources = Lists.newArrayList();
    private List<VirtualFile> resourceSources = Lists.newArrayList();
    private List<VirtualFile> resourceTestSources = Lists.newArrayList();

    public ModuleSetting addJavaSources(VirtualFile javaSource) {
        if (javaSource != null) {
            javaSources.add(javaSource);
        }
        return this;
    }

    public ModuleSetting addJavaTestSources(VirtualFile javaTestSource) {
        if (javaTestSource != null) {
            javaTestSources.add(javaTestSource);
        }
        return this;
    }

    public ModuleSetting addResourceSources(VirtualFile resourceSource) {
        if (resourceSource != null) {
            resourceSources.add(resourceSource);
        }
        return this;
    }

    public ModuleSetting addResourceTestSources(VirtualFile resourceTestSource) {
        if (resourceTestSource != null) {
            resourceTestSources.add(resourceTestSource);
        }
        return this;
    }

    public List<VirtualFile> getJavaSources() {
        return javaSources;
    }

    public List<VirtualFile> getJavaTestSources() {
        return javaTestSources;
    }

    public List<VirtualFile> getResourceSources() {
        return resourceSources;
    }

    public List<VirtualFile> getResourceTestSources() {
        return resourceTestSources;
    }

    @Nullable
    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public List<VirtualFile> getAllResourceSources() {
        List<VirtualFile> resources = Lists.newArrayList();
        resources.addAll(resourceSources);
        resources.addAll(resourceTestSources);
        return resources;
    }

    @Override
    public String toString() {
        return "ModuleSetting{" +
                "javaSources=" + javaSources +
                ", javaTestSources=" + javaTestSources +
                ", resourceSources=" + resourceSources +
                ", resourceTestSources=" + resourceTestSources +
                '}';
    }
}
