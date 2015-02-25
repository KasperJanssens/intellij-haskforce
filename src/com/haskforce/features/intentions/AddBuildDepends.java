package com.haskforce.features.intentions;


import com.haskforce.cabal.index.CabalFileIndex;
import com.haskforce.cabal.psi.*;
import com.haskforce.cabal.psi.impl.CabalElementFactory;
import com.haskforce.utils.FileUtil;
import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AddBuildDepends extends BaseIntentionAction {

    public final String packageName;

    public AddBuildDepends(String packageName) {
        this.packageName = packageName;
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Add build depends";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile psiFile) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {
        CabalFile cabalFile = CabalFileIndex.getCabalFileByProjectName(project, GlobalSearchScope.allScope(project));
        if (cabalFile == null) return;
        CabalLibrary library = PsiTreeUtil.findChildOfType(cabalFile,CabalLibrary.class);
        List<CabalLibraryKeys> libraryKeysList = null;
        if (library == null) {
            return;
        }
        libraryKeysList = library.getLibraryKeysList();
        for (CabalLibraryKeys cabalLibraryKeys : libraryKeysList) {
            CabalBuildInformation buildInformation = cabalLibraryKeys.getBuildInformation();
            List<CabalDependency> dependencyList = null;
            if (buildInformation == null) {
                CabalBuildInformation cabalBuildInformation = CabalElementFactory.createCabalBuildInformation(project, packageName);
                library.addAfter(cabalBuildInformation,libraryKeysList.get(0));
            } else {
                dependencyList = buildInformation.getBuildDepends().getDependencyList();
                //should always be good, cabal file will not compile without having at least
                //one dependency. No compile of cabal file, no quickfix concerning adding something to the cabal file
                //seems easiest and not a big deal to have the cabal file compiler
                CabalDependency firstDependency = dependencyList.get(0);
                CabalDependency newDependency = CabalElementFactory.createCabalDependency(project, packageName);
                firstDependency.addAfter(newDependency, firstDependency);
            }
        }
    }
}
