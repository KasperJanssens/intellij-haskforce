package com.haskforce.highlighting;

import com.haskforce.utils.ExecUtil;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Interface + encapsulation of details concerning ghc-mod communication.
 */
public class GhcMod {
    @Nullable
    public static String getPath(@NotNull Project project) {
        final String path = PropertiesComponent.getInstance(project).getValue("ghcModPath");
        return path == null || path.isEmpty() ? null : path;
    }

    @NotNull
    public static Problems check(@NotNull Project project, @NotNull String workingDirectory, @NotNull String file) {
        String ghcModPath = getPath(project);
        String stdout;
        if (ghcModPath == null || (stdout = exec(workingDirectory, ghcModPath, "check", file)) == null) {
            return new Problems();
        }
        return parseProblems(new Scanner(stdout));
    }

    @Nullable
    public static  String exec(@NotNull String workingDirectory, @NotNull String ghcModPath, String... commands) {
        GeneralCommandLine commandLine = new GeneralCommandLine(ghcModPath);
        commandLine.setWorkDirectory(workingDirectory);
        commandLine.addParameters(commands);
        final String stdout = ExecUtil.readCommandLine(commandLine);
        if (stdout == null) {
            return null;
        }
        return stdout;
    }

    @NotNull
    public static Problems parseProblems(Scanner scanner) {
        Problems result = new Problems();
        Problem problem;
        while ((problem = parseProblem(scanner)) != null) {
            result.add(problem);
        }
        return result;
    }

    @Nullable
    public static Problem parseProblem(Scanner scanner) {
        if (scanner == null) {
            return null;
        }
        scanner.useDelimiter(":");
        if (!scanner.hasNext()) {
            return null;
        }
        String file = scanner.next();
        if (!scanner.hasNext()) {
            return null;
        }
        if (!scanner.hasNextInt()) {
            // We're probably parsing something like C:\path\to\file.hs
            file += ':' + scanner.next();
            if (!scanner.hasNextInt()) {
                return null;
            }
        }
        final int startLine = scanner.nextInt();
        if (!scanner.hasNextInt()) {
            return null;
        }
        final int startColumn = scanner.nextInt();
        scanner.skip(":");
        scanner.useDelimiter("\n");
        if (!scanner.hasNext()) {
            return null;
        }
        // Remove "In a stmt..." text and set newlines.
        // TODO: How to format annotation with newlines?
        String message = StringUtil.split(scanner.next(), "\0In a stmt").get(0).replace('\0', '\n');
        // Remove "Use -v to .." suffix from message.
        if (message.endsWith("\nUse -v to see a list of the files searched for.")) {
            message = message.substring(0, message.length() - "\nUse -v to see a list of the files searched for.".length());
        }
        // Remove newlines from filename.
        file = file.trim();
        return new Problem(file, startLine, startColumn, message);
    }

    public static class Problems extends ArrayList<Problem>{}

    public static class Problem {
        public String file;
        public int startLine;
        public int startColumn;
        public String message;
        public boolean isError;

        public Problem(String file, int startLine, int startColumn, String message) {
            this.file = file;
            this.startLine = startLine;
            this.startColumn = startColumn;
            this.message = message;
            this.isError = !message.startsWith("Warning: ");
            if (this.isError) {
                this.message = message;
            } else {
                this.message = message.substring("Warning: ".length());
            }
        }
    }
}
