package com.an5on.config


/**
 * Represents the Git-related configuration settings for the Punkt application.
 *
 * @property addOnLocalChange Whether to automatically add files to Git when local changes are detected.
 * @property commitOnLocalChange Whether to automatically commit changes when local changes are detected.
 * @property pushOnLocalChange Whether to automatically push changes when local changes are detected.
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
data class GitConfiguration(
    val addOnLocalChange: Boolean,
    val commitOnLocalChange: Boolean,
    val pushOnLocalChange: Boolean,
)
