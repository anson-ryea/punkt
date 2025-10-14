package com.an5on.git.bundled

import arrow.core.raise.Raise
import arrow.core.raise.catch
import com.an5on.error.GitError
import org.eclipse.jgit.api.Git
import java.nio.file.Path

object BundledInitOperation {
    fun Raise<GitError>.bundledInit(path: Path) {
        catch(
            {
                Git.init().setDirectory(path.toFile()).call()
            },
            { e ->
                when (e) {
                    else -> throw e
                }
            }
        )
    }
}