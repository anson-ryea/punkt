package com.an5on.git

import kotlinx.serialization.Serializable

/**
 * An enumeration of the supported sources for obtaining credentials for the bundled JGit library.
 *
 * This enum defines the different strategies that can be used to authenticate with a remote Git repository when
 * using the built-in JGit implementation. The order of preference for these providers can be configured by the user.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
@Serializable
enum class GitCredentialsProviderForBundledType() {
    /**
     * Use the Git Credential Manager (GCM).
     *
     * This strategy attempts to retrieve credentials by invoking the system's `git-credential-manager` executable.
     */
    GCM,

    /**
     * Use the GitHub Command-Line Interface (CLI).
     *
     * This strategy attempts to retrieve an authentication token by invoking the `gh auth token` command.
     */
    GH_CLI,

    /**
     * Use environment variables.
     *
     * This strategy looks for the `GIT_USERNAME` and `GIT_PASSWORD` environment variables to use for authentication.
     */
    ENV;
}