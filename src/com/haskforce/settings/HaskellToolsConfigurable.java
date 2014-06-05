package com.haskforce.settings;


import com.haskforce.utils.ExecUtil;
import com.intellij.execution.ui.layout.Grid;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.TextAccessor;
import com.intellij.util.ui.GridBag;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.haskforce.utils.GuiUtil.createButton;
import static com.haskforce.utils.GuiUtil.createDisplayVersion;
import static com.haskforce.utils.GuiUtil.createExecutableOption;

/**
 * The "Haskell Tools" option in Preferences->Project Settings.
 */
public class HaskellToolsConfigurable implements SearchableConfigurable {
    public static final String HASKELL_TOOLS_ID = "Haskell Tools";

    private Project project;

    // Old values to detect user updates.
    private String oldParserHelperPath;
    private String oldStylishPath;

    // Swing components.
    private JPanel settings;
    private TextFieldWithBrowseButton parserHelperPath;
    private JLabel parserHelperVersion;
    private TextFieldWithBrowseButton stylishPath;
    private JLabel stylishVersion;

    public HaskellToolsConfigurable(@NotNull Project inProject) {
        project = inProject;
        oldParserHelperPath = PropertiesComponent.getInstance(project).getValue("parserHelperPath", "");
        oldStylishPath = PropertiesComponent.getInstance(project).getValue("stylishPath", "");
    }

    @NotNull
    @Override
    public String getId() {
        return HASKELL_TOOLS_ID;
    }

    @Nullable
    @Override
    public Runnable enableSearch(String s) {
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return HASKELL_TOOLS_ID;
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    /**
     * Creates the GUI panel and sets the text fields to the old values.
     */
    @Nullable
    @Override
    public JComponent createComponent() {
        settings = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1;
        c.gridx = 0;

        // Parser-helper configuration.
        c.gridy = 0;
        parserHelperPath = createExecutableOption(settings, "Parser-Helper", c);

        c.gridy = 1;
        parserHelperVersion = createDisplayVersion(settings, "Parser-Helper", c);

        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        createButton(settings, "Auto find", createApplyPathAction(parserHelperPath, "parser-helper"), c);

        if (!oldParserHelperPath.isEmpty()) {
            parserHelperPath.setText(oldParserHelperPath);
        }

        c.fill = GridBagConstraints.HORIZONTAL;

        // Stylish configuration.
        c.gridy = 3;
        stylishPath = createExecutableOption(settings, "Stylish-Haskell", c);

        c.gridy = 4;
        stylishVersion = createDisplayVersion(settings, "Stylish-Haskell", c);

        c.gridy = 5;
        c.fill = GridBagConstraints.NONE;
        createButton(settings, "Auto find", createApplyPathAction(stylishPath, "stylish-haskell"), c);

        if (!oldStylishPath.isEmpty()) {
            stylishPath.setText(oldStylishPath);
        }

        updateVersionInfoFields();
        return settings;
    }

    private static ActionListener createApplyPathAction(final TextAccessor textField, final String executable) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String guessedPath = ExecUtil.locateExecutableByGuessing(executable);
                if (guessedPath != null) {
                    textField.setText(guessedPath);
                } else {
                    Messages.showErrorDialog("Could not find '" + executable + "'.", "HaskForce");
                }
            }
        };
    }

    /**
     * Enables the apply button if anything changed.
     */
    @Override
    public boolean isModified() {
        return !(parserHelperPath.getText().equals(oldParserHelperPath) &&
                    stylishPath.getText().equals(oldStylishPath));
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
     * Heuristically finds the version number. Current implementation is the
     * identity function since cabal plays nice.
     */
    private static String getVersion(String cmd, String versionflag) {
        return ExecUtil.exec(cmd + ' ' + versionflag);
    }

    /**
     * Updates the version info fields for all files configured.
     */
    private void updateVersionInfoFields() {
        // Parser-helper.
        if (!parserHelperPath.getText().isEmpty()) {
            parserHelperVersion.setText(getVersion(parserHelperPath.getText(), "--numeric-version"));
        }

        // Stylish-Haskell.
        if (!stylishPath.getText().isEmpty()) {
            stylishVersion.setText(getVersion(stylishPath.getText(), "--version"));
        }
    }

    /**
     * Persistent save of the current state.
     */
    private void saveState() {
        PropertiesComponent.getInstance(project).setValue("parserHelperPath", parserHelperPath.getText());
        PropertiesComponent.getInstance(project).setValue("stylishPath", stylishPath.getText());
        oldParserHelperPath = parserHelperPath.getText();
        oldStylishPath = stylishPath.getText();
    }

    /**
     * Restore components to the initial state.
     */
    private void restoreState() {
        parserHelperPath.setText(oldParserHelperPath);
        stylishPath.setText(oldStylishPath);
    }
}
