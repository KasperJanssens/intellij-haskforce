// This is a generated file. Not intended for manual editing.
package com.haskforce.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface HaskellIdecl extends PsiElement {

  @Nullable
  HaskellAtype getAtype();

  @Nullable
  HaskellCon getCon();

  @NotNull
  List<HaskellConstr> getConstrList();

  @Nullable
  HaskellContext getContext();

  @NotNull
  List<HaskellCtype> getCtypeList();

  @Nullable
  HaskellFunorpatdecl getFunorpatdecl();

  @Nullable
  HaskellKind getKind();

  @Nullable
  HaskellOqtycon getOqtycon();

  @Nullable
  HaskellQtycls getQtycls();

  @Nullable
  HaskellTypee getTypee();

  @Nullable
  HaskellVars getVars();

  @Nullable
  PsiElement getDoublearrow();

  @Nullable
  PsiElement getEquals();

  @Nullable
  PsiElement getExclamation();

  @Nullable
  PsiElement getLparen();

  @Nullable
  PsiElement getRparen();

  @Nullable
  PsiElement getSemicolon();

}
