package com.haskforce.refactoring;

import com.haskforce.language.HaskellNamesValidator;
import com.haskforce.psi.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.refactoring.rename.RenamePsiFileProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class HaskellRenamePsiFileProcessor extends RenamePsiFileProcessor {

    @Override
    public void prepareRenaming(PsiElement element, String newName, Map<PsiElement, String> allRenames) {
        /**
         * TODO
         * also will have to validate newname first. But to do that we need to get a hold of the name validator.
         */
        if (element instanceof HaskellFile) {

            HaskellFile haskellFile = (HaskellFile) element;
            /**
             * If the current module name is Main you will likely not want to change it.
             */
            if ("Main".equals(haskellFile.getModuleName())){
                return;
            }
            PsiElement[] children = haskellFile.getChildren();
            for (PsiElement child : children) {
                if (child instanceof HaskellModuledecl){
                    HaskellModuledecl haskellModuledecl = (HaskellModuledecl) child;
                    HaskellQconid qconid = haskellModuledecl.getQconid();
                    List<HaskellConid> conidList = qconid.getConidList();
                    if (conidList.size()>0){
                        HaskellConid haskellConid = conidList.get(conidList.size() - 1);
                        String newNameWithoutHs = FileUtil.getNameWithoutExtension(newName);
                        allRenames.put(haskellConid,newNameWithoutHs);
                    }
                }
            }

            allRenames.put(element, newName);
        }
    }

    @Override
    public boolean canProcessElement(PsiElement element) {
        return element instanceof PsiFile;
    }

}
