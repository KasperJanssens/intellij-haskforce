// This is a generated file. Not intended for manual editing.
package com.haskforce.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface HaskellGuard extends PsiElement {

  @Nullable
  HaskellInfixexp getInfixexp();

  @NotNull
  List<HaskellNcomment> getNcommentList();

  @Nullable
  HaskellPat getPat();

  @NotNull
  List<HaskellPragma> getPragmaList();

  @NotNull
  List<HaskellPstringtoken> getPstringtokenList();

  @NotNull
  List<HaskellQconid> getQconidList();

  @NotNull
  List<HaskellQconsym> getQconsymList();

  @NotNull
  List<HaskellQinfixconid> getQinfixconidList();

  @NotNull
  List<HaskellQinfixvarid> getQinfixvaridList();

  @NotNull
  List<HaskellQvarid> getQvaridList();

  @NotNull
  List<HaskellQvarsym> getQvarsymList();

  @NotNull
  List<HaskellReservedop> getReservedopList();

  @NotNull
  List<HaskellSpecial> getSpecialList();

  @NotNull
  List<HaskellWhitechar> getWhitecharList();

  @Nullable
  PsiElement getLeftarrow();

}
