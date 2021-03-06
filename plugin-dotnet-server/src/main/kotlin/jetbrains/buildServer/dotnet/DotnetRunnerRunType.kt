/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * See LICENSE in the project root for license information.
 */

package jetbrains.buildServer.dotnet

import jetbrains.buildServer.requirements.Requirement
import jetbrains.buildServer.serverSide.InvalidProperty
import jetbrains.buildServer.serverSide.PropertiesProcessor
import jetbrains.buildServer.serverSide.RunType
import jetbrains.buildServer.serverSide.RunTypeRegistry
import jetbrains.buildServer.util.StringUtil
import jetbrains.buildServer.web.openapi.PluginDescriptor

/**
 * Dotnet runner definition.
 */
class DotnetRunnerRunType(
        private val _pluginDescriptor: PluginDescriptor,
        runTypeRegistry: RunTypeRegistry) : RunType() {

    init {
        _pluginDescriptor.pluginResourcesPath
        runTypeRegistry.registerRunType(this)
    }

    override fun getType(): String {
        return DotnetConstants.RUNNER_TYPE
    }

    override fun getDisplayName(): String {
        return DotnetConstants.RUNNER_DISPLAY_NAME
    }

    override fun getDescription(): String {
        return DotnetConstants.RUNNER_DESCRIPTION
    }

    override fun getRunnerPropertiesProcessor(): PropertiesProcessor? {
        return PropertiesProcessor { properties ->
            val command = properties?.get(DotnetConstants.PARAM_COMMAND)
            if (command.isNullOrEmpty()) {
                return@PropertiesProcessor arrayListOf(InvalidProperty(DotnetConstants.PARAM_COMMAND, "Command must be set"))
            }

            val errors = arrayListOf<InvalidProperty>()
            DotnetParametersProvider.commandTypes[command]?.let {
                errors.addAll(it.validateProperties(properties))
            }

            properties[CoverageConstants.PARAM_TYPE]?.let {
                DotnetParametersProvider.coverageTypes[it]?.let {
                    errors.addAll(it.validateProperties(properties))
                }
            }

            errors
        }
    }

    override fun getEditRunnerParamsJspFilePath(): String? {
        return _pluginDescriptor.getPluginResourcesPath("editDotnetParameters.jsp")
    }

    override fun getViewRunnerParamsJspFilePath(): String? {
        return _pluginDescriptor.getPluginResourcesPath("viewDotnetParameters.jsp")
    }

    override fun getDefaultRunnerProperties(): Map<String, String>? {
        return emptyMap()
    }

    override fun describeParameters(parameters: Map<String, String>): String {
        val paths = parameters[DotnetConstants.PARAM_PATHS] ?: ""
        val commandName = parameters[DotnetConstants.PARAM_COMMAND]?.replace('-', ' ')
        return if (!commandName.isNullOrBlank()) {
            "$commandName $paths"
        } else {
            parameters[DotnetConstants.PARAM_ARGUMENTS]?.let {
                StringUtil.splitCommandArgumentsAndUnquote(it).take(2).joinToString(" ")
            } ?: ""
        }
    }

    override fun getRunnerSpecificRequirements(runParameters: Map<String, String>): List<Requirement> {
        val requirements = arrayListOf<Requirement>()
        runParameters[DotnetConstants.PARAM_COMMAND]?.let {
            DotnetParametersProvider.commandTypes[it]?.let {
                requirements.addAll(it.getRequirements(runParameters))
            }
        }

        runParameters[CoverageConstants.PARAM_TYPE]?.let {
            DotnetParametersProvider.coverageTypes[it]?.let {
                requirements.addAll(it.getRequirements(runParameters))
            }
        }

        return requirements
    }
}
