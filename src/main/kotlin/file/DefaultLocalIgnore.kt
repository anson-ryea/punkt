package com.an5on.file

import com.an5on.config.ActiveConfiguration.configuration

object DefaultLocalIgnore : Ignore {
    override val ignorePatterns: Set<String>
        get() = configuration.global.ignoredLocalFiles
    override val ignorePathMatchers
        get() = buildPathMatchersFromPatterns(
            ignorePatterns,
            true
        )
}