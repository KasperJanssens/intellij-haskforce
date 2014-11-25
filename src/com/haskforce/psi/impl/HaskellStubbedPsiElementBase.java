package com.haskforce.psi.impl;

import com.haskforce.psi.HaskellCompositeElement;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;

public abstract class HaskellStubbedPsiElementBase<T extends StubElement<?>> extends StubBasedPsiElementBase<T> implements HaskellCompositeElement {
    public HaskellStubbedPsiElementBase(T stub, IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public HaskellStubbedPsiElementBase(ASTNode node) {
        super(node);
    }
}
