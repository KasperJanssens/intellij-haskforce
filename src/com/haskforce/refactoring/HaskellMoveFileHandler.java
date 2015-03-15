package com.haskforce.refactoring;

import com.haskforce.psi.HaskellConid;
import com.haskforce.psi.HaskellFile;
import com.haskforce.psi.HaskellModuledecl;
import com.haskforce.psi.HaskellPsiUtil;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.move.moveClassesOrPackages.MoveClassesOrPackagesUtil;
import com.intellij.refactoring.move.moveFilesOrDirectories.MoveFileHandler;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ProcessingContext;
import com.intellij.util.Query;
import org.apache.velocity.util.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HaskellMoveFileHandler extends MoveFileHandler {


    @Override
    public boolean canProcessElement(PsiFile psiFile) {
        return psiFile instanceof HaskellFile;
    }

    @Override
    /**
     * Moved file creates a map with From -> To elements. This map will be used in the retargetUsages function.
     * For every constructor that needs to be renamed we need to add that psielement to From-To wise in the map.
     * The find usages function will then find all usages of these constructor, and the reanamin will be done in
     * the retarget usages function. As far as I understood. seems doable, but what when we do not simply
     * rename a constructor but have to remove it (three deep -> two deep)
     */
    public void prepareMovedFile(PsiFile psiFile, PsiDirectory psiDirectory, Map<PsiElement, PsiElement> map) {
        HaskellModuledecl haskellModuledecl = PsiTreeUtil.getChildOfType(psiFile, HaskellModuledecl.class);
        String presentableText = psiDirectory.getPresentation().getPresentableText();
        String[] subDirs = presentableText.split("/");
        List<HaskellConid> conidList = haskellModuledecl.getQconid().getConidList();
        /**
         * For now
         */
        assert subDirs.length == conidList.size();
//        VirtualFile[] contentSourceRoots = ProjectRootManager.getInstance(psiFile.getProject()).getContentSourceRoots();
        /**
         * For all changed constructors need to create a new psi element that's a conid. Somewhere in HaskellPsiUtil?
         */
        System.out.println("koekoek");

    }

    @Nullable
    @Override
    public List<UsageInfo> findUsages(PsiFile psiFile, PsiDirectory newParent, boolean searchInComments,
                                      boolean searchInOtherFiles) {
        /**
         * KIVVVSS : hierarchy of one directory deep for now. Only move from one directory deep to one directory deep.
         * Feeling my way around here.
         */

        PsiReferenceProvider referenceProvider = ServiceManager.getService(PsiReferenceProvider.class);



        HaskellModuledecl haskellModuledecl = PsiTreeUtil.getChildOfType(psiFile, HaskellModuledecl.class);
        List<HaskellConid> conidList = haskellModuledecl.getQconid().getConidList();
        HaskellConid firstCon = conidList.get(0);
        Query<PsiReference> firstConReferences = ReferencesSearch.search(firstCon);

        return null;
        /*String currentDirectory = psiFile.getContainingDirectory().getName();
        String futureDirectory = newParent.getName();
        List<UsageInfo> result = new ArrayList<UsageInfo>();
        List<HaskellConid> conidList = haskellModuledecl.getQconid().getConidList();
        for (HaskellConid haskellConid : conidList) {
            if (haskellConid.getText().equals(futureDirectory)){
                UsageInfo[] usages = MoveClassesOrPackagesUtil.findUsages(haskellConid, true, true, "koekoek");
                UsageInfo[] usages = MoveClassesOrPackagesUtil.findUsages(haskellConid, true, true, "koekoek");
                Collections.addAll(result, usages);
            }
        }*/
    }

    @Override
    public void retargetUsages(List<UsageInfo> list, Map<PsiElement, PsiElement> oldToNewMap) {
        /**
        This renames the usages.
         **/
        System.out.println("retargetUsages");
    }

    @Override
    public void updateMovedFile(PsiFile psiFile) throws IncorrectOperationException {
        /**
         * This renames the module itself, as the file itself will not be renamed by target usages.
         * How does this connect with the rename file thingy, that isn't triggered or so?
         */
        String[] constructorNames = StringUtils.split(psiFile.getContainingDirectory().getName(), ".");
        HaskellFile haskellFile = (HaskellFile) psiFile;
        HaskellModuledecl haskellModuledecl = PsiTreeUtil.findChildOfType(haskellFile, HaskellModuledecl.class);

        List<HaskellConid> conidList = haskellModuledecl.getQconid().getConidList();
        /**
         * KIVVVSS : hierarchy of one directory deep for now. Only move from one directory deep to one directory deep.
         * Feeling my way around here.
         */
        conidList.get(0).setName(constructorNames[0]);
    }
}
