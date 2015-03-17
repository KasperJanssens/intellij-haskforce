package com.haskforce.move;

import com.haskforce.HaskellLightPlatformCodeInsightFixtureTestCase;
import com.intellij.psi.PsiFile;

public class HaskellMoveDirectoryUpTest extends HaskellLightPlatformCodeInsightFixtureTestCase {

    public HaskellMoveDirectoryUpTest() {
        super("move/MoveDirectoryUp", "move/MoveDirectoryUp");
    }

    public void testMoveDirectoryUp(){
        PsiFile[] files = myFixture.configureByFiles("Over/MoveMe.hs",
                "Over/Here/ReferMoveMe.hs");

        myFixture.moveFile("Over/MoveMe.hs", "Over/Here/");

        myFixture.checkResultByFile("Over/Here/MoveMe.hs",
                "Over/Here/MoveMe-after.hs", false);
        myFixture.checkResultByFile("Over/Here/ReferMoveMe.hs",
                "Over/Here/ReferMoveMe-after.hs", false);
    }
}
