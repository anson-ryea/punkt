package com.an5on.git.bundled

import arrow.core.raise.Raise
import arrow.core.raise.catch
import com.an5on.error.GitError
import org.eclipse.jgit.api.Git
import java.nio.file.Path
import kotlin.io.path.pathString

object BundledAddOperation {
    fun Raise<GitError>.bundledAdd(repoPath: Path, targetPath: Path) {
        catch(
            {
                val relativeTargetPath = repoPath.relativize(targetPath)
                val localRepo = Git.open(repoPath.toFile())

                localRepo.add()
                    .addFilepattern(relativeTargetPath.pathString)
                    .call()
            },
            { e ->
                when (e) {
                    else -> throw e
                }
            }
        )
    }
}