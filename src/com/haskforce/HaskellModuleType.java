package com.haskforce;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;

public class HaskellModuleType extends ModuleType<HaskellModuleBuilder> {
    public static final String MODULE_TYPE_ID = "HASKELL_MODULE";

    public HaskellModuleType() {
        super(MODULE_TYPE_ID);
    }

    public static HaskellModuleType getInstance() {
        return (HaskellModuleType) ModuleTypeManager.getInstance().findByID(MODULE_TYPE_ID);
    }

    public static Collection<Module> findModules(@NotNull Project project) {
        return ModuleUtil.getModulesOfType(project, HaskellModuleType.getInstance());
    }

    @NotNull
    @Override
    public HaskellModuleBuilder createModuleBuilder() {
        return new HaskellModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return "Haskell Module";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Haskell modules are used for developing <b>Haskell</b> applications.";
    }

    @Override
    public Icon getBigIcon() {
        return HaskellIcons.FILE;
    }

    @Override
    public Icon getNodeIcon(@Deprecated boolean isOpened) {
        return HaskellIcons.FILE;
    }
}
