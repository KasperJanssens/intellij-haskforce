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

public class HaskellImporttImpl extends ASTWrapperPsiElement implements HaskellImportt {

  public HaskellImporttImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HaskellVisitor) ((HaskellVisitor)visitor).visitImportt(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public HaskellCon getCon() {
    return findChildByClass(HaskellCon.class);
  }

  @Override
  @Nullable
  public HaskellTycon getTycon() {
    return findChildByClass(HaskellTycon.class);
  }

  @Override
  @Nullable
  public HaskellVarid getVarid() {
    return findChildByClass(HaskellVarid.class);
  }

  @Override
  @Nullable
  public HaskellVars getVars() {
    return findChildByClass(HaskellVars.class);
  }

  @Override
  @Nullable
  public HaskellVarsym getVarsym() {
    return findChildByClass(HaskellVarsym.class);
  }

  @Override
  @Nullable
  public PsiElement getDoubleperiod() {
    return findChildByType(DOUBLEPERIOD);
  }

}
