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

# gradle
gradle -v
gradle wrapper
gradle wrapper --gradle-version 8.0
./gradlew init



# build.gradle.kts 
create build.gradle.kts file in EventApp
and write this:
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

# settings.gradle.kts: 
create settings.gradle.kts
and write this:
pluginManagement {
repositories {
google()
mavenCentral()
gradlePluginPortal()
}
}
dependencyResolutionManagement {
repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
repositories {
google()
mavenCentral()
}
}

rootProject.name = "Eventapp"
include(":app")

# import project
and import project