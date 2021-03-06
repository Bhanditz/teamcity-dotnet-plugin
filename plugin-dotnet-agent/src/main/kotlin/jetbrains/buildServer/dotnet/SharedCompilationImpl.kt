package jetbrains.buildServer.dotnet

import jetbrains.buildServer.agent.Environment
import jetbrains.buildServer.agent.TargetType
import jetbrains.buildServer.agent.runner.TargetRegistry
import jetbrains.buildServer.util.OSType
import kotlin.coroutines.experimental.buildSequence

class SharedCompilationImpl(
        private val _environment: Environment,
        private val _targetRegistry: TargetRegistry)
    : SharedCompilation {
    override fun requireSuppressing(context: DotnetBuildContext): Boolean {
        return context.sdks.maxBy { it.version }?.let {
            // Prevents the case when VBCSCompiler service remains in memory after `dotnet build` for Linux and consumes 100% of 1 CPU core and a lot of memory
            // https://youtrack.jetbrains.com/issue/TW-55268
            // https://github.com/dotnet/roslyn/issues/27566
            if (it.version > Version.LastVersionWithoutSharedCompilation) {
                when (_environment.os) {
                    OSType.UNIX, OSType.MAC -> true
                    else -> {
                        // dotCover is waiting for finishing of all spawned processes including a build's infrastructure processes
                        // https://github.com/JetBrains/teamcity-dotnet-plugin/issues/132
                        _targetRegistry.activeTargets.contains(TargetType.CodeCoverageProfiler)
                    }
                }
            } else false
        } ?: false
    }
}