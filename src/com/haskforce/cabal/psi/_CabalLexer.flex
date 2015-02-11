package com.haskforce.cabal.psi;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static com.haskforce.cabal.psi.CabalTypes.*;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.openapi.util.Pair;
import com.intellij.util.containers.Stack;

%%

%{
  private int indent;
  private boolean inIndentation = false;
  private int yycolumn;
  private Stack<Integer> indentationStack;
  public _CabalLexer() {
    this((java.io.Reader)null);
    indentationStack = ContainerUtil.newStack();
  }
%}

%public
%class _CabalLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL="\r"|"\n"|"\r\n"
LINE_WS=[\ \t\f]
WHITE_SPACE=({LINE_WS}|{EOL})+

COMMENT=--([^\^\r\n][^\r\n]*|[\r\n])
VARIDREGEXP=[a-zA-Z_\-0-9']*
NUMBERREGEXP=[0-9]+
PATHREGEXP=[a-zA-Z_/\\\-0-9.]*
ADDRESSREGEXP=[a-zA-Z_\-0-9@]*
CRLF=([\r\n])

%state FINDINDENTATIONCONTEXT, CONFIGNAME

%%
<YYINITIAL> {
     <<EOF>>         {
                          if (inIndentation){
                            inIndentation = false;
                            if (! indentationStack.isEmpty ()){
                              return WHITESPACERBRACETOK;
                            }
                          }
                          return null;
                     }
      [\ \f]          {
                          if (inIndentation){
                            indent++;
                          }
                          return com.intellij.psi.TokenType.WHITE_SPACE;
                      }
      [\t]            {
                          if (inIndentation){
                            indent = indent + (indent + 8) % 8;
                          }
                          return com.intellij.psi.TokenType.WHITE_SPACE;
                      }
    {EOL}           {
                         if (inIndentation){
                           yybegin(FINDINDENTATIONCONTEXT);
                           indent = 0;
                         }
                         return com.intellij.psi.TokenType.WHITE_SPACE;
                    }
  "library"          {
                         yybegin(FINDINDENTATIONCONTEXT);
                         indent = yycolumn;
                         return LIBRARY;
                     }
  "executable"       {
                           yybegin(CONFIGNAME);
                           return EXECUTABLE;
                     }
  "test-suite"       {
                           yybegin(CONFIGNAME);
                           return TEST_SUITE;
                     }
  "type"             { return TYPEKEY; }
  "test-module"      {return TESTMODULEKEY;}
  "main-is"      {return MAINISKEY;}
  "true"             {return TRUE;}
  "false"             {return FALSE;}
  ":"                { return COLON; }
  ","                { return COMMA; }
  "."                { return DOT; }
  "=="            { return EQ;}
  ">="            { return GTEQ;}
  "<="            { return LTEQ;}
  ">"             { return GT;}
  "<"             { return LT;}
  "&&"            { return AND;}
  "name"          { return NAMEKEY; }
  "version"       { return VERSIONKEY; }
  "cabal-version" { return CABALVERSIONKEY; }
  "synopsis"      { return SYNOPSISKEY; }
  "author"        { return AUTHORKEY; }
  "maintainer"    { return MAINTAINERKEY; }
  "category"      { return CATEGORYKEY; }
  "build-type"      { return BUILDTYPEKEY; }
  "default-language"      { return DEFAULTLANGUAGEKEY; }
  "extra-source-files"      { return EXTRASOURCEFILESKEY; }
  "build-depends"      { return BUILDDEPENDSKEY; }
  "other-extensions"      { return OTHEREXTENSIONSKEY; }
  "other-modules"      { return OTHERMODULESKEY; }
  "exposed-modules"      { return EXPOSEDMODULESKEY; }
  "exposed"         { return EXPOSEDKEY; }
  "license-file"       { return LICENSEFILEKEY;}
  "homepage"        { return HOMEPAGEKEY;}
  "bugReports"      { return BUGREPORTSKEY;}
  "package"         { return PACKAGEKEY;}
  "license-files"  { return LICENSEFILESKEY;}
  "data-dir"       {return DATADIRKEY;}
  "stability"      { return STABILITYKEY;}
  "copyright"      {return COPYRIGHTKEY;}
  "author"         {return AUTHORKEY;}
  "data-files"     {return DATAFILESKEY;}
  "tested-with"    {return TESTEDWITHKEY;}
  "maintainer"     {return MAINTAINERKEY;}
  "default-language" {return DEFAULTLANGUAGEKEY;}
  "extra-source-files" {return EXTRASOURCEFILESKEY;}
  "extra-doc-files"    {return EXTRADOCFILESKEY;}
  "extra-tmp-files"    {return EXTRATMPFILESKEY;}
  "build-depends"      {return BUILDDEPENDSKEY;}
  "other-extensions"   {return OTHEREXTENSIONSKEY;}
  "other-modules"      {return OTHERMODULESKEY;}
  "exposed-modules"    {return EXPOSEDMODULESKEY;}
  "hs-source-dirs"     {return HSSOURCEDIRSKEY;}
  "extensions"         {return EXTENSIONSKEY;}
  "ghc-options"        {return GHCOPTIONSKEY;}
  "ghc-prof-options"   {return GHCPROFOPTIONSKEY;}
  "ghc-shared-options" {return GHCSHAREDOPTIONSKEY;}
  "build-tools"        {return BUILDTOOLSKEY;}
  "includes"           {return INCLUDESKEY;}
  "install-includes"   {return INSTALLINCLUDESKEY;}
  "include-dirs"       {return INCLUDEDIRSKEY;}
  "c-sources"          {return CSOURCESKEY;}
  "js-sources"         {return JSSOURCESKEY;}
  "extra-libraries"    {return EXTRALIBRARIESKEY;}
  "extra-ghci-libraries" {return EXTRAGHCILIBRARIESKEY;}
  "extra-lib-dirs"     {return EXTRALIBDIRSKEY;}
  "cc-options"         {return CCOPTIONSKEY;}
  "ld-options"         {return LDOPTIONSKEY;}
  "pkg-config-depends" {return PKGCONFIGDEPENDSKEY;}
  "frameworks"        {return FRAMEWORKSKEY;}
  "buildable"        {return BUILDABLEKEY;}
  {COMMENT}          { return COMMENT; }
  {NUMBERREGEXP}     { return NUMBERREGEXP; }
  {VARIDREGEXP}      { return VARIDREGEXP; }
}

<CONFIGNAME> {
  [\ ]            {return com.intellij.psi.TokenType.WHITE_SPACE;}
  {VARIDREGEXP}   {
                     yybegin(FINDINDENTATIONCONTEXT);
                     indent = yycolumn;
                     return VARIDREGEXP;
                  }
}

<FINDINDENTATIONCONTEXT> {
      [\ \f]          {
                          indent++;
                          return com.intellij.psi.TokenType.WHITE_SPACE;
                      }
      [\t]            {
                          indent = indent + (indent + 8) % 8;
                          return com.intellij.psi.TokenType.WHITE_SPACE;
                      }
      [\n]            {
                          indent = 0;
                          return com.intellij.psi.TokenType.WHITE_SPACE;
                      }
      [^]             {
                          yypushback(1);
                          yybegin(YYINITIAL);
                          if (indentationStack.isEmpty()){
                             indentationStack.push(indent);
                             inIndentation = true;
                             return WHITESPACELBRACETOK;
                          }  else {
                             if(indent == indentationStack.peek()){
                               inIndentation = true;
                             } else {
                                   if(indent < indentationStack.peek()){
                                      indentationStack.pop();
                                      if (indentationStack.isEmpty()){
                                        inIndentation = false;

                                      } else {
                                        inIndentation = true;
                                      }
                                      return WHITESPACERBRACETOK;
                                   } /*else {
                                      Do not take multiple indentations into account
                                      not necessary for cabal and just gets us in trouble

                                      inIndentation = true;
                                      return WHITESPACELBRACETOK;
                                   }*/
                             }
                          }
                      }

}


