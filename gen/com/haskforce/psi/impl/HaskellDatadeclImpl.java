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

public class HaskellDatadeclImpl extends ASTWrapperPsiElement implements HaskellDatadecl {

  public HaskellDatadeclImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HaskellVisitor) ((HaskellVisitor)visitor).visitDatadecl(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public HaskellAtype getAtype() {
    return findChildByClass(HaskellAtype.class);
  }

  @Override
  @Nullable
  public HaskellCon getCon() {
    return findChildByClass(HaskellCon.class);
  }

  @Override
  @NotNull
  public List<HaskellConstr> getConstrList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, HaskellConstr.class);
  }

  @Override
  @NotNull
  public List<HaskellContext> getContextList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, HaskellContext.class);
  }

  @Override
  @Nullable
  public HaskellCtype getCtype() {
    return findChildByClass(HaskellCtype.class);
  }

  @Override
  @Nullable
  public HaskellKind getKind() {
    return findChildByClass(HaskellKind.class);
  }

  @Override
  @Nullable
  public HaskellOqtycon getOqtycon() {
    return findChildByClass(HaskellOqtycon.class);
  }

  @Override
  @Nullable
  public HaskellQtycls getQtycls() {
    return findChildByClass(HaskellQtycls.class);
  }

  @Override
  @NotNull
  public List<HaskellTypee> getTypeeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, HaskellTypee.class);
  }

  @Override
  @NotNull
  public List<HaskellTyvar> getTyvarList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, HaskellTyvar.class);
  }

  @Override
  @Nullable
  public HaskellVars getVars() {
    return findChildByClass(HaskellVars.class);
  }

  @Override
  @Nullable
  public PsiElement getEquals() {
    return findChildByType(EQUALS);
  }

  @Override
  @Nullable
  public PsiElement getExclamation() {
    return findChildByType(EXCLAMATION);
  }

  @Override
  @Nullable
  public PsiElement getLparen() {
    return findChildByType(LPAREN);
  }

  @Override
  @Nullable
  public PsiElement getRparen() {
    return findChildByType(RPAREN);
  }

  @Override
  @Nullable
  public PsiElement getSemicolon() {
    return findChildByType(SEMICOLON);
  }

}
