package com.an5on.git

import kotlinx.serialization.Serializable

@Serializable
enum class BundledGitCredentialsProviderType() {
    GCM,
    GH_CLI,
    ENV;
}