package com.an5on.git.system

import com.an5on.config.ActiveConfiguration.configuration
import java.nio.file.Path

abstract class SystemGitExecutor {
     open fun execute(args: List<String>, workingPath: Path): Int {
        val process = ProcessBuilder(configuration.git.systemGitCommand, *args.toTypedArray())
            .directory(workingPath.toFile())
            .inheritIO()
            .start()

        return process.waitFor()
    }
}