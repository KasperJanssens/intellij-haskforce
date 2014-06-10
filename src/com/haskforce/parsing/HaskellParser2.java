package com.haskforce.parsing;

import com.haskforce.parsing.jsonParser.JsonParser;
import com.haskforce.parsing.srcExtsDatatypes.*;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import static com.haskforce.parsing.HaskellTypes2.*;
// These can be imported as * when the old parser is removed.
import static com.haskforce.psi.HaskellTypes.OPENPRAGMA;
import static com.haskforce.psi.HaskellTypes.CLOSEPRAGMA;
import static com.haskforce.psi.HaskellTypes.OPENCOM;
import static com.haskforce.psi.HaskellTypes.CLOSECOM;
import static com.haskforce.psi.HaskellTypes.CPP;
import static com.haskforce.psi.HaskellTypes.CPPIF;
import static com.haskforce.psi.HaskellTypes.CPPELSE;
import static com.haskforce.psi.HaskellTypes.CPPENDIF;
import static com.haskforce.psi.HaskellTypes.COMMENT;
import static com.haskforce.psi.HaskellTypes.COMMENTTEXT;
import static com.haskforce.psi.HaskellTypes.DOUBLEQUOTE;
import static com.haskforce.psi.HaskellTypes.STRINGTOKEN;
import static com.haskforce.psi.HaskellTypes.BADSTRINGTOKEN;
import static com.haskforce.psi.HaskellTypes.MODULE;
import static com.haskforce.psi.HaskellTypes.WHERE;
import static com.haskforce.psi.HaskellTypes.PRAGMA;
import static com.haskforce.psi.HaskellTypes.EQUALS;
import static com.haskforce.psi.HaskellTypes.IMPORT;
import static com.haskforce.psi.HaskellTypes.QUALIFIED;
import static com.haskforce.psi.HaskellTypes.HIDING;
import static com.haskforce.psi.HaskellTypes.PERIOD;
import static com.haskforce.psi.HaskellTypes.RPAREN;
import static com.haskforce.psi.HaskellTypes.LPAREN;
import static com.haskforce.psi.HaskellTypes.AS;

/**
 * New Parser using parser-helper.
 */
public class HaskellParser2 implements PsiParser {
    private static final Logger LOG = Logger.getInstance(HaskellParser2.class);
    private final Project myProject;
    private final JsonParser myJsonParser;

    public HaskellParser2(@NotNull Project project) {
        myProject = project;
        myJsonParser = new JsonParser(project);
    }

    @NotNull
    @Override
    public ASTNode parse(IElementType root, PsiBuilder builder) {
        PsiBuilder.Marker rootMarker = builder.mark();
        TopPair tp = myJsonParser.parse(builder.getOriginalText());
        if (tp.error != null && !tp.error.isEmpty()) {
            // TODO: Parse failed. Possibly warn. Could be annoying.
        }

        IElementType e = builder.getTokenType();
        while (!builder.eof() && (e == COMMENT
                || e == CPP || e == OPENCOM)) {
            if (e == COMMENT || e == OPENCOM) {
                e = parseComment(e, builder, tp.comments);
            } else if (e == CPPIF || e == CPPELSE || e == CPPENDIF) {
                // Ignore CPP-tokens, they are not fed to parser-helper anyways.
                builder.advanceLexer();
                e = builder.getTokenType();
            }
        }
        parseModule(builder, (Module) tp.moduleType, tp.comments);
        return chewEverything(rootMarker, root, builder);
    }

    private static ASTNode chewEverything(PsiBuilder.Marker marker, IElementType e, PsiBuilder builder) {
        while (!builder.eof()) {
            builder.advanceLexer();
        }
        marker.done(e);
        ASTNode result = builder.getTreeBuilt();
        // System.out.println("Psifile:" + builder.getTreeBuilt().getPsi().getContainingFile().getName());
        return result;
    }

    /**
     * Parses a complete module.
     */
    private static void parseModule(PsiBuilder builder, Module module, Comment[] comments) {
        parseModulePragmas(builder, module == null ? null : module.modulePragmas, comments);
        parseModuleHead(builder, module == null ? null : module.moduleHeadMaybe, comments);
        parseImportDecls(builder, module == null ? null : module.importDecls, comments);
        parseBody(builder, module == null ? null : module.decls, comments);
    }

    /**
     * Parses "module NAME [exportSpecList] where".
     */
    private static void parseModuleHead(PsiBuilder builder, ModuleHead head, Comment[] comments) {
        IElementType e = builder.getTokenType();
        if (e != MODULE) return;

        PsiBuilder.Marker moduleMark = builder.mark();
        consumeToken(builder, MODULE);
        parseModuleName(builder, head == null ? null : head.moduleName, comments);
        consumeToken(builder, WHERE);
        // TODO: exportSpecList
        moduleMark.done(e);
    }

    private static void parseModuleName(PsiBuilder builder, ModuleName name,  Comment[] comments) {
        builder.getTokenType(); // Need to getTokenType to advance lexer over whitespace.
        int startPos = builder.getCurrentOffset();
        IElementType e = builder.getTokenType();
        // Data.Maybe is a legal module name.
        while ((name != null &&
               (builder.getCurrentOffset() - startPos) <  name.name.length()) ||
                name == null && e != WHERE) {
            builder.remapCurrentToken(NAME);
            consumeToken(builder, NAME);
            e = builder.getTokenType();
            if (e == PERIOD) builder.advanceLexer();
        }
    }

    /**
     * Parses a list of import statements.
     */
    private static void parseImportDecls(PsiBuilder builder, ImportDecl[] importDecls, Comment[] comments) {
        IElementType e = builder.getTokenType();

        int i = 0;
        while (importDecls != null && i < importDecls.length) {
            if (e == CPPIF || e == CPPELSE || e == CPPENDIF) {
                builder.advanceLexer();
                e = builder.getTokenType();
                continue;
            }
            if (e != IMPORT) return;

            parseImportDecl(builder, importDecls[i], comments);
            i++;
            e = builder.getTokenType();
        }
    }

    /**
     * Parses an import statement.
     */
    private static void parseImportDecl(PsiBuilder builder, ImportDecl importDecl, Comment[] comments) {
        IElementType e = builder.getTokenType();
        PsiBuilder.Marker importMark = builder.mark();
        consumeToken(builder, IMPORT);
        IElementType e2 = builder.getTokenType();
        if (e2 == QUALIFIED || (importDecl != null && importDecl.importQualified)) {
            consumeToken(builder, QUALIFIED);
        }
        parseModuleName(builder, importDecl == null ? null : importDecl.importModule, comments);
        e2 = builder.getTokenType();
        if (e2 == AS || false) { // TODO: Update.
            consumeToken(builder, AS);
            e2 = builder.getTokenType();
            parseModuleName(builder, importDecl == null ? null : importDecl.importAs, comments);
            e2 = builder.getTokenType();
        }
        if (e2 == HIDING || false) { // (importDecl != null && importDecl.importSpecs)) { TODO: FIXME
            consumeToken(builder, HIDING);
            e2 = builder.getTokenType();
        }
        int nest = 0;
        if (e2 == LPAREN) {
            consumeToken(builder, LPAREN);
            nest++;
        }
        while (nest > 0) {
            builder.advanceLexer();
            e2 = builder.getTokenType();
            if (e2 == LPAREN) {
                nest++;
            }
            if (e2 == RPAREN) {
                nest--;
            }
        }
        if (e2 == RPAREN) consumeToken(builder, RPAREN);
        importMark.done(e);
    }

    private static void parseBody(PsiBuilder builder, DeclTopType[] decls, Comment[] comments) {
        int i = 0;
        while (decls != null && i < decls.length) {
            parseDecl(builder, decls[i], comments);
            i++;
        }
    }

    private static void parseDecl(PsiBuilder builder, DeclTopType decl, Comment[] comments) {
        IElementType e = builder.getTokenType();
        PsiBuilder.Marker declMark = builder.mark();
        if (decl instanceof PatBind) {
            parsePatBind(builder, (PatBind) decl, comments);
        } else if (decl instanceof FunBind) {
            parseFunBind(builder, (FunBind) decl, comments);
        } else {
            throw new RuntimeException("Unexpected decl type: " + decl.toString());
        }
        declMark.done(e);
    }

    private static void parsePatBind(PsiBuilder builder, PatBind patBind, Comment[] comments) {
        IElementType e = builder.getTokenType();
        parsePatTop(builder, patBind.pat, comments);
        if (patBind.type != null) throw new RuntimeException("Unexpected type in patbind");
        // TODO: parseType(builder, patBind.type, comments);
        parseRhs(builder, patBind.rhs, comments);
        if (patBind.binds != null) throw new RuntimeException("Unexpected binds in patbind");
    }

    private static void parseFunBind(PsiBuilder builder, FunBind funBind, Comment[] comments) {
        IElementType e = builder.getTokenType();
        int i = 0;
        while (funBind.match != null && i < funBind.match.length) {
            parseMatchTop(builder, funBind.match[i], comments);
            i++;
        }
        System.out.println("Unexpected FunBind type " + funBind.toString());
    }

    private static void parseMatchTop(PsiBuilder builder, MatchTopType matchTopType, Comment[] comments) {
        IElementType e = builder.getTokenType();
        if (matchTopType instanceof Match) {
            parseMatch(builder, (Match) matchTopType, comments);
        } else if (matchTopType instanceof InfixMatch) {
            //TODO: parseInfixMatch(builder, (InfixMatch) matchTopType, comments);
            throw new RuntimeException("infixmatch");
        }
        System.out.println("Unexpected matchTopType type " + matchTopType.toString());
    }

    private static void parseMatch(PsiBuilder builder, Match match, Comment[] comments) {
        IElementType e = builder.getTokenType();
        parseName(builder, match.name, comments);
        int i = 0;
        while (match.pats != null && i < match.pats.length) {
            parsePatTop(builder, match.pats[i], comments);
            i++;
        }
        parseRhs(builder, match.rhs, comments);
        System.out.println("Unexpected Match type " + match.toString());
    }

    private static void parsePatTop(PsiBuilder builder, PatTopType patTopType, Comment[] comments) {
        IElementType e = builder.getTokenType();
        if (patTopType instanceof PVar) {
            parsePVar(builder, (PVar) patTopType, comments);
        } else {
            throw new RuntimeException("parsePatTop" + patTopType.toString());
        }
    }

    private static IElementType parseComment(IElementType e, PsiBuilder builder, Comment[] comments) {
        while (e == COMMENT || e == COMMENTTEXT ||
                e == OPENCOM || e == CLOSECOM) {
            builder.advanceLexer();
            e = builder.getTokenType();
        }
        return e;
    }

    /**
     * Parses a group of module pragmas.
     */
    private static void parseModulePragmas(PsiBuilder builder, ModulePragmaTopType[] modulePragmas,  Comment[] comments) {
        int i = 0;
        while(modulePragmas != null && i < modulePragmas.length) {
            parseModulePragma(builder, modulePragmas[i], comments);
            i++;
        }
    }

    /**
     * Parses a module pragma.
     */
    private static void parseModulePragma(PsiBuilder builder, ModulePragmaTopType modulePragmaTopType,  Comment[] comments) {
        int i = 0;
        if (modulePragmaTopType instanceof LanguagePragma) {
            LanguagePragma langPragma = (LanguagePragma) modulePragmaTopType;
            IElementType e = builder.getTokenType();
            PsiBuilder.Marker pragmaMark = builder.mark();
            consumeToken(builder, OPENPRAGMA);
            consumeToken(builder, PRAGMA);
            while (langPragma.names != null && i < langPragma.names.length) {
                parseName(builder, langPragma.names[i], comments);
                i++;
            }
            consumeToken(builder, CLOSEPRAGMA);
            pragmaMark.done(e);
        } else if (modulePragmaTopType instanceof OptionsPragma) {
            throw new RuntimeException("parseModulePragma" + modulePragmaTopType.toString());
        } else if (modulePragmaTopType instanceof AnnModulePragma) {
            throw new RuntimeException("parseModulePragma" + modulePragmaTopType.toString());
        }
    }

    /**
     * Parses a pattern variable.
     */
    private static void parsePVar(PsiBuilder builder, PVar pVar,  Comment[] comments) {
        builder.remapCurrentToken(VARID); // FIXME: Should be PVARID
        consumeToken(builder, VARID);
    }

    private static void parseRhs(PsiBuilder builder, RhsTopType rhsTopType,  Comment[] comments) {
        consumeToken(builder, EQUALS);
        if (rhsTopType instanceof UnGuardedRhs) {
            parseExpTopType(builder, ((UnGuardedRhs) rhsTopType).exp, comments);
        } else if (rhsTopType instanceof GuardedRhss) {
            throw new RuntimeException("GuardedRhss" + rhsTopType.toString());
        }
    }

    /**
     * Parses a qualified name.
     */
    private static void parseQName(PsiBuilder builder, QNameTopType qNameTopType,  Comment[] comments) {
        if (qNameTopType instanceof Qual) {
            Qual name = (Qual) qNameTopType;
            parseModuleName(builder, name.moduleName, comments);
            parseName(builder, name.name, comments);
        } else if (qNameTopType instanceof UnQual) {
            parseName(builder, ((UnQual) qNameTopType).name, comments);
        } else if (qNameTopType instanceof Special) {
            // TODO: parseSpecialCon(builder, ((Special) qNameTopType).specialCon, comments);
            throw new RuntimeException("QName-special" + qNameTopType.toString());
        }
    }

    /**
     * Parses a name.
     */
    private static void parseName(PsiBuilder builder, NameTopType nameTopType,  Comment[] comments) {
        if (nameTopType instanceof Ident) {
            builder.remapCurrentToken(NAME);
            consumeToken(builder, NAME);
        } else if (nameTopType instanceof Symbol) {
            builder.remapCurrentToken(SYMBOL);
            consumeToken(builder, SYMBOL);
        }
    }

    /**
     * Parses a literal
     */
    private static void parseLiteralTop(PsiBuilder builder, LiteralTopType literalTopType,  Comment[] comments) {
        IElementType e = builder.getTokenType();
        if (literalTopType instanceof StringLit) {
            PsiBuilder.Marker marker = builder.mark();
            consumeToken(builder, DOUBLEQUOTE);
            IElementType e2 = builder.getTokenType();
            while (e2 != DOUBLEQUOTE) {
                if (e2 == BADSTRINGTOKEN) {
                    builder.error("Bad stringtoken");
                    builder.advanceLexer();
                } else {
                    consumeToken(builder, STRINGTOKEN);
                }
                e2 = builder.getTokenType();
            }
            consumeToken(builder, DOUBLEQUOTE);
            marker.done(e);
        } else {
            throw new RuntimeException("LiteralTop: " + literalTopType.toString());
        }
    }

    /**
     * Parses an expression.
     */
    private static void parseExpTopType(PsiBuilder builder, ExpTopType expTopType, Comment[] comments) {
        builder.getTokenType();
        if (expTopType instanceof App) {
            parseExpTopType(builder, ((App) expTopType).e1, comments);
            parseExpTopType(builder, ((App) expTopType).e2, comments);
        } else if (expTopType instanceof Var) {
            parseQName(builder, ((Var) expTopType).qName, comments);
        } else if (expTopType instanceof Lit) {
            parseLiteralTop(builder, ((Lit) expTopType).literal, comments);
        } else {
            throw new RuntimeException("parseExpTopType: " + expTopType.toString());
        }
    }


    public static boolean consumeToken(PsiBuilder builder_, IElementType token) {
        if (nextTokenIsInner(builder_, token)) {
            builder_.advanceLexer();
            return true;
        }
        return false;
    }

    public static boolean nextTokenIsInner(PsiBuilder builder_, IElementType token) {
        IElementType tokenType = builder_.getTokenType();
        if (token != tokenType) {
            System.out.println("Unexpected token: " + tokenType + " vs " + token);
        }
        return token == tokenType;
    }
}
