package com.an5on.git.system

object SystemGitUtils {
    val isGitInstalled: Boolean
        get() = try {
            val process = ProcessBuilder("git", "--version")
                .redirectErrorStream(true)
                .start()
            process.waitFor() == 0
        } catch (e: Exception) {
            false
        }

}