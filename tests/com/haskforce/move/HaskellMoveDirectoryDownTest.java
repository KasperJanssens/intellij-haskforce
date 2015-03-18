package com.haskforce.move;

import com.haskforce.HaskellLightPlatformCodeInsightFixtureTestCase;
import com.intellij.psi.PsiFile;

public class HaskellMoveDirectoryDownTest extends HaskellLightPlatformCodeInsightFixtureTestCase {

    public HaskellMoveDirectoryDownTest() {
        super("move/MoveDirectoryDown", "move/MoveDirectoryDown");
    }
    

    
    public void testMoveDirectoryDown(){
        PsiFile[] files = myFixture.configureByFiles("Over/Here/MoveMe.hs",
                "Over/Here/ReferringMoveMe.hs");

        myFixture.moveFile("Over/Here/MoveMe.hs", "Over/");

        myFixture.checkResultByFile("Over/MoveMe.hs", "Over/MoveMe-after.hs", false);
        myFixture.checkResultByFile("Over/Here/ReferringMoveMe.hs",
                "Over/Here/ReferringMoveMe-after.hs", false);
    }

}
