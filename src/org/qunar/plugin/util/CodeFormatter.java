package org.qunar.plugin.util;

import com.intellij.formatting.FormatTextRanges;
import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.impl.source.codeStyle.CodeFormatterFacade;
import org.jetbrains.annotations.NotNull;

/**
 * format code util
 * 
 * Author: jianyu.lin
 * Date: 16-12-1 Time: 下午11:49
 */
public class CodeFormatter {

    /**
     * format code
     * @param psiElement psi element
     * @param language format language
     */
    public static void format(@NotNull PsiElement psiElement, @NotNull Language language) {
        PsiFile psiFile = psiElement.getContainingFile();
        CodeFormatterFacade formatterFacade = new CodeFormatterFacade(new CodeStyleSettings(), language);
        formatterFacade.processText(psiFile, new FormatTextRanges(psiElement.getTextRange(), true), true);
    }

    /**
     * format code
     * @param psiElement psi element
     */
    public static void format(@NotNull PsiElement psiElement) {
        format(psiElement, psiElement.getLanguage());
    }
}
