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
import com.intellij.psi.PsiReference;

public class HaskellConidImpl extends ASTWrapperPsiElement implements HaskellConid {

  public HaskellConidImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HaskellVisitor) ((HaskellVisitor)visitor).visitConid(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getConidRegexp() {
    return findNotNullChildByType(CONIDREGEXP);
  }

  @NotNull
  public String getName() {
    return HaskellPsiImplUtil.getName(this);
  }

  @NotNull
  public PsiReference getReference() {
    return HaskellPsiImplUtil.getReference(this);
  }

  @NotNull
  public PsiElement setName(String newName) {
    return HaskellPsiImplUtil.setName(this, newName);
  }

}
