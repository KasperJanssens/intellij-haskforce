// This is a generated file. Not intended for manual editing.
package com.haskforce.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface HaskellStringtoken extends PsiElement {

  @NotNull
  List<HaskellEscape> getEscapeList();

  @NotNull
  List<HaskellGap> getGapList();

  @NotNull
  List<HaskellSymbol> getSymbolList();

  @NotNull
  List<HaskellVarid> getVaridList();

}
