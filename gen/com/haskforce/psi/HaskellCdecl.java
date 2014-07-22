// This is a generated file. Not intended for manual editing.
package com.haskforce.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface HaskellCdecl extends PsiElement {

  @NotNull
  List<HaskellCtype> getCtypeList();

  @Nullable
  HaskellGendecl getGendecl();

  @Nullable
  HaskellKind getKind();

  @NotNull
  List<HaskellPat> getPatList();

  @NotNull
  List<HaskellPstringtoken> getPstringtokenList();

  @NotNull
  List<HaskellQcon> getQconList();

  @NotNull
  List<HaskellQvar> getQvarList();

  @Nullable
  HaskellRhs getRhs();

  @Nullable
  HaskellTypee getTypee();

  @NotNull
  List<HaskellVarid> getVaridList();

  @Nullable
  HaskellVarop getVarop();

  @NotNull
  List<HaskellVarsym> getVarsymList();

  @Nullable
  PsiElement getDoublecolon();

  @Nullable
  PsiElement getEquals();

}
