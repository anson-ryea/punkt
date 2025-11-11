group = "com.an5on"
version = "0.0.0"

val kotlinVersion = "2.2.21"
val cliktVersion = "5.0.3"

plugins {
    application
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"
    id("org.graalvm.buildtools.native") version "0.11.1"
    id("org.jetbrains.dokka") version "2.0.0"
    id("com.github.gmazzo.buildconfig") version "5.7.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation(platform("io.arrow-kt:arrow-stack:2.1.0"))
    implementation("io.arrow-kt:arrow-core")
    implementation("commons-io:commons-io:2.20.0")
    implementation("org.apache.commons:commons-text:1.14.0")
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.25.2"))
    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.apache.logging.log4j:log4j-core")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.13")
    implementation("com.github.ajalt.clikt:clikt:${cliktVersion}")
    implementation("com.github.ajalt.clikt:clikt-markdown:${cliktVersion}")
    implementation("com.github.mwiede:jsch:2.27.3")
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.5.0.202303070854-r") // Must not change its version to guarantee GraalVM compatibility
    implementation("org.eclipse.jgit:org.eclipse.jgit.ssh.jsch:6.5.0.202303070854-r"){
        exclude(group="com.jcraft", module="jsch") // Strip off original jsch as it is abandoned
    }
    implementation("com.h2database:h2:2.4.240")
    implementation("commons-codec:commons-codec:1.19.0")
    implementation("io.github.java-diff-utils:java-diff-utils:4.16")
    implementation("io.github.java-diff-utils:java-diff-utils-jgit:4.16")
    implementation("org.jetbrains.pty4j:pty4j:0.13.11")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("com.an5on.MainKt")
    applicationDefaultJvmArgs = listOf(
        "--enable-native-access=ALL-UNNAMED"
    )
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("punkt")
            mainClass.set("com.an5on.MainKt")
            debug.set(true)
            verbose.set(true)
            fallback.set(true)
            buildArgs.addAll(
                "--no-fallback",
                "--enable-url-protocols=https",
                "--enable-native-access=ALL-UNNAMED"
            )
        }
    }

    agent {
        defaultMode = "standard"
        enabled.set(true)
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
}

buildConfig {
    buildConfigField("APP_VERSION", provider { version.toString() })
}