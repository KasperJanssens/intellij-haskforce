// This is a generated file. Not intended for manual editing.
package com.haskforce.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface HaskellLetexp extends HaskellCompositeElement {

  @NotNull
  HaskellExp getExp();

  @NotNull
  List<HaskellFunorpatdecl> getFunorpatdeclList();

  @NotNull
  List<HaskellGendecl> getGendeclList();

  @NotNull
  List<HaskellPpragma> getPpragmaList();

  @NotNull
  PsiElement getIn();

  @NotNull
  PsiElement getLet();

  @Nullable
  PsiElement getWhitespacelbracetok();

  @Nullable
  PsiElement getWhitespacerbracetok();

  @Nullable
  PsiElement getLbrace();

  @Nullable
  PsiElement getRbrace();

}
