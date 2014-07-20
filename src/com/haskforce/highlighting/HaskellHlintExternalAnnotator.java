/*
 * Copyright 2012-2013 Sergey Ignatov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Adapted from https://github.com/ignatov/intellij-erlang
 * src/org/intellij/erlang/dialyzer/ErlangDialyzerExternalAnnotator.java
 */

package com.haskforce.highlighting;

import com.google.gson.GsonBuilder;
import com.haskforce.HaskellFileType;
import com.haskforce.utils.ExecUtil;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An external annotator providing warning highlights from hlint.
 *
 * You can set the path to hlint via the Haskell Tools section of the project settings.  However, if it is
 * not set then the annotator will attempt to find hlint automatically and use it.  If it cannot find hlint it will
 * not attempt to provide annotations.
 *
 * The annotator runs once all other background processes have completed, such as the lexer, parser, highlighter, etc.
 * If the file does not parse, these annotations will not be available.
 */
public class HaskellHlintExternalAnnotator extends ExternalAnnotator<HaskellHlintExternalAnnotator.State, HaskellHlintExternalAnnotator.State> {
    private static final Logger LOG = Logger.getInstance(HaskellHlintExternalAnnotator.class);
    private static final Pattern WHITESPACE_REGEX = Pattern.compile("\\s+");
    private static final Pattern HLINT_VERSION_REGEX = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");
    private static final VersionTriple HLINT_MIN_VERSION_WITH_JSON_SUPPORT = new VersionTriple(1, 9, 1);
    public boolean useJson = false;

    /**
     * Parse problems from the hlint --json output.
     */
    private static Problem[] parseProblemsJson(String input) {
        return new GsonBuilder().create().fromJson(input, Problem[].class);
    }

    /**
     * Parse a single problem from the old hlint output if json is not supported.
     */
    private static Problem parseProblemFallback(String input) {
        List<String> split = StringUtil.split(input, ":");
        if (split.size() < 5) {
            return null;
        }
        int line = StringUtil.parseInt(split.get(1), 0);
        if (line == 0) {
            return null;
        }
        int column = StringUtil.parseInt(split.get(2), 0);
        if (column == 0) {
            return null;
        }
        String warning = StringUtil.split(split.get(4), "\n").get(0);
        split = StringUtil.split(input, "\n");
        split = ContainerUtil.subList(split, 2);
        split = StringUtil.split(StringUtil.join(split, "\n"), "Why not:");
        if (split.size() != 2) {
            return null;
        }
        final String found = split.get(0).trim();
        final String whyNot = split.get(1).trim();
        return new Problem("", "", found, warning, "", new String[]{}, "", new int[]{line, column, 0, 0}, whyNot);
    }

    @Nullable
    @Override
    public State collectInformation(@NotNull PsiFile file) {
        VirtualFile vFile = file.getVirtualFile();
        if (vFile == null || vFile.getFileType() != HaskellFileType.INSTANCE) {
            return null;
        }
        String canonicalPath = vFile.getCanonicalPath();
        if (canonicalPath == null) {
            return null;
        }
        Module module = ModuleUtilCore.findModuleForPsiElement(file);
        if (module == null) {
            return null;
        }
        Sdk sdk = ModuleRootManager.getInstance(module).getSdk();
        if (sdk == null) {
            return null;
        }
        String homePath = sdk.getHomePath();
        if (homePath == null) {
            return null;
        }

        final String workingDir = file.getProject().getBasePath();
        final String hlintPath = PropertiesComponent.getInstance(module.getProject()).getValue("hlintPath");
        return hlintPath == null || hlintPath.isEmpty() ? null : new State(hlintPath, getVersion(hlintPath), file.getText(), workingDir);
    }

    @Nullable
    @Override
    public State doAnnotate(State state) {
        if (state == null) {
            return null;
        }
        useJson = HLINT_MIN_VERSION_WITH_JSON_SUPPORT.lte(state.myHlintVersion);
        // Use "-" to read file from stdin.
        GeneralCommandLine commandLine = new GeneralCommandLine(state.myHlintPath, "-");
        commandLine.setWorkDirectory(state.myWorkingDir);
        if (useJson) {
            commandLine.addParameter("--json");
        }
        final String stdout = ExecUtil.readCommandLine(commandLine, state.myFileText);
        if (stdout == null || stdout.isEmpty()) {
            return null;
        }
        if (useJson) {
            ContainerUtil.addAll(state.problems, parseProblemsJson(stdout));
        } else {
            final List<String> lints = StringUtil.split(stdout, "\n\n");
            for (String lint : lints) {
                ContainerUtil.addIfNotNull(state.problems, parseProblemFallback(lint));
            }
        }
        return state;
    }

    @Override
    public void apply(@NotNull PsiFile file, State annotationResult, @NotNull AnnotationHolder holder) {
        if (annotationResult == null || !file.isValid()) {
            return;
        }
        String text = file.getText();
        for (Problem problem : annotationResult.problems) {
            final int offsetStart = StringUtil.lineColToOffset(text, problem.getStartLine() - 1, problem.getStartCol() - 1);
            if (offsetStart == -1) {
                continue;
            }
            final int offsetEnd = useJson ? StringUtil.lineColToOffset(text, problem.getEndLine() - 1, problem.getEndCol() - 1)
                                          : getOffsetEndFallback(offsetStart, problem, text);
            if (offsetEnd == -1) {
                continue;
            }
            TextRange problemRange = TextRange.create(offsetStart, offsetEnd);
            String message = problem.hint + (problem.to == null ? "" : ", why not: " + problem.to);
            holder.createWarningAnnotation(problemRange, message);
            // TODO: Add an inspection to fix/ignore.
        }
    }

    @Nullable
    private static VersionTriple getVersion(String hlintPath) {
        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setExePath(hlintPath);
        commandLine.addParameter("--version");
        final String version = ExecUtil.readCommandLine(commandLine);
        if (version == null) {
            return null;
        }
        Matcher m = HLINT_VERSION_REGEX.matcher(version);
        if (!m.find()) {
            return null;
        }
        return new VersionTriple(
                Integer.parseInt(m.group(1)),
                Integer.parseInt(m.group(2)),
                Integer.parseInt(m.group(3)));
    }

    /**
     * Fallback to a crude guess if the json output is not available from hlint.
     */
    private static int getOffsetEndFallback(int offsetStart, Problem problem, String text) {
        int width = 0;
        int nonWhiteSpaceToFind = WHITESPACE_REGEX.matcher(problem.from).replaceAll("").length();
        int nonWhiteSpaceFound = 0;
        while (offsetStart + width < text.length()) {
            final char c = text.charAt(offsetStart + width);
            if (StringUtil.isLineBreak(c)) {
                break;
            }
            if (!StringUtil.isWhiteSpace(c)) {
                ++nonWhiteSpaceFound;
            }
            ++width;
            if (nonWhiteSpaceFound >= nonWhiteSpaceToFind) {
                break;
            }
        }
        return offsetStart + width;
    }

    public static class State {
        public final List<Problem> problems = new ArrayList<Problem>(0);
        private final String myHlintPath;
        private final VersionTriple myHlintVersion;
        private final String myFileText;
        private final String myWorkingDir;

        public State(String hlintPath, VersionTriple hlintVersion, String fileText, String workingDir) {
            myHlintPath = hlintPath;
            myHlintVersion = hlintVersion;
            myFileText = fileText;
            myWorkingDir = workingDir;
        }
    }

    public static class Problem {
        public String decl;
        public String file;
        public String from;
        public String hint;
        public String module;
        public String[] note;
        public String severity;
        public int[] span;
        public String to;

        public Problem(String decl, String file, String from, String hint, String module, String[] note, String severity, int[] span, String to) {
            this.decl = decl;
            this.file = file;
            this.from = from;
            this.hint = hint;
            this.module = module;
            this.note = note;
            this.severity = severity;
            this.span = span;
            this.to = to;
        }

        public int getStartLine() {
            return span[0];
        }

        public int getStartCol() {
            return span[1];
        }

        public int getEndLine() {
            return span[2];
        }

        public int getEndCol() {
            return span[3];
        }
    }

    // TODO: VersionTriple may be useful in a util module or there may be an better existing implementation.
    public static class VersionTriple {
        private final int x;
        private final int y;
        private final int z;

        VersionTriple(final int x_, final int y_, final int z_) {
            x = x_;
            y = y_;
            z = z_;
        }

        public boolean eq(VersionTriple v) {
            return v != null && x == v.x && y == v.y && z == v.z;
        }

        public boolean gt(VersionTriple v) {
            return v != null && (x > v.x || x == v.x && (y > v.y || y == v.y && z > v.z));
        }

        public boolean lt(VersionTriple v) {
            return v != null && (x < v.x || x == v.x && (y < v.y || y == v.y && z < v.z));
        }

        public boolean gte(VersionTriple v) {
            return eq(v) || gt(v);
        }

        public boolean lte(VersionTriple v) {
            return eq(v) || lt(v);
        }
    }
}
