package com.haskforce.settings;

import com.haskforce.HaskellSdkType;
import com.haskforce.jps.model.HaskellBuildOptions;
import com.haskforce.utils.ExecUtil;
import com.haskforce.utils.GuiUtil;
import com.intellij.compiler.options.CompilerConfigurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.TextFieldWithHistory;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;

/**
 * The "Haskell Compiler" section in Preferences->Compiler.
 */
public class HaskellCompilerConfigurable extends CompilerConfigurable {
    public static final String HASKELL_COMPILER_ID = "Haskell compiler";

    // Swing components.
    private JPanel mainPanel;
    // GHC Binary components.
    private TextFieldWithBrowseButton ghcPath;
    private JLabel ghcVersion;
    // Cabal binary components.
    private TextFieldWithBrowseButton cabalPath;
    private JLabel cabalVersion;
    // Cabal configure flags
    private TextFieldWithHistory cabalFlags;
    // Build configuration components.
    private JCheckBox profilingBuild;
    private JCheckBox cabalBuild;
    private JCheckBox cabalSandbox;
    private JCheckBox installCabalDependencies;
    private JCheckBox enableTests;

    // Data container for settings.
    private final HaskellBuildSettings mySettings;
    // Improved settings if default values.
    private String oldGhcPath;
    private String oldCabalPath;

    @SuppressWarnings("FieldCanBeLocal")
    private final Project myProject;

    public HaskellCompilerConfigurable(@NotNull Project inProject) {
        super(inProject);
        myProject = inProject;
        mySettings = HaskellBuildSettings.getInstance(myProject);

        oldGhcPath = getGhcPath(myProject, mySettings);
        ghcPath.setText(oldGhcPath);
        GuiUtil.addFolderListener(ghcPath, "ghc");

        oldCabalPath = getCabalPath(mySettings);
        cabalPath.setText(oldCabalPath);
        GuiUtil.addFolderListener(cabalPath, "cabal");

        cabalBuild.setSelected(mySettings.isCabalEnabled());
        cabalSandbox.setSelected(mySettings.isCabalSandboxEnabled());
        installCabalDependencies.setSelected(mySettings.isInstallCabalDependenciesEnabled());
        enableTests.setSelected(mySettings.isEnableTestsEnabled());
        updateVersionInfoFields();
    }

    private static String getGhcPath(@NotNull Project project, @NotNull HaskellBuildSettings settings) {
        String ghcPath = settings.getGhcPath();
        if (ghcPath.equals(HaskellBuildOptions.DEFAULT_GHC_PATH)) {
            File sdkGhcPath = HaskellSdkType.getExecutable(project);
            if (sdkGhcPath != null) return sdkGhcPath.getAbsolutePath();
        }
        return ghcPath;
    }

    private static String getCabalPath(@NotNull HaskellBuildSettings settings) {
        String cabalPath = settings.getCabalPath();
        if (cabalPath.equals(HaskellBuildOptions.DEFAULT_CABAL_PATH)) {
            String foundCabalPath = ExecUtil.locateExecutable(HaskellBuildOptions.DEFAULT_CABAL_PATH);
            if (foundCabalPath != null && !foundCabalPath.isEmpty()) return foundCabalPath;
        }
        return cabalPath;
    }

    @NotNull
    @Override
    public String getId() {
        return HASKELL_COMPILER_ID;
    }

    @Nullable
    @Override
    public Runnable enableSearch(String s) {
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return HASKELL_COMPILER_ID;
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    /**
     * Constructs the compiler panel in Settings->Compiler. Also responsible
     * for filling in previous values or constructing sane default values.
     */
    @Nullable
    @Override
    public JComponent createComponent() {
        return mainPanel;
    }

    /**
     * Enables the apply button if anything changed.
     */
    @Override
    public boolean isModified() {
        return !(ghcAndCabalUnchanged() &&
                cabalFlags.getText().equals(mySettings.getCabalFlags()) &&
                profilingBuild.isSelected() == mySettings.isProfilingEnabled() &&
                cabalBuild.isSelected() == mySettings.isCabalEnabled() &&
                cabalSandbox.isSelected() == mySettings.isCabalSandboxEnabled() &&
                installCabalDependencies.isSelected() == mySettings.isInstallCabalDependenciesEnabled() &&
                enableTests.isSelected() == mySettings.isEnableTestsEnabled());
    }

    /**
     * Returns true if the ghc and cabal paths are unchanged.
     */
    private boolean ghcAndCabalUnchanged() {
        return (ghcPath.getText().equals(mySettings.getGhcPath()) ||
                ghcPath.getText().equals(oldGhcPath)) &&
                (cabalPath.getText().equals(mySettings.getCabalPath()) ||
                        cabalPath.getText().equals(oldCabalPath));
    }

    /**
     * Triggered when the user pushes the apply button.
     */
    @Override
    public void apply() throws ConfigurationException {
        updateVersionInfoFields();
        saveState();
    }

    /**
     * Triggered when the user pushes the cancel button.
     */
    @Override
    public void reset() {
        restoreState();
    }

    @Override
    public void disposeUIResources() {

    }

    /**
     * Persistent save of the current state.
     */
    private void saveState() {
        // Save to disk and to communicate with build server.
        mySettings.setProfilingBuild(profilingBuild.isSelected());
        mySettings.setUseCabal(cabalBuild.isSelected());
        mySettings.setUseCabalSandbox(cabalSandbox.isSelected());
        mySettings.setInstallCabalDependencies(installCabalDependencies.isSelected());
        mySettings.setEnableTests(enableTests.isSelected());
        oldGhcPath = ghcPath.getText();
        mySettings.setGhcPath(oldGhcPath);
        oldCabalPath = cabalPath.getText();
        mySettings.setCabalPath(oldCabalPath);
        mySettings.setCabalFlags(cabalFlags.getText());
    }

    /**
     * Updates the version info fields for all files configured.
     */
    private void updateVersionInfoFields() {
        ghcVersion.setText(ExecUtil.exec(ghcPath.getText() +  " --numeric-version"));
        cabalVersion.setText(ExecUtil.exec(cabalPath.getText() +  " --numeric-version"));
    }

    /**
     * Restore components to the initial state.
     */
    private void restoreState() {
        ghcPath.setText(oldGhcPath);
        cabalPath.setText(oldCabalPath);
        cabalFlags.setText(mySettings.getCabalFlags());
        profilingBuild.setSelected(mySettings.isProfilingEnabled());
        cabalBuild.setSelected(mySettings.isCabalEnabled());
        cabalSandbox.setSelected(mySettings.isCabalSandboxEnabled());
        installCabalDependencies.setSelected(mySettings.isInstallCabalDependenciesEnabled());
        enableTests.setSelected(mySettings.isEnableTestsEnabled());
    }
}
