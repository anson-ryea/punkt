package com.an5on.command

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.type.VerbosityType

object CommandUtils {
    fun determineVerbosity(verbosityOption: VerbosityType?) = verbosityOption ?: configuration.general.verbosity
}