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

    public void testMove(){
        PsiFile[] files = myFixture.configureByFiles("From/MoveMe.hs", "To/Token.hs");
        myFixture.moveFile("From/MoveMe.hs", "To/");
        HaskellFile moveMe = (HaskellFile)files[0];
        HaskellFile token = (HaskellFile)files[1];
        assertEquals("To", moveMe.getContainingDirectory().getName());
        assertEquals("To.MoveMe", moveMe.getModuleName());

        List<HaskellImpdecl> impdeclList = token.getBody().getImpdeclList();
        assertEquals(impdeclList.size(),1);

        myFixture.checkResultByFile("To/Token.hs", "To/Token-after.hs", false);
    }
}
