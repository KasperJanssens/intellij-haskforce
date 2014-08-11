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

public class HaskellGuardImpl extends ASTWrapperPsiElement implements HaskellGuard {

  public HaskellGuardImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HaskellVisitor) ((HaskellVisitor)visitor).visitGuard(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<HaskellAlt> getAltList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, HaskellAlt.class);
  }

  @Override
  @Nullable
  public HaskellClassdecl getClassdecl() {
    return findChildByClass(HaskellClassdecl.class);
  }

  @Override
  @Nullable
  public HaskellCtype getCtype() {
    return findChildByClass(HaskellCtype.class);
  }

  @Override
  @Nullable
  public HaskellDatadecl getDatadecl() {
    return findChildByClass(HaskellDatadecl.class);
  }

  @Override
  @Nullable
  public HaskellDefaultdecl getDefaultdecl() {
    return findChildByClass(HaskellDefaultdecl.class);
  }

  @Override
  @Nullable
  public HaskellDerivingdecl getDerivingdecl() {
    return findChildByClass(HaskellDerivingdecl.class);
  }

  @Override
  @Nullable
  public HaskellExp getExp() {
    return findChildByClass(HaskellExp.class);
  }

  @Override
  @Nullable
  public HaskellForeigndecl getForeigndecl() {
    return findChildByClass(HaskellForeigndecl.class);
  }

  @Override
  @Nullable
  public HaskellFunorpatdecl getFunorpatdecl() {
    return findChildByClass(HaskellFunorpatdecl.class);
  }

  @Override
  @Nullable
  public HaskellGendecl getGendecl() {
    return findChildByClass(HaskellGendecl.class);
  }

  @Override
  @Nullable
  public HaskellInstancedecl getInstancedecl() {
    return findChildByClass(HaskellInstancedecl.class);
  }

  @Override
  @Nullable
  public HaskellNewtypedecl getNewtypedecl() {
    return findChildByClass(HaskellNewtypedecl.class);
  }

  @Override
  @Nullable
  public HaskellPat getPat() {
    return findChildByClass(HaskellPat.class);
  }

  @Override
  @Nullable
  public HaskellPpragma getPpragma() {
    return findChildByClass(HaskellPpragma.class);
  }

  @Override
  @NotNull
  public List<HaskellPstringtoken> getPstringtokenList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, HaskellPstringtoken.class);
  }

  @Override
  @NotNull
  public List<HaskellQcon> getQconList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, HaskellQcon.class);
  }

  @Override
  @NotNull
  public List<HaskellQop> getQopList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, HaskellQop.class);
  }

  @Override
  @NotNull
  public List<HaskellQvar> getQvarList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, HaskellQvar.class);
  }

  @Override
  @Nullable
  public HaskellStmts getStmts() {
    return findChildByClass(HaskellStmts.class);
  }

  @Override
  @Nullable
  public HaskellTypedecl getTypedecl() {
    return findChildByClass(HaskellTypedecl.class);
  }

  @Override
  @NotNull
  public List<HaskellVarid> getVaridList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, HaskellVarid.class);
  }

  @Override
  @NotNull
  public List<HaskellVarsym> getVarsymList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, HaskellVarsym.class);
  }

  @Override
  @Nullable
  public PsiElement getBackslash() {
    return findChildByType(BACKSLASH);
  }

  @Override
  @Nullable
  public PsiElement getDoubleperiod() {
    return findChildByType(DOUBLEPERIOD);
  }

  @Override
  @Nullable
  public PsiElement getIdsplice() {
    return findChildByType(IDSPLICE);
  }

  @Override
  @Nullable
  public PsiElement getLbrace() {
    return findChildByType(LBRACE);
  }

  @Override
  @Nullable
  public PsiElement getLeftarrow() {
    return findChildByType(LEFTARROW);
  }

  @Override
  @Nullable
  public PsiElement getLthopen() {
    return findChildByType(LTHOPEN);
  }

  @Override
  @Nullable
  public PsiElement getLunboxparen() {
    return findChildByType(LUNBOXPAREN);
  }

  @Override
  @Nullable
  public PsiElement getParensplice() {
    return findChildByType(PARENSPLICE);
  }

  @Override
  @Nullable
  public PsiElement getRbrace() {
    return findChildByType(RBRACE);
  }

  @Override
  @Nullable
  public PsiElement getRunboxparen() {
    return findChildByType(RUNBOXPAREN);
  }

  @Override
  @Nullable
  public PsiElement getSemicolon() {
    return findChildByType(SEMICOLON);
  }

  @Override
  @Nullable
  public PsiElement getSinglequote() {
    return findChildByType(SINGLEQUOTE);
  }

  @Override
  @Nullable
  public PsiElement getThquote() {
    return findChildByType(THQUOTE);
  }

}
