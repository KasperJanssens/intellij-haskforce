package com.haskforce.codeInsight;

import com.haskforce.HaskellIcons;
import com.haskforce.HaskellLanguage;
import com.haskforce.highlighting.annotation.external.GhcMod;
import com.haskforce.highlighting.annotation.external.GhcModi;
import com.haskforce.psi.*;
import com.haskforce.utils.LogicUtil;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.intellij.util.ProcessingContext;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Fills the list of completions available on ctrl-space.
 */
public class HaskellCompletionContributor extends CompletionContributor {
    public static final Key<String[]> MODULE_CACHE_KEY = new Key("MODULE_CACHE");
    public static final Key<List<LookupElement>> LANGUAGE_CACHE_KEY = new Key("LANGUAGE_CACHE");
    public static final Key<String[]> FLAG_CACHE_KEY = new Key("FLAG_CACHE");
    public static final Key<Map<String, List<LookupElement>>> BROWSE_CACHE_KEY = new Key("BROWSE_CACHE");

    private static String[] PRAGMA_TYPES = new String[]{
            "LANGUAGE ", "OPTIONS_GHC ", "WARNING ", "DEPRECATED ", "INLINE ", "NOINLINE ", "INLINABLE ", "CONLIKE ",
            "RULES ", "ANN ", "LINE ", "SPECIALIZE ", "UNPACK ", "SOURCE "};

    public static String[] getPragmaTypes() {
        return PRAGMA_TYPES.clone();
    }

    public HaskellCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().withLanguage(HaskellLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet result) {
                        PsiElement position = parameters.getPosition();
                        PsiFile file = parameters.getOriginalFile();
                        List<ModuleAlias> moduleAliases = parseModuleAliases(file);
                        UserDataHolder cacheHolder = getCacheHolder(file);
                        // Completion methods should return either void or boolean.  If boolean, then it should indicate
                        // whether or not we were in the appropriate context.  This is useful to determine if following
                        // completions should be added.
                        completeKeywordImport(position, result);
                        completeKeywordQualified(position, result);
                        if (completePragma(position, cacheHolder, result)) return;
                        if (completeModuleImport(position, cacheHolder, result)) return;
                        if (completeQualifiedNames(position, moduleAliases, cacheHolder, result)) return;
                        if (completeNameImport(position, cacheHolder, result)) return;
                        completeLocalNames(position, moduleAliases, cacheHolder, result);
                    }
                }
        );
    }

    public static void completeKeywordImport(@NotNull final PsiElement position, @NotNull final CompletionResultSet result) {
        PsiElement el = position;
        while (el != null) {
            PsiElement parent = el.getParent();
            if (parent instanceof HaskellImpdecl) {
                // We shouldn't completion "import" if we're already in an import.
                return;
            }
            if (parent instanceof HaskellBody) {
                break;
            }
            el = parent;
        }
        PsiElement prev = getPrevSiblingWhere(new Function<PsiElement, Boolean>() {
            @Override
            public Boolean fun(PsiElement psiElement) {
                return !PsiTreeUtil.instanceOf(psiElement, HaskellPpragma.class, PsiWhiteSpace.class, PsiComment.class);
            }
        }, el);
        // Check if previous sibling is an import or does not exist (first import).
        if (prev == null || prev instanceof HaskellImpdecl) {
            result.addElement(stringToLookupElement.fun("import "));
        }
    }

    public static void completeKeywordQualified(@NotNull final PsiElement position, @NotNull final CompletionResultSet result) {
        final PsiElement prevLeaf = PsiTreeUtil.prevVisibleLeaf(position);
        if (prevLeaf != null && prevLeaf.getText().equals("import")) {
            result.addElement(stringToLookupElement.fun("qualified "));
        }
    }

    public static boolean completePragma(@NotNull final PsiElement position,
                                         @NotNull final UserDataHolder cacheHolder,
                                         @NotNull final CompletionResultSet result) {
        final PsiElement prevSibling = getPrevSiblingWhere(new Function<PsiElement, Boolean>() {
            @Override
            public Boolean fun(PsiElement psiElement) {
                return !(psiElement instanceof PsiWhiteSpace);
            }
        }, position);

        // Pragma types.
        if (prevSibling != null && "{-#".equals(prevSibling.getText())) {
            addAllElements(result, LogicUtil.map(stringToLookupElement, PRAGMA_TYPES));
        }

        final PsiElement openPragma = getPrevSiblingWhere(new Function<PsiElement, Boolean>() {
            @Override
            public Boolean fun(PsiElement psiElement) {
                return psiElement.getText().equals("{-#");
            }
        }, position);

        final PsiElement pragmaTypeElement = getNextSiblingWhere(new Function<PsiElement, Boolean>() {
            @Override
            public Boolean fun(PsiElement psiElement) {
                return !(psiElement instanceof PsiWhiteSpace);
            }
        }, openPragma);

        if (pragmaTypeElement == null) {
            return false;
        }

        final String pragmaType = pragmaTypeElement.getText();

        if ("LANGUAGE".equals(pragmaType)) {
            addAllElements(result, cacheHolder.getUserData(LANGUAGE_CACHE_KEY));
        } else if ("OPTIONS_GHC".equals(pragmaType)) {
            // TODO: Workaround since completion autocompletes after the "-", so without this
            // we may end up completing -foo with --foo (inserting a "-").
            final String[] flags = cacheHolder.getUserData(FLAG_CACHE_KEY);
            if (position.getText().startsWith("-")) {
                addAllElements(result, LogicUtil.map(new Function<String, LookupElement>() {
                    @Override
                    public LookupElement fun(String s) {
                        return stringToLookupElement.fun(s.startsWith("-") ? s.substring(1) : s);
                    }
                }, flags));
            } else {
                addAllElements(result, LogicUtil.map(stringToLookupElement, flags));
            }
        }
        return true;
    }

    public static boolean completeModuleImport(@NotNull final PsiElement position,
                                               @NotNull final UserDataHolder cacheHolder,
                                               @NotNull final CompletionResultSet result) {
        // TODO: Refactor this implementation.
        PsiElement el = position.getParent();
        if (!(el instanceof HaskellConid)) {
            return false;
        }
        el = el.getParent();
        if (!(el instanceof HaskellQconid)) {
            return false;
        }
        el = el.getParent();
        if (!(el instanceof HaskellImpdecl)) {
            return false;
        }
        // Regardless of whether we actually have cache data to work with, we still want to return true
        // after this point since we've already identified that we are in the appropriate context.
        final String[] list = cacheHolder.getUserData(MODULE_CACHE_KEY);
        if (list != null) {
            StringBuilder builder = new StringBuilder(0);
            el = position.getParent();
            while (el != null) {
                el = el.getPrevSibling();
                if (el != null) {
                    builder.insert(0, el.getText());
                }
            }
            final String partialModule = builder.toString();
            Set<String> newLines = new HashSet<String>(0);
            for (String line : list) {
                if (line.startsWith(partialModule)) {
                    String newLine = line.replace(partialModule, "");
                    final int firstDotPos = newLine.indexOf('.');
                    if (firstDotPos != -1) {
                        newLine = newLine.substring(0, firstDotPos);
                    }
                    newLines.add(newLine);
                }
            }
            addAllElements(result, LogicUtil.map(stringToLookupElement, newLines));
        }
        return true;
    }

    public static boolean completeNameImport(@NotNull final PsiElement position,
                                             @NotNull final UserDataHolder cacheHolder,
                                             @NotNull final CompletionResultSet result) {
        PsiElement el = position.getParent();
        if (el == null) {
            return false;
        }
        el = el.getParent();
        if (!(el instanceof HaskellImportt)) {
            if (el != null) {
                el = el.getParent();
                if (!(el instanceof HaskellImportt)) {
                    return false;
                }
            }
        }
        el = getPrevSiblingWhere(new Function<PsiElement, Boolean>() {
            @Override
            public Boolean fun(PsiElement psiElement) {
                return psiElement instanceof HaskellQconid;
            }
        }, el);
        if (el == null) {
            return false;
        }
        final String module = el.getText();
        final Map<String, List<LookupElement>> cachedNames = cacheHolder.getUserData(BROWSE_CACHE_KEY);
        if (cachedNames != null) {
            addAllElements(result, cachedNames.get(module));
        }
        return true;
    }

    public static boolean completeQualifiedNames(@NotNull final PsiElement position,
                                                 @NotNull final List<ModuleAlias> moduleAliases,
                                                 @NotNull final UserDataHolder cacheHolder,
                                                 @NotNull final CompletionResultSet result) {
        PsiElement el = position.getParent();
        if (el == null) {
            return false;
        }
        el = el.getParent();
        if (!(el instanceof HaskellQconid || el instanceof HaskellQvarid)) {
            return false;
        }
        final String qName = el.getText();
        final int lastDotPos = qName.lastIndexOf('.');
        if (lastDotPos == -1) {
            return false;
        }
        final String alias = qName.substring(0, lastDotPos);
        // Pull user-qualified names from cache.
        final Map<String, List<LookupElement>> browseCache = cacheHolder.getUserData(BROWSE_CACHE_KEY);
        if (browseCache != null) {
            final ModuleAlias moduleAlias = LogicUtil.first(new Function<ModuleAlias, Boolean>() {
                @Override
                public Boolean fun(ModuleAlias x) {
                    return x != null && alias.equals(x.alias);
                }
            }, moduleAliases);
            if (moduleAlias != null) {
                addAllElements(result, browseCache.get(moduleAlias.module));
            }
        }
        return true;
    }

    public static boolean completeLocalNames(@NotNull final PsiElement position,
                                             @NotNull final List<ModuleAlias> moduleAliases,
                                             @NotNull final UserDataHolder holder,
                                             @NotNull final CompletionResultSet result) {
        if (PsiTreeUtil.getParentOfType(position, HaskellExp.class) == null) {
            return false;
        }
        final Map<String, List<LookupElement>> cachedNames = holder.getUserData(BROWSE_CACHE_KEY);
        if (cachedNames == null) {
            return false;
        }
        for (ModuleAlias x : moduleAliases) {
            // If alias is null, then it was not imported qualified, so it should be globally available.
            if (x.alias == null) {
                addAllElements(result, cachedNames.get(x.module));
            }
        }
        return true;
    }

    @Nullable
    public static Map<String, List<LookupElement>> getBrowseCache(@NotNull final UserDataHolder holder,
                                                                  @NotNull final PsiFile file,
                                                                  @NotNull final Project project,
                                                                  @NotNull final String workDir,
                                                                  final boolean force) {
        GhcModi ghcModi = GhcModi.getInstance(project, workDir);
        if (ghcModi == null) {
            return null;
        }
        List<ModuleAlias> moduleAliases = parseModuleAliases(file);
        Map<String, List<LookupElement>> browseCache = force ? null : holder.getUserData(BROWSE_CACHE_KEY);
        if (browseCache == null) {
            browseCache = new HashMap<String, List<LookupElement>>(moduleAliases.size());
        }
        for (ModuleAlias x : moduleAliases) {
            final List<LookupElement> cachedNames = browseCache.get(x.module);
            if (cachedNames != null && !browseCache.containsKey(x.module)) {
                browseCache.put(x.module, cachedNames);
                continue;
            }
            final List<LookupElement> names = LogicUtil.map(browseItemToLookupElement, ghcModi.browse(x.module));
            browseCache.put(x.module, names);
        }
        return browseCache;
    }

    /**
     * Returns a map of module -> alias for each imported module.  If a module is imported but not qualified, alias
     * will be null.
     */
    @NotNull
    public static List<ModuleAlias> parseModuleAliases(@NotNull final PsiFile file) {
        PsiElement el = null;
        for (PsiElement child : file.getChildren()) {
            if (child instanceof HaskellBody) {
                el = child;
                break;
            }
        }
        if (el == null) {
            return new ArrayList(0);
        }
        List<ModuleAlias> result = new ArrayList<ModuleAlias>(10);
        // For simplicity, let's just assume Prelude is always there.
        result.add(new ModuleAlias("Prelude", null));
        for (PsiElement child : el.getChildren()) {
            if (child instanceof HaskellImpdecl) {
                String module = null;
                String alias = null;
                boolean isQualified = child.getText().startsWith("import qualified");
                for (PsiElement gChild : child.getChildren()) {
                    if (gChild instanceof HaskellQconid) {
                        if (module == null) {
                            module = gChild.getText();
                        } else {
                            alias = gChild.getText();
                        }
                    }
                }
                if (module != null) {
                    // If the user didn't alias the module and it was imported qualified, the full module name is used.
                    if (alias == null && isQualified) {
                        alias = module;
                    }
                    //noinspection ObjectAllocationInLoop
                    result.add(new ModuleAlias(module, alias));
                }
            }
        }
        return result;
    }

    public static class ModuleAlias {
        public @NotNull final String module;
        public @Nullable final String alias;

        public ModuleAlias(@NotNull String module, @Nullable String alias) {
            this.module = module;
            this.alias = alias;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ModuleAlias
                    && Comparing.equal(module, ((ModuleAlias) o).module)
                    && Comparing.equal(alias, ((ModuleAlias) o).alias);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }

    /**
     * Helper method to load data from ghc-mod into a file cache to be used for autocompletion.  This is done in a
     * Java thread so execution can continue.  It just so happens to be very convenient to do this in the external
     * annotator; however, there may be a better place to do this.
     */
    public static void loadCacheData(@NotNull final PsiFile file, @NotNull final Project project, @NotNull final String workDir) {
        loadCacheData(file, project, workDir, false);
    }

    public static void loadCacheData(@NotNull final PsiFile file, @NotNull final Project project, @NotNull final String workDir, final boolean force) {
        final UserDataHolder cache = getCacheHolder(file);
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                if (force || cache.getUserData(LANGUAGE_CACHE_KEY) == null) {
                    cache.putUserData(LANGUAGE_CACHE_KEY, LogicUtil.map(stringToLookupElement, GhcMod.lang(project, workDir)));
                }
                if (force || cache.getUserData(FLAG_CACHE_KEY) == null) {
                    cache.putUserData(FLAG_CACHE_KEY, GhcMod.flag(project, workDir));
                }
                if (force || cache.getUserData(MODULE_CACHE_KEY) == null) {
                    cache.putUserData(MODULE_CACHE_KEY, GhcMod.list(project, workDir));
                }
                // Checks for force and existing cache are done in getBrowseCache()
                cache.putUserData(BROWSE_CACHE_KEY, getBrowseCache(cache, file, project, workDir, force));
            }
        });
    }

    public static UserDataHolder getCacheHolder(@NotNull PsiFile file) {
        final Module module = ModuleUtilCore.findModuleForPsiElement(file);
        return module == null ? file : module;
    }

    /**
     * Helper to prevent having to do a null check before adding elements to the completion result.
     */
    public static void addAllElements(CompletionResultSet result, List<LookupElement> elements) {
        if (elements != null) {
            result.addAllElements(elements);
        }
    }

    @Nullable
    public static PsiElement getFirstElementWhere(Function<PsiElement, PsiElement> modify,
                                                  Function<PsiElement, Boolean> where,
                                                  PsiElement initialElement) {
        if (initialElement == null) {
            return null;
        }
        PsiElement result = modify.fun(initialElement);
        while (result != null) {
            if (where.fun(result)) {
                return result;
            }
            result = modify.fun(result);
        }
        return null;
    }

    @Nullable
    public static PsiElement getPrevSiblingWhere(Function<PsiElement, Boolean> f, PsiElement e) {
        return getFirstElementWhere(new Function<PsiElement, PsiElement>() {
            @Override
            public PsiElement fun(PsiElement psiElement) {
                return psiElement.getPrevSibling();
            }
        }, f, e);
    }

    @Nullable
    public static PsiElement getNextSiblingWhere(Function<PsiElement, Boolean> f, PsiElement e) {
        return getFirstElementWhere(new Function<PsiElement, PsiElement>() {
            @Override
            public PsiElement fun(PsiElement psiElement) {
                return psiElement.getNextSibling();
            }
        }, f, e);
    }

    /**
     * Adjust the error message when no lookup is found.
     */
    @Nullable
    @Override
    public String handleEmptyLookup(@NotNull CompletionParameters parameters, final Editor editor) {
        return "HaskForce: no completion found.";
    }

    public static LookupElement createLookupElement(@NotNull String name, @NotNull String module, @NotNull String type) {
        return LookupElementBuilder.create(name).withIcon(HaskellIcons.FILE)
                .withTailText(" (" + module + ')', true)
                .withTypeText(type);
    }

    public static final Function<String, LookupElement> stringToLookupElement = new Function<String, LookupElement>() {
        @Override
        public LookupElement fun(String s) {
            return LookupElementBuilder.create(s).withIcon(HaskellIcons.FILE);
        }
    };

    public static final Function<GhcModi.BrowseItem, LookupElement> browseItemToLookupElement = new Function<GhcModi.BrowseItem, LookupElement>() {
        @Override
        public LookupElement fun(GhcModi.BrowseItem x) {
            return createLookupElement(x.name, x.module, x.type);
        }
    };
}
