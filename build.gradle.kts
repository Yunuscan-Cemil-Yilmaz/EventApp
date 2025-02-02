import org.jetbrains.kotlin.fir.declarations.builder.buildScript

// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Project Level
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false // for Firebase
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false // Match the Kotlin version
}

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.2")
    }
}