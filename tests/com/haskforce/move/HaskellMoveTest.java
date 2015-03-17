package com.haskforce.move;

import com.haskforce.HaskellLightPlatformCodeInsightFixtureTestCase;
import com.haskforce.psi.HaskellConid;
import com.haskforce.psi.HaskellFile;
import com.haskforce.psi.HaskellImpdecl;
import com.intellij.psi.PsiFile;

import java.util.List;

public class HaskellMoveTest extends HaskellLightPlatformCodeInsightFixtureTestCase {

    public HaskellMoveTest() {
        super("move", "move");
    }

    public void testSimpleMove(){
        PsiFile[] files = myFixture.configureByFiles("SimpleMove/From/MoveMe.hs", "SimpleMove/To/Token.hs");
        myFixture.moveFile("SimpleMove/From/MoveMe.hs", "SimpleMove/To/");

        myFixture.checkResultByFile("SimpleMove/To/Token.hs", "SimpleMove/To/Token-after.hs", false);
        myFixture.checkResultByFile("SimpleMove/To/MoveMe.hs", "SimpleMove/From/MoveMe-after.hs", false);
    }
}
