// This is a generated file. Not intended for manual editing.
package com.haskforce.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface HaskellRhs extends HaskellCompositeElement {

  @NotNull
  List<HaskellExp> getExpList();

  @Nullable
  HaskellFunorpatdecl getFunorpatdecl();

  @Nullable
  HaskellGendecl getGendecl();

  @NotNull
  List<HaskellGuard> getGuardList();

  @NotNull
  List<HaskellPpragma> getPpragmaList();

  @Nullable
  PsiElement getWhere();

  @Nullable
  PsiElement getWhitespacelbracetok();

  @Nullable
  PsiElement getWhitespacerbracetok();

  @Nullable
  PsiElement getLbrace();

  @Nullable
  PsiElement getRbrace();

}
