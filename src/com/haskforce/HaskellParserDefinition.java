package com.haskforce;

import com.haskforce.highlighting.HaskellSyntaxHighlightingLexer;
import com.haskforce.parsing.HaskellParser2;
import com.haskforce.parsing.HaskellTypes2;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;


import com.haskforce.parser.HaskellParser;
import com.haskforce.psi.HaskellFile;
import com.haskforce.psi.HaskellTypes;

public class HaskellParserDefinition implements ParserDefinition {
    private static final boolean pjbuild = false;
    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet COMMENTS = TokenSet.create(HaskellTypes.COMMENT,
            HaskellTypes.COMMENTTEXT, HaskellTypes.OPENCOM, HaskellTypes.CLOSECOM);
    public static final TokenSet STRINGS = TokenSet.create(HaskellTypes.STRINGTOKEN);

    public static final IFileElementType FILE = new IFileElementType(Language.<HaskellLanguage>findInstance(HaskellLanguage.class));

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new HaskellSyntaxHighlightingLexer();
    }

    /**
     * These tokens are filtered out by the PsiBuilder before they reach the
     * parser.
     */
    @NotNull
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    /**
     * These tokens are filtered out by the PsiBuilder before they reach the
     * parser. They are also searched for TODO items.
     */
    @NotNull
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return STRINGS;
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        if (pjbuild || System.getProperty("PARSER", "").equals("2")) {
            return new HaskellParser2(project);
        }
        return new HaskellParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new HaskellFile(viewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        if (pjbuild || System.getProperty("PARSER", "").equals("2")) {
            return HaskellTypes2.Factory.createElement(node);
        }
        return HaskellTypes.Factory.createElement(node);
    }
}
