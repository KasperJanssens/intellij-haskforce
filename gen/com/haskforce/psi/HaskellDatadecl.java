// This is a generated file. Not intended for manual editing.
package com.haskforce.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface HaskellDatadecl extends PsiElement {

  @Nullable
  HaskellAtype getAtype();

  @NotNull
  List<HaskellCon> getConList();

  @NotNull
  List<HaskellConstr> getConstrList();

  @NotNull
  List<HaskellContext> getContextList();

  @Nullable
  HaskellCtype getCtype();

  @Nullable
  HaskellKind getKind();

  @Nullable
  HaskellOqtycon getOqtycon();

  @NotNull
  List<HaskellQtycls> getQtyclsList();

  @NotNull
  List<HaskellTypee> getTypeeList();

  @Nullable
  HaskellVars getVars();

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
