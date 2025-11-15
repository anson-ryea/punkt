package com.an5on.type

/**
 * An enumeration representing the styles for displaying paths in list operations.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
enum class PathStyle {
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