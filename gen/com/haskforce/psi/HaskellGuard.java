// This is a generated file. Not intended for manual editing.
package com.haskforce.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface HaskellGuard extends PsiElement {

  @Nullable
  HaskellAlt getAlt();

  @Nullable
  HaskellClassdecl getClassdecl();

  @NotNull
  List<HaskellCpp> getCppList();

  @Nullable
  HaskellCtype getCtype();

  @Nullable
  HaskellDatadecl getDatadecl();

  @Nullable
  HaskellDefaultdecl getDefaultdecl();

  @Nullable
  HaskellDerivingdecl getDerivingdecl();

  @NotNull
  List<HaskellExp> getExpList();

  @Nullable
  HaskellForeigndecl getForeigndecl();

  @Nullable
  HaskellFunorpatdecl getFunorpatdecl();

  @Nullable
  HaskellGendecl getGendecl();

  @Nullable
  HaskellInstancedecl getInstancedecl();

  @Nullable
  HaskellNewtypedecl getNewtypedecl();

  @NotNull
  List<HaskellPat> getPatList();

  @Nullable
  HaskellPpragma getPpragma();

  @NotNull
  List<HaskellPstringtoken> getPstringtokenList();

  @NotNull
  List<HaskellQcon> getQconList();

  @NotNull
  List<HaskellQop> getQopList();

  @NotNull
  List<HaskellQvar> getQvarList();

  @Nullable
  HaskellStmts getStmts();

  @Nullable
  HaskellTypedecl getTypedecl();

  @NotNull
  List<HaskellVarid> getVaridList();

  @NotNull
  List<HaskellVarsym> getVarsymList();

  @Nullable
  PsiElement getBackslash();

  @Nullable
  PsiElement getDoubleperiod();

  @Nullable
  PsiElement getIdsplice();

  @Nullable
  PsiElement getLbrace();

  @Nullable
  PsiElement getLthopen();

  @Nullable
  PsiElement getLunboxparen();

  @Nullable
  PsiElement getParensplice();

  @Nullable
  PsiElement getRbrace();

  @Nullable
  PsiElement getRunboxparen();

  @Nullable
  PsiElement getSemicolon();

  @Nullable
  PsiElement getSinglequote();

  @Nullable
  PsiElement getThquote();

}
