plugins {
    kotlin("jvm") version "2.2.20"
    id("org.graalvm.buildtools.native") version "0.11.1"
    id("org.jetbrains.dokka") version "2.0.0"
}

group = "com.an5on"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.ajalt.clikt:clikt:5.0.3")
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