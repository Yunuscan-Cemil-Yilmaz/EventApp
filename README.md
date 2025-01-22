# EventApp
a event app for lists local events and selecting with kotlin, firebase and google apies

# Firebase
create a firebase project and download a google-services.json file for your project
and move the google-services.json file into app folder.

# local.properties
sdk.dir=your_sdk_dir
WEB_CLIENT_ID=your_web_client_id
TICKET_MASTER_API=your_ticketmaster_api_key
MAPS_API=your_google_maps_api_key


# build.gradle.kts
project level build gradle:
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