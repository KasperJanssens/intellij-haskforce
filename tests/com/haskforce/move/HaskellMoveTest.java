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
    
    public void testSimpleMoveMoreReferences(){
        PsiFile[] files = myFixture.configureByFiles("SimpleMoveMoreReferences/From/MoveMe.hs",
                "SimpleMoveMoreReferences/To/Token.hs",
                "SimpleMoveMoreReferences/From/ReferMoveToo.hs");
        myFixture.moveFile("SimpleMoveMoreReferences/From/MoveMe.hs", "SimpleMoveMoreReferences/To/");

        myFixture.checkResultByFile("SimpleMoveMoreReferences/To/Token.hs", "SimpleMoveMoreReferences/To/Token-after.hs", false);
        myFixture.checkResultByFile("SimpleMoveMoreReferences/To/MoveMe.hs", "SimpleMoveMoreReferences/From/MoveMe-after.hs", false);
        myFixture.checkResultByFile("SimpleMoveMoreReferences/From/ReferMoveToo.hs", "SimpleMoveMoreReferences/From/ReferMoveToo-after.hs", false);
    }   
    
    public void testMoveDirectoryUp(){
        PsiFile[] files = myFixture.configureByFiles("MoveDirectoryUp/Over/Here/MoveMe.hs",
                "MoveDirectoryUp/Over/Here/ReferringMoveMe.hs");

        myFixture.moveFile("MoveDirectoryUp/Over/Here/MoveMe.hs", "MoveDirectoryUp/Over/");

        myFixture.checkResultByFile("MoveDirectoryUp/Over/MoveMe.hs", "MoveDirectoryUp/Over/MoveMe-after.hs", false);
        myFixture.checkResultByFile("MoveDirectoryUp/Over/Here/ReferringMoveMe.hs",
                "MoveDirectoryUp/Over/Here/ReferringMoveMe-after.hs", false);
    }
}
