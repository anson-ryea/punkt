package com.an5on.config

data class GitConfiguration(
    val addOnLocalChange: Boolean,
    val commitOnLocalChange: Boolean,
    val pushOnLocalChange: Boolean,
)
