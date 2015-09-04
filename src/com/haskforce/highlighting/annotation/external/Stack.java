package com.haskforce.highlighting.annotation.external;

import com.haskforce.settings.SettingsChangeNotifier;
import com.haskforce.settings.ToolSettings;
import com.intellij.openapi.module.ModuleComponent;
import org.jetbrains.annotations.NotNull;

/**
 * Created by developer on 4/09/15.
 */
public class Stack implements ModuleComponent, SettingsChangeNotifier {

    private String path;
    private String flags;

    @Override
    public void projectOpened() {

    }

    @Override
    public void projectClosed() {

    }

    @Override
    public void moduleAdded() {

    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "Stack";
    }

    @Override
    public void onSettingsChanged(@NotNull ToolSettings settings) {
        path = settings.getPath();
        flags = settings.getFlags();
    }
}
