package com.haskforce.psi.references;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.haskforce.HaskellModuleType;
import com.haskforce.codeInsight.HaskellCompletionContributor;
import com.haskforce.index.HaskellModuleIndex;
import com.haskforce.psi.*;
import com.haskforce.psi.impl.HaskellPsiImplUtil;
import com.haskforce.utils.HaskellUtil;
import com.haskforce.utils.SystemUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.ID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Resolves references to elements.
 */
public class HaskellReference extends PsiReferenceBase<PsiNamedElement> implements PsiPolyVariantReference {
    private String name;

    public HaskellReference(@NotNull PsiNamedElement element, TextRange textRange) {
        super(element, textRange);
        name = element.getName();
    }

    public static final ResolveResult[] EMPTY_RESOLVE_RESULT = new ResolveResult[0];

    /**
     * Resolves references to a set of results.
     */
    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        // We should only be resolving varids or conids.
        if (!(myElement instanceof HaskellVarid || myElement instanceof HaskellConid)) {
            return EMPTY_RESOLVE_RESULT;
        }
        // Make sure that we only complete the last conid in a qualified expression.
        if (myElement instanceof HaskellConid) {
            // Don't resolve a module import to a constructor.
            HaskellQconid qconid = PsiTreeUtil.getParentOfType(myElement, HaskellQconid.class);
            if (qconid == null) { return EMPTY_RESOLVE_RESULT; }
            if (!myElement.equals(Iterables.getLast(qconid.getConidList()))) { return EMPTY_RESOLVE_RESULT; }
            /**
             * Maybe we're trying to find the references to the qualifier name of a qualified import. In that
             * case, the next sibling of the conId has to be a '.'
             *  .
             */
            PsiElement dotSibling = myElement.getNextSibling();
            if (".".equals(dotSibling.getText())){

            }

        }
        Project project = myElement.getProject();

        HaskellImpdecl haskellImpdecl = PsiTreeUtil.getParentOfType(myElement, HaskellImpdecl.class);
        if (haskellImpdecl != null){
            /**
             * Do not go looking for module matches when the element is not the last constructor of an import declaration
             * This can be found by checking whether the nextsibling of myelement is null, and myElement's parent is the
             * first qconId in the impDecl. Pfffw...
             */
            PsiElement parent = myElement.getParent();
            if (parent instanceof  HaskellQconid) {
                HaskellQconid haskellQconid = (HaskellQconid) parent;
                if (myElement.getNextSibling() == null ) {
                    if (haskellImpdecl.getQconidList().get(0).equals(haskellQconid)) {
                        List<PsiElementResolveResult> results = handleImportReferences(haskellImpdecl, myElement);
                        return results.toArray(new ResolveResult[results.size()]);
                    }
                }
            }
        }



        /**
         * TODO
         * Would like to use this StubIndex, but using it here creates problems when performing go to symbol,
         * ends up in a neverending recursive call and a nice stackoverflow error. Don't know why.
         *
         * https://devnet.jetbrains.com/thread/459789
         */
//        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
//        Collection<HaskellNamedElement> namedElements = StubIndex.getElements(HaskellAllNameIndex.KEY, name, project, scope, HaskellNamedElement.class);

        // Guess 20 variants tops most of the time in any real code base.
        final List<PsiNamedElement> namedElements = HaskellUtil.findDefinitionNode(project, name, myElement);
        List<ResolveResult> results = new ArrayList<ResolveResult>(20);
        List<HaskellPsiUtil.Import> imports = HaskellPsiUtil.parseImports(myElement.getContainingFile());

        String qualifiedCallName = HaskellUtil.getQualifiedPrefix(myElement);

        if (qualifiedCallName == null){
            results.addAll(HaskellUtil.matchGlobalNamesUnqualified(myElement,namedElements,imports));

            List<PsiElement> localVariables = HaskellUtil.matchLocalDefinitionsInScope(myElement, name);
            for (PsiElement psiElement : localVariables) {
                results.add(new PsiElementResolveResult(psiElement));
            }

            /**
             * TODO find out if we can or can not check the where clauses in case we found something higher up (a
             * let clause or so). Not yet sure about the correct precedence.
             */
            List<PsiElement> localWhereDefinitions = HaskellUtil.matchWhereClausesInScope(myElement, name);
            for (PsiElement element : localWhereDefinitions) {
                results.add(new PsiElementResolveResult(element));
            }
        } else {
            results.addAll(HaskellUtil.matchGlobalNamesQualified(namedElements, qualifiedCallName, imports));
        }
        return results.toArray(new ResolveResult[results.size()]);
    }

    private @NotNull List<PsiElementResolveResult> handleImportReferences(@NotNull HaskellImpdecl haskellImpdecl,
                                                @NotNull PsiNamedElement myElement) {
        /**
         * Don't use the named element yet to determine which elemen we're
         * talking about, not necessary yet
         */
        /**
         * TODO this might not work in an older JRE
         */
        List<PsiElementResolveResult> modulesFound = Lists.newArrayList();
        List<HaskellQconid> qconidList = haskellImpdecl.getQconidList();
        if (qconidList.size() > 0){
            String moduleName = qconidList.get(0).getText();
            /**
             * TODO serious line separator before PR
             */
            GlobalSearchScope globalSearchScope = GlobalSearchScope.projectScope(myElement.getProject());
            List<HaskellFile> filesByModuleName = HaskellModuleIndex.getFilesByModuleName(myElement.getProject(), moduleName, globalSearchScope);
            for (HaskellFile haskellFile : filesByModuleName) {
                HaskellModuledecl[] moduleDecls = PsiTreeUtil.getChildrenOfType(haskellFile, HaskellModuledecl.class);
                if (moduleDecls.length != 0){
                    List<HaskellConid> conidList = moduleDecls[0].getQconid().getConidList();
                    modulesFound.add(new PsiElementResolveResult(conidList.get(conidList.size() - 1)));
                }
            }
        }
        return modulesFound;
    }

    /**
     * Resolves references to a single result, or fails.
     */
    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    /**
     * Controls what names that get added to the autocompletion popup available
     * on ctrl-space.
     */
    @NotNull
    @Override
    public Object[] getVariants() {
        // If we are not in an expression, don't provide reference completion.
        if (PsiTreeUtil.getParentOfType(myElement, HaskellExp.class) == null) {
            return new Object[]{};
        }
        // If we are in a qualified name, don't provide reference completion.
        final PsiElement qId = PsiTreeUtil.getParentOfType(myElement, HaskellQconid.class, HaskellQvarid.class);
        if (qId != null && qId.textContains('.')) {
            return new Object[]{};
        }
        final PsiFile containingFile = myElement.getContainingFile();
        if (!(containingFile instanceof HaskellFile)) {
            return new Object[]{};
        }
        List<PsiNamedElement> namedNodes = HaskellUtil.findDefinitionNodes((HaskellFile)containingFile);
        List<LookupElement> variants = new ArrayList<LookupElement>(20);
        for (final PsiNamedElement namedElement : namedNodes) {
            final PsiElement genDecl = PsiTreeUtil.getParentOfType(namedElement, HaskellGendecl.class);
            final PsiFile psiFile = namedElement.getContainingFile();
            if (!(psiFile instanceof HaskellFile)) { continue; }
            final String module = ((HaskellFile) psiFile).getModuleOrFileName();
            final String name = namedElement.getName();
            if (name == null) { continue; }
            final String type;
            if (genDecl != null) {
                final PsiElement cType = PsiTreeUtil.getChildOfType(genDecl, HaskellCtype.class);
                type = cType == null ? "" : cType.getText();
            } else {
                type = "";
            }
            variants.add(HaskellCompletionContributor.createLookupElement(name, module, type));
        }
        return variants.toArray();
    }

    @Override
    public TextRange getRangeInElement() {
        return new TextRange(0, this.getElement().getNode().getTextLength());
    }

    /**
     * Called when renaming refactoring tries to rename the Psi tree.
     */
    @Override
    public PsiElement handleElementRename(final String newName)  throws IncorrectOperationException {
        PsiElement element;
        if (myElement instanceof HaskellVarid) {
            element = HaskellPsiImplUtil.setName((HaskellVarid) myElement, newName);
            if (element != null) return element;
            throw new IncorrectOperationException("Cannot rename " + name + " to " + newName);
        } else if (myElement instanceof HaskellConid) {
            element = HaskellPsiImplUtil.setName((HaskellConid) myElement, newName);
            if (element != null) return element;
            throw new IncorrectOperationException("Cannot rename " + name + " to " + newName);
        }
        return super.handleElementRename(newName);
    }
}
