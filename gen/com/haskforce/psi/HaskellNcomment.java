// This is a generated file. Not intended for manual editing.
package com.haskforce.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface HaskellNcomment extends PsiElement {

  @NotNull
  List<HaskellAny> getAnyList();

  @NotNull
  List<HaskellNcomment> getNcommentList();

  @NotNull
  List<HaskellSeq> getSeqList();

  @NotNull
  PsiElement getClosecom();

  @NotNull
  PsiElement getOpencom();

}
