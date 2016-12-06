/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.mybatis.reference;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.qunar.plugin.mybatis.util.TypeAliasResolver;
import org.qunar.plugin.service.JavaService;
import org.qunar.plugin.service.TypeAliasService;

import java.util.Set;

/**
 * type alias reference
 *
 * Author: jianyu.lin
 * Date: 2016/11/25 Time: 下午6:09
 */
public class TypeAliasNameReference extends PsiReferenceBase<XmlAttributeValue> {

    public TypeAliasNameReference(@NotNull XmlAttributeValue element) {
        super(element);
    }

    @Nullable
    @Override
    public PsiClass resolve() {
        XmlAttributeValue xmlAttributeValue = getElement();
        PsiClass qualifiedClass = JavaService.getInstance(getElement().getProject()).findClass(xmlAttributeValue.getValue());
        return qualifiedClass != null ? qualifiedClass : TypeAliasService
                .INSTANCE(xmlAttributeValue.getProject()).getAliasClassByName(xmlAttributeValue.getValue());
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = getElement().getProject();
        Set<String> registerAliases = TypeAliasResolver.getAllTypeAlias(project).keySet();
        return registerAliases.toArray(new String[registerAliases.size()]);
    }
}
