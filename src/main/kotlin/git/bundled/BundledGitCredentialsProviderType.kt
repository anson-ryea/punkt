package com.an5on.git.bundled

import kotlinx.serialization.Serializable

@Serializable
enum class BundledGitCredentialsProviderType() {
    GCM,
    GH_CLI,
    ENV;
}