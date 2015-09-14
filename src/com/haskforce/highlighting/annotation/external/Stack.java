package com.haskforce.highlighting.annotation.external;

import com.haskforce.settings.SettingsChangeNotifier;
import com.haskforce.settings.ToolKey;
import com.haskforce.settings.ToolSettings;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class Stack implements ModuleComponent, SettingsChangeNotifier {

    private String path;
    private String flags;
    private BufferedReader input;
    private BufferedWriter output;

    public Stack(@NotNull Module module){
        path = PropertiesComponent.getInstance(module.getProject()).getValue(ToolKey.STACK_KEY);
    }

    public void initProcess(){
        GeneralCommandLine commandLine = new GeneralCommandLine("/home/developer/.cabal/bin/stack");
        ParametersList parametersList = commandLine.getParametersList();
        parametersList.addParametersString("ide");
        parametersList.addParametersString("start");
        commandLine.setRedirectErrorStream(true);
        Process process;
        try {
            process = commandLine.createProcess();
        } catch (ExecutionException e) {
            throw new RuntimeException(e.toString());
        }
        input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        output = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

        try {
            consumeBullshit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void consumeBullshit() throws IOException {
        while (input.ready()) {
            String discard = input.readLine();
            System.out.println(discard);
        }
        try {
            Thread.sleep(500);
            if(input.ready()){
                consumeBullshit();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

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

    public String exec(String s) {
        if (input == null | output == null) {
            initProcess();
        }

        try {
            output.write(s);
            output.flush();
            return input.readLine();
        } catch (IOException e) {
            return null;
        }
    }
}
