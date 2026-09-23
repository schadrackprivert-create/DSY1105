plugins {
    kotlin("jvm") version "2.1.10"
    application
}

group = "cl.duoc.gamezone"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

kotlin {
    jvmToolchain(17)
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("gamezone.MainKt")
}
