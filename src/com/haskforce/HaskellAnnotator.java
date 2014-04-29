package com.haskforce;

import com.haskforce.highlighting.HaskellSyntaxHighlighter;
import com.haskforce.psi.*;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class HaskellAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull final AnnotationHolder holder) {
        element.accept(new HaskellVisitor() {
            @Override
            public void visitNcomment(@NotNull HaskellNcomment o) {
                super.visitNcomment(o);
                setHighlightingRecursive(o, holder, HaskellSyntaxHighlighter.NCOMMENT);
            }

            @Override
            public void visitPragma(@NotNull HaskellPragma o) {
                super.visitPragma(o);
                setHighlightingRecursive(o, holder, HaskellSyntaxHighlighter.PRAGMA);
            }

            @Override
            public void visitQvarid(@NotNull HaskellQvarid o) {
                super.visitQvarid(o);
                setHighlightingRecursive(o, holder, HaskellSyntaxHighlighter.VARID);
            }

            @Override
            public void visitQinfixvarid(@NotNull HaskellQinfixvarid o) {
                super.visitQinfixvarid(o);
                setHighlightingRecursive(o, holder, HaskellSyntaxHighlighter.INFIXVARID);
            }

            @Override
            public void visitQvarsym(@NotNull HaskellQvarsym o) {
                super.visitQvarsym(o);
                setHighlightingRecursive(o, holder, HaskellSyntaxHighlighter.VARSYM);
            }

            @Override
            public void visitQconsym(@NotNull HaskellQconsym o) {
                super.visitQconsym(o);
                setHighlightingRecursive(o, holder, HaskellSyntaxHighlighter.CONSYM);
            }

//            @Override
//            public void visitStringtoken(@NotNull HaskellStringtoken o) {
//                super.visitStringtoken(o);
//                setHighlightingRecursive(o, holder, HaskellSyntaxHighlighter.STRING);
//            }

            @Override
            public void visitReservedop(@NotNull HaskellReservedop o) {
                super.visitReservedop(o);
                setHighlighting(o, holder, HaskellSyntaxHighlighter.RESERVEDOP);
            }
        });
    }

    private static void setHighlighting(@NotNull PsiElement element, @NotNull AnnotationHolder holder,
                                        @NotNull TextAttributesKey key) {
        holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);
        holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(
                EditorColorsManager.getInstance().getGlobalScheme().getAttributes(key));
    }

    private static void setHighlightingRecursive(@NotNull PsiElement element, @NotNull AnnotationHolder holder,
                                                 @NotNull TextAttributesKey key) {
        setHighlighting(element, holder, key);
        for (PsiElement child : element.getChildren()) {
            setHighlightingRecursive(child, holder, key);
        }
    }
}
