package com.haskforce.move;

import com.haskforce.HaskellLightPlatformCodeInsightFixtureTestCase;
import com.intellij.psi.PsiFile;

public class HaskellSimpleMoveMoreReferencesTest extends HaskellLightPlatformCodeInsightFixtureTestCase {

    public HaskellSimpleMoveMoreReferencesTest() {
        super("move/SimpleMoveMoreReferences", "move/SimpleMoveMoreReferences");
    }
    
    public void testSimpleMoveMoreReferences(){
        PsiFile[] files = myFixture.configureByFiles("From/MoveMe.hs",
                "To/Token.hs",
                "From/ReferMoveToo.hs");
        myFixture.moveFile("From/MoveMe.hs", "To/");

        myFixture.checkResultByFile("To/Token.hs", "To/Token-after.hs", false);
        myFixture.checkResultByFile("To/MoveMe.hs", "From/MoveMe-after.hs", false);
        myFixture.checkResultByFile("From/ReferMoveToo.hs", "From/ReferMoveToo-after.hs", false);
    }   

}
