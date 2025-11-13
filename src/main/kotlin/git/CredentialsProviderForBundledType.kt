package com.an5on.git

import kotlinx.serialization.Serializable

@Serializable
enum class CredentialsProviderForBundledType() {
    GCM,
    GH_CLI,
    ENV;
}