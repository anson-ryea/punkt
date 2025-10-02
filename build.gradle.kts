plugins {
    application
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
    id("org.graalvm.buildtools.native") version "0.11.1"
    id("org.jetbrains.dokka") version "2.0.0"
}

group = "com.an5on"
version = "1.0-SNAPSHOT"

val cliktVersion = "5.0.3"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
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
    implementation("org.eclipse.store:storage-embedded:3.0.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("com.an5on.MainKt")
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("punkt")
            mainClass.set("com.an5on.MainKt")
            debug.set(true)
            verbose.set(true)
            fallback.set(true)
            buildArgs.addAll(listOf(
                "--enable-url-protocols=https"
            ))
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