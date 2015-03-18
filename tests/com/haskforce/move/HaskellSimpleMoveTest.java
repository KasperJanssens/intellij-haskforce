package com.haskforce.move;

import com.haskforce.HaskellLightPlatformCodeInsightFixtureTestCase;
import com.haskforce.psi.HaskellConid;
import com.haskforce.psi.HaskellFile;
import com.haskforce.psi.HaskellImpdecl;
import com.intellij.psi.PsiFile;

import java.util.List;

public class HaskellSimpleMoveTest extends HaskellLightPlatformCodeInsightFixtureTestCase {

    public HaskellSimpleMoveTest() {
        super("move/SimpleMove", "move/SimpleMove");
    }

    public void testSimpleMove(){
        PsiFile[] files = myFixture.configureByFiles("From/MoveMe.hs", "To/Token.hs", "SimpleMove.cabal");
        myFixture.moveFile("From/MoveMe.hs", "To/");

        myFixture.checkResultByFile("To/Token.hs", "To/Token-after.hs", false);
        myFixture.checkResultByFile("To/MoveMe.hs", "From/MoveMe-after.hs", false);
        myFixture.checkResultByFile("SimpleMove.cabal", "SimpleMove-after.cabal", false);
    }
    

}
