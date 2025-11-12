package com.an5on.git

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.GitError
import com.an5on.error.LocalError
import com.an5on.states.local.LocalState

class GenericOperationWithSystem(
    private val args: List<String>
) : GitOperableWithSystem {
    override fun operateWithSystem(): Either<GitError, Int> = either {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        executeSystemGit(args, configuration.global.localStatePath).bind()
    }
}