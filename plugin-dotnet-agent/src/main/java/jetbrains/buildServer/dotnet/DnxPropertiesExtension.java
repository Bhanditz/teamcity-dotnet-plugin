/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * See LICENSE in the project root for license information.
 */

package jetbrains.buildServer.dotnet;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Provides a list of available DNX runtimes.
 */
public class DnxPropertiesExtension extends AgentLifeCycleAdapter {

    private static final Logger LOG = Logger.getInstance(DnxRuntimeDetector.class.getName());
    private final DnxRuntimeDetector myRuntimeDetector;
    private final DnxToolProvider myToolProvider;

    public DnxPropertiesExtension(@NotNull final EventDispatcher<AgentLifeCycleListener> events,
                                  @NotNull final DnxRuntimeDetector runtimeDetector,
                                  @NotNull final DnxToolProvider toolProvider) {
        myRuntimeDetector = runtimeDetector;
        myToolProvider = toolProvider;
        events.addListener(this);
    }

    @Override
    public void beforeAgentConfigurationLoaded(@NotNull BuildAgent agent) {
        final BuildAgentConfiguration config = agent.getConfiguration();
        final String toolPath;

        LOG.info("Observing DNX runtimes");
        final Map<String, String> runtimes = myRuntimeDetector.getRuntimes();
        for (String name : runtimes.keySet()) {
            LOG.info(String.format("Found DNX runtime %s at %s", name, runtimes.get(name)));
            config.addConfigurationParameter(name, runtimes.get(name));
        }

        LOG.info("Locating DNX tools");
        try {
            toolPath = myToolProvider.getPath(DnxConstants.RUNNER_TYPE);
        } catch (ToolCannotBeFoundException e) {
            LOG.debug(e);
            return;
        }

        LOG.info("Found DNX at " + toolPath);
        config.addConfigurationParameter(DnxConstants.CONFIG_PATH, toolPath);
    }
}