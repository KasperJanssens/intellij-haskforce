package com.haskforce.jps.model;

import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.xmlb.annotations.AbstractCollection;
import com.intellij.util.xmlb.annotations.Tag;

import java.util.List;

/**
 * Serialization object for communicating build settings with the build server.
 */
public class HaskellBuildOptions {
    public static final boolean DEFAULT_USE_CABAL = true;
    public static final boolean DEFAULT_USE_CABAL_SANDBOX = false;
    public static final boolean DEFAULT_USE_PROFILING_BUILD = true;
    public static final String DEFAULT_GHC_PATH = "ghc";
    public static final String DEFAULT_CABAL_PATH = "cabal";
    public static final String DEFAULT_CABAL_FLAGS = "";
    public static final boolean DEFAULT_INSTALL_CABAL_DEPENDENCIES = false;

    public HaskellBuildOptions() {
    }

    public HaskellBuildOptions(HaskellBuildOptions options) {
        myUseCabal = options.myUseCabal;
        myUseCabalSandbox = options.myUseCabalSandbox;
        myProfilingBuild = options.myProfilingBuild;
        myGhcPath = options.myGhcPath;
        myCabalPath = options.myCabalPath;
        myCabalFlags = options.myCabalFlags;
        myInstallCabalDependencies = options.myInstallCabalDependencies;
    }

    @Tag("useCabal")
    public boolean myUseCabal = DEFAULT_USE_CABAL;

    @Tag("useCabalSandbox")
    public boolean myUseCabalSandbox = DEFAULT_USE_CABAL_SANDBOX;

    @Tag("useProfilingBuild")
    public boolean myProfilingBuild = DEFAULT_USE_PROFILING_BUILD;

    @Tag("ghcPath")
    public String myGhcPath = DEFAULT_GHC_PATH;

    @Tag("cabalPath")
    public String myCabalPath = DEFAULT_CABAL_PATH;

    @Tag("cabalFlags")
    public String myCabalFlags = DEFAULT_CABAL_FLAGS;

    @Tag("cabalFiles")
    @AbstractCollection(surroundWithTag = false, elementTag = "cabalFile")
    public List<String> myCabalFiles = ContainerUtil.newArrayList();

    @Tag("installDependencies")
    public boolean myInstallCabalDependencies = DEFAULT_INSTALL_CABAL_DEPENDENCIES;

    @Override
    public String toString() {
        return "HaskellBuildOptions{" +
                "myUseCabal=" + myUseCabal +
                ", myUseCabalSandbox=" + myUseCabalSandbox +
                ", myProfilingBuild=" + myProfilingBuild +
                ", myGhcPath=" + myGhcPath +
                ", myCabalPath=" + myCabalPath +
                ", myCabalFlags=" + myCabalFlags +
                ", myCabalFiles=" + myCabalFiles +
                ", myInstallCabalDependencies=" + myInstallCabalDependencies +
                '}';
    }
}
