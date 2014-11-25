// This is a generated file. Not intended for manual editing.
package com.haskforce.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.haskforce.psi.HaskellTypes.*;
import com.haskforce.psi.*;

public class HaskellKindImpl extends HaskellCompositeElementImpl implements HaskellKind {

  public HaskellKindImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HaskellVisitor) ((HaskellVisitor)visitor).visitKind(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public HaskellBkind getBkind() {
    return findNotNullChildByClass(HaskellBkind.class);
  }

  @Override
  @Nullable
  public HaskellKind getKind() {
    return findChildByClass(HaskellKind.class);
  }

  @Override
  @Nullable
  public PsiElement getRightarrow() {
    return findChildByType(RIGHTARROW);
  }

}
