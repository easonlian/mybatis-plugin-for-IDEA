/*
* Copyright (c) 2016 Qunar.com. All Rights Reserved.
*/
package org.qunar.plugin.util;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * check dom element and find potential problems
 *
 * Author: jianyu.lin
 * Date: 2016/11/23 Time: 上午11:14
 */
public interface DomChecker<T extends DomElement> {

    /**
     * check dom element
     * @param manager inspection manager
     * @param domElements dom elements
     * @return problems
     */
    @SuppressWarnings("unused")
    @NotNull
    List<ProblemDescriptor> check(@NotNull InspectionManager manager, @NotNull List<T> domElements);
}