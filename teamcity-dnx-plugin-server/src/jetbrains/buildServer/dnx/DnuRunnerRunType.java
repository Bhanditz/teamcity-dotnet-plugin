/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * See LICENSE in the project root for license information.
 */

package jetbrains.buildServer.dnx;

import jetbrains.buildServer.requirements.Requirement;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * DNX utility runner definition.
 */
public class DnuRunnerRunType extends RunType {

    private final PluginDescriptor myPluginDescriptor;

    public DnuRunnerRunType(@NotNull final PluginDescriptor pluginDescriptor,
                            @NotNull final RunTypeRegistry runTypeRegistry) {
        myPluginDescriptor = pluginDescriptor;
        runTypeRegistry.registerRunType(this);
    }

    @NotNull
    @Override
    public String getType() {
        return DnuConstants.DNU_RUNNER_TYPE;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return DnuConstants.DNU_RUNNER_DISPLAY_NAME;
    }

    @NotNull
    @Override
    public String getDescription() {
        return DnuConstants.DNU_RUNNER_DESCRIPTION;
    }

    @Override
    public PropertiesProcessor getRunnerPropertiesProcessor() {
        return new PropertiesProcessor() {
            @Override
            public Collection<InvalidProperty> process(Map<String, String> map) {
                return Collections.emptyList();
            }
        };
    }

    @Override
    public String getEditRunnerParamsJspFilePath() {
        return myPluginDescriptor.getPluginResourcesPath("editDnuParameters.jsp");
    }

    @Override
    public String getViewRunnerParamsJspFilePath() {
        return myPluginDescriptor.getPluginResourcesPath("viewDnuParameters.jsp");
    }

    @Override
    public Map<String, String> getDefaultRunnerProperties() {
        return new HashMap<>();
    }

    @NotNull
    @Override
    public String describeParameters(@NotNull Map<String, String> parameters) {
        return super.describeParameters(parameters);
    }

    @NotNull
    @Override
    public List<Requirement> getRunnerSpecificRequirements(@NotNull Map<String, String> runParameters) {
        return Collections.emptyList();
    }
}
