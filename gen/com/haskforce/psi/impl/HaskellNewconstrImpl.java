// This is a generated file. Not intended for manual editing.
package com.haskforce.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.haskforce.psi.HaskellTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.haskforce.psi.*;

public class HaskellNewconstrImpl extends ASTWrapperPsiElement implements HaskellNewconstr {

  public HaskellNewconstrImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HaskellVisitor) ((HaskellVisitor)visitor).visitNewconstr(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public HaskellAtype getAtype() {
    return findChildByClass(HaskellAtype.class);
  }

  @Override
  @NotNull
  public HaskellCon getCon() {
    return findNotNullChildByClass(HaskellCon.class);
  }

  @Override
  @Nullable
  public HaskellTypee getTypee() {
    return findChildByClass(HaskellTypee.class);
  }

  @Override
  @Nullable
  public HaskellVar getVar() {
    return findChildByClass(HaskellVar.class);
  }

}
