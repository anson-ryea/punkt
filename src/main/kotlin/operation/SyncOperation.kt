package com.an5on.operation

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.command.CommandUtils.determineVerbosity
import com.an5on.command.Echos
import com.an5on.command.options.CommonOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.git.AddOperation.add
import com.an5on.git.CommitOperation.commit
import com.an5on.git.PushOperation.push
import com.an5on.operation.OperationUtils.existingLocalPathsToActivePaths
import com.an5on.operation.OperationUtils.expand
import com.an5on.states.local.LocalState
import com.an5on.states.local.LocalTransactionCopyToLocal
import com.an5on.states.local.LocalTransactionMakeDirectories
import com.an5on.type.VerbosityType
import org.apache.commons.io.filefilter.RegexFileFilter
import java.nio.file.Path
import kotlin.io.path.isDirectory

/**
 * Handles the sync operation to synchronize files from the active state to the local state.
 *
 * This object provides operations to sync paths, either by syncing existing local files or specific paths.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
object SyncOperation {
    /**
     * Syncs the specified active paths or all existing local files if no paths are provided.
     *
     * @param activePaths the set of active paths to sync, or null to sync all existing local files
     * @param commonOptions the sync options
     * @param echos the echo functions for output
     */
    fun Raise<PunktError>.sync(activePaths: Set<Path>?, globalOptions: GlobalOptions, commonOptions: CommonOptions, echos: Echos) {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        if (activePaths.isNullOrEmpty()) {
            syncExistingLocal(globalOptions, commonOptions, echos)
        } else {
            syncPaths(activePaths, globalOptions, commonOptions, echos)
        }

        if (configuration.git.addOnLocalChange) {
            add(configuration.general.localStatePath,
                globalOptions.useBundledGit
                )
        }

        if (configuration.git.commitOnLocalChange) {
            commit("test",
                globalOptions.useBundledGit
            )
        }

        if (configuration.git.pushOnLocalChange) {
            push(false, globalOptions.useBundledGit)
        }
    }

    /**
     * Syncs the specified set of active paths.
     *
     * @param activePaths the set of active paths to sync
     * @param commonOptions the sync options
     * @param echos the echo functions for output
     */
    private fun Raise<PunktError>.syncPaths(activePaths: Set<Path>, globalOptions: GlobalOptions, commonOptions: CommonOptions, echos: Echos) {
        val verbosity = determineVerbosity(globalOptions.verbosity)

        val includeExcludeFilter = RegexFileFilter(commonOptions.include.pattern)
            .and(RegexFileFilter(commonOptions.exclude.pattern).negate())
//            .and(ActiveEqualsLocalFileFilter.negate())

        val expandedActivePaths = activePaths.flatMap { activePath ->
            echos.echoStage("Syncing: $activePath", verbosity, VerbosityType.NORMAL)

            activePath.expand(commonOptions.recursive, includeExcludeFilter)
        }.toSet()

        LocalState.pendingTransactions.addAll(
            expandedActivePaths.map { activePath ->
                if (activePath.isDirectory()) {
                    LocalTransactionMakeDirectories(activePath)
                } else {
                    LocalTransactionCopyToLocal(activePath)
                }
            }
        )

        LocalState.commit()
    }

    private fun Raise<PunktError>.syncExistingLocal(globalOptions: GlobalOptions, commonOptions: CommonOptions, echos: Echos) {
        syncPaths(existingLocalPathsToActivePaths, globalOptions, commonOptions, echos)
    }
}