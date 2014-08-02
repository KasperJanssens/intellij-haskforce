package com.haskforce.cabal.highlighting;

import com.haskforce.cabal.psi.CabalTypes;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class CabalSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey COLON = TextAttributesKey.createTextAttributesKey(
            "CABAL_COLON", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey[] COLON_KEYS = new TextAttributesKey[]{COLON};

    public static final TextAttributesKey KEY = TextAttributesKey.createTextAttributesKey(
            "CABAL_KEY", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    public static final TextAttributesKey[] KEY_KEYS = new TextAttributesKey[]{KEY};

    public static final TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey(
            "CABAL_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};

    public static final TextAttributesKey CONFIG = TextAttributesKey.createTextAttributesKey(
            "CABAL_CONFIG", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey[] CONFIG_KEYS = new TextAttributesKey[]{CONFIG};

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new CabalSyntaxHighlightingLexer();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType == CabalTypes.COLON) {
            return COLON_KEYS;
        }
        if (tokenType == CabalTypes.COMMENT) {
            return COMMENT_KEYS;
        }
        if (tokenType == CabalTypes.KEY) {
            return KEY_KEYS;
        }
        if (tokenType == CabalTypes.CONFIG) {
            return CONFIG_KEYS;
        }
        return EMPTY;
    }
}
