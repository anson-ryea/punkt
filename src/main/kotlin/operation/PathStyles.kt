package com.an5on.operation

/**
 * Represents the styles for displaying paths in list operations.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
enum class PathStyles {
    /**
     * Display paths as absolute paths in the active state.
     */
    ABSOLUTE,

    /**
     * Display paths as relative to the home directory in the active state.
     */
    RELATIVE,

    /**
     * Display paths as absolute paths in the local state.
     */
    LOCAL_ABSOLUTE,

    /**
     * Display paths as relative to the local directory in the local state.
     */
    LOCAL_RELATIVE
}