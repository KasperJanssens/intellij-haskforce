package com.haskforce.refactoring;

import com.google.common.collect.Lists;
import com.haskforce.psi.HaskellConid;
import com.haskforce.psi.HaskellFile;
import com.haskforce.psi.HaskellModuledecl;
import com.haskforce.psi.HaskellPsiUtil;
import com.haskforce.psi.impl.HaskellElementFactory;
import com.haskforce.utils.FileUtil;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.move.moveClassesOrPackages.MoveClassesOrPackagesUtil;
import com.intellij.refactoring.move.moveFilesOrDirectories.MoveFileHandler;
import com.intellij.refactoring.util.MoveRenameUsageInfo;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ProcessingContext;
import com.intellij.util.Query;
import org.apache.velocity.util.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class HaskellMoveFileHandler extends MoveFileHandler {


    private PsiDirectory psiDirectory;

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
        this.psiDirectory = psiDirectory;
        Project project = psiFile.getProject();
        // 2 of 4
        HaskellModuledecl haskellModuledecl = PsiTreeUtil.getChildOfType(psiFile, HaskellModuledecl.class);
        List<String> subDirs = FileUtil.getPathFromSourceRoot(project, psiDirectory.getVirtualFile());
        List<HaskellConid> conidList = haskellModuledecl.getQconid().getConidList();
        /**
         * For all changed constructors need to create a new psi element that's a conid. Somewhere in HaskellPsiUtil?
         */
        if (subDirs.size() == conidList.size()-1) {
            for (int i = 0; i < subDirs.size(); i++) {
                String currentSubDir = subDirs.get(i);
                HaskellConid oldConId = conidList.get(i);
                if (!currentSubDir.equals(oldConId.getName())) {
                    HaskellConid newConId = HaskellElementFactory.createConidFromText(project, currentSubDir);
                    map.put(oldConId, oldConId.replace(newConId));
                }
            }
            HaskellConid moduleName = conidList.get(conidList.size() - 1);
            map.put(moduleName, moduleName);
        }
        if (subDirs.size() < conidList.size()-1){
            int i = 0;
            for (; i < subDirs.size(); i++) {
                String currentSubDir = subDirs.get(i);
                HaskellConid oldConId = conidList.get(i);
                if (!currentSubDir.equals(oldConId.getName())) {
                    HaskellConid newConId = HaskellElementFactory.createConidFromText(project, currentSubDir);
                    map.put(oldConId, oldConId.replace(newConId));
                } else {
                    map.put(oldConId, oldConId);
                }
            }
            List<HaskellConid> constructorsToRemove = conidList.subList(i, conidList.size() - 1);
            for (HaskellConid haskellConid : constructorsToRemove) {
                map.put(haskellConid,null);
                PsiElement dot = haskellConid.getNextSibling();
                dot.delete();
                haskellConid.delete();
            }
            HaskellConid moduleName = conidList.get(conidList.size() - 1);
            map.put(moduleName, moduleName);
        }

        if (subDirs.size() > conidList.size()-1){
            int i = 0;
            for (; i < conidList.size()-1; i++) {
                String currentSubDir = subDirs.get(i);
                HaskellConid oldConId = conidList.get(i);
                if (!currentSubDir.equals(oldConId.getName())) {
                    HaskellConid newConId = HaskellElementFactory.createConidFromText(project, currentSubDir);
                    map.put(oldConId, oldConId.replace(newConId));
                } else {
                    map.put(oldConId, oldConId);
                }
            }

            HaskellConid originalModuleName = conidList.get(conidList.size() - 1);
            PsiElement originalModuleNameParent = originalModuleName.getParent();
            PsiElement dot = originalModuleName.getPrevSibling();
            HaskellConid newModuleName = (HaskellConid)originalModuleName.copy();
            for (; i< subDirs.size();i++){
                HaskellConid newConId = HaskellElementFactory.createConidFromText(project, subDirs.get(i));
                originalModuleNameParent.addBefore(newConId, originalModuleName);
                originalModuleNameParent.addBefore(dot.copy(), originalModuleName);
            }
//            map.put(originalModuleName,originalModuleName.replace(newModuleName));
            map.put(originalModuleName,originalModuleName);
        }
    }

    @Nullable
    @Override
    public List<UsageInfo> findUsages(PsiFile psiFile, PsiDirectory newParent, boolean searchInComments,
                                      boolean searchInOtherFiles) {

        // 1 of 4
        /**
         * KIVVVSS : hierarchy of one directory deep for now. Only move from one directory deep to one directory deep.
         * Feeling my way around here.
         */

        HaskellModuledecl haskellModuledecl = PsiTreeUtil.getChildOfType(psiFile, HaskellModuledecl.class);
        List<HaskellConid> conidList = haskellModuledecl.getQconid().getConidList();
        List<UsageInfo> usageInfos = Lists.newArrayList();
        for (HaskellConid haskellConid : conidList) {
            Collection<PsiReference> psiReferences = ReferencesSearch.search(haskellConid).findAll();
            for (PsiReference psiReference : psiReferences) {
                UsageInfo usageInfo = new MoveRenameUsageInfo(psiReference, haskellConid);
                usageInfos.add(usageInfo);
            }
        }
        return usageInfos;
    }

    @Override
    public void retargetUsages(List<UsageInfo> list, Map<PsiElement, PsiElement> oldToNewMap) {
        // 4 of 4
        /**
        This renames the usages.
         **/
        for (UsageInfo usageInfo : list) {
            if (usageInfo instanceof MoveRenameUsageInfo){
                MoveRenameUsageInfo moveRenameUsageInfo = (MoveRenameUsageInfo) usageInfo;
                PsiElement oldElement = moveRenameUsageInfo.getReferencedElement();
                PsiElement newElement = oldToNewMap.get(oldElement);
                PsiReference reference = moveRenameUsageInfo.getReference();
                if (reference != null){
                    if (newElement != null) {
                        reference.bindToElement(newElement);
                    } else {
                        PsiElement referringElement = reference.getElement();
                        PsiElement dot = referringElement.getNextSibling();
                        dot.delete();
                        referringElement.delete();
                    }
                }
            }
        }
    }

    @Override
    public void updateMovedFile(PsiFile psiFile) throws IncorrectOperationException {
        // 3 of 4
        /**
         This is for 'post move' actions. The file's current directory should already be updated and so. Nothing more to
         do here for the haskell side of things.
         */
    }
}
