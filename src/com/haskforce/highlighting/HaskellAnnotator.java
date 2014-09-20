package com.haskforce.highlighting;

import com.haskforce.psi.*;
import com.haskforce.quickfixes.HaskellModuleFilenameFix;
import com.haskforce.quickfixes.HaskellModuleNameFix;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * Annotator that:
 *
 * 1) brushes up syntax highlighting issues from parsing a broken program.
 * 2) Registers quickfixes on broken nodes.
 */
public class HaskellAnnotator implements Annotator {
    private static final String MSG = "File and module name differs";
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull final AnnotationHolder holder) {
        element.accept(new HaskellVisitor() {
            @Override
            public void visitPpragma(@NotNull HaskellPpragma o) {
                super.visitPpragma(o);
                setHighlighting(o, holder, HaskellSyntaxHighlighter.PRAGMA);
            }

            @Override
            public void visitQvarid(@NotNull HaskellQvarid o) {
                super.visitQvarid(o);
                setHighlighting(o, holder, HaskellSyntaxHighlighter.VARID);
            }

            @Override
            public void visitQvarsym(@NotNull HaskellQvarsym o) {
                super.visitQvarsym(o);
                setHighlighting(o, holder, HaskellSyntaxHighlighter.VARSYM);
            }

            @Override
            public void visitQconsym(@NotNull HaskellQconsym o) {
                super.visitQconsym(o);
                setHighlighting(o, holder, HaskellSyntaxHighlighter.CONSYM);
            }

            @Override
            public void visitPstringtoken(@NotNull HaskellPstringtoken o) {
                super.visitPstringtoken(o);
                setHighlighting(o, holder, HaskellSyntaxHighlighter.STRING);
            }

            @Override
            public void visitModuledecl(@NotNull HaskellModuledecl o) {
                super.visitModuledecl(o);
                final HaskellQconid qc = o.getQconid();
                if (qc != null) {
                    String moduleName = qc.getConid().getText();
                    String fullFileName = o.getContainingFile().getName();
                    //noinspection ConstantConditions
                    if (fullFileName != null) {
                        String fileSuffix = fullFileName.substring(fullFileName.lastIndexOf('.'));
                        String fileName = fullFileName.substring(0, fullFileName.length() - fileSuffix.length());
                        if (!moduleName.equals(fileName) && !"Main".equals(moduleName)) {
                            HaskellModuleFilenameFix fixFile = new HaskellModuleFilenameFix(moduleName + fileSuffix);
                            final HaskellQconid qc2 = o.getQconid();
                            if (qc2 != null) {
                                HaskellModuleNameFix fixName = new HaskellModuleNameFix(qc2.getConid(), fileName);
                                holder.createErrorAnnotation(qc, MSG).registerFix(fixFile);
                                holder.createErrorAnnotation(qc, MSG).registerFix(fixName);
                            }
                        }
                    }
                }
            }
        });
    }

    private static void setHighlighting(@NotNull PsiElement element, @NotNull AnnotationHolder holder,
                                        @NotNull TextAttributesKey key) {
        holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(
                EditorColorsManager.getInstance().getGlobalScheme().getAttributes(key));
    }
}
