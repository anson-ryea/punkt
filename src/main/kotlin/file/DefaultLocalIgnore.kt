package com.an5on.file

import com.an5on.config.ActiveConfiguration.configuration


/**
 * An implementation of the [Ignore] interface that provides default local state ignore patterns from the configuration.
 *
 * This object retrieves ignore patterns from the global configuration and applies them specifically to local state files.
 * The path matchers are built with the local state path prefix to ensure patterns are matched relative to the
 * local state directory.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object DefaultLocalIgnore : Ignore {
    override val ignorePatterns: Set<String>
        get() = configuration.global.ignoredLocalFiles
    override val ignorePathMatchers
        get() = buildPathMatchersFromPatterns(
            ignorePatterns,
            true
        )
}