plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
    id("org.graalvm.buildtools.native") version "0.11.1"
    id("org.jetbrains.dokka") version "2.0.0"
}

group = "com.an5on"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("slf4j:slf4j-api:2.0.17")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.13")
    implementation("com.github.ajalt.clikt:clikt:5.0.3")
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.5.0.202303070854-r") // Must not change its version to guarantee GraalVM compatibility
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("punkt")
            mainClass.set("com.an5on.MainKt")
            debug.set(true)
            verbose.set(true)
            fallback.set(true)
        }
    }
}