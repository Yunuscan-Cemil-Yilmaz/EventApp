// App Level

import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services") // for Firebase
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) // Match the Kotlin version
}

val localProperties = File(rootDir, "local.properties")
val webClientId = if (localProperties.exists()) {
    val properties = Properties()
    properties.load(localProperties.inputStream())
    properties.getProperty("WEB_CLIENT_ID") ?: ""
} else {
    ""
}
val ticketMasterApi = if (localProperties.exists()) {
    val properties = Properties()
    properties.load(localProperties.inputStream())
    properties.getProperty("TICKET_MASTER_API") ?: ""
} else {
    ""
}
val mapsApi = if (localProperties.exists()) {
    val properties = Properties()
    properties.load(localProperties.inputStream())
    properties.getProperty("MAPS_API") ?: ""
}else {
    ""
}

android {
    namespace = "com.example.eventapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.eventapp"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "WEB_CLIENT_ID", "\"${webClientId}\"")
        buildConfigField("String", "TICKET_MASTER_API", "\"$ticketMasterApi\"")
        buildConfigField("String", "MAPS_API_KEY", "\"${mapsApi}\"")
        manifestPlaceholders["MAPS_API_KEY"] = mapsApi
    }

    buildTypes {
        debug {
            buildConfigField("String", "WEB_CLIENT_ID", "\"${webClientId}\"")
            buildConfigField("String", "TICKET_MASTER_API", "\"$ticketMasterApi\"")
            buildConfigField("String", "MAPS_API_KEY", "\"${mapsApi}\"")
            manifestPlaceholders["MAPS_API_KEY"] = mapsApi
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "WEB_CLIENT_ID", "\"${webClientId}\"")
            buildConfigField("String", "TICKET_MASTER_API", "\"$ticketMasterApi\"")
            buildConfigField("String", "MAPS_API_KEY", "\"${mapsApi}\"")
            manifestPlaceholders["MAPS_API_KEY"] = mapsApi
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true// enable build config property
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    viewBinding{
        enable = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("com.google.firebase:firebase-auth:22.1.1") // for Firebase Auth
    implementation("com.google.android.gms:play-services-auth:20.5.0") // Google Play Services Auth
    implementation("com.google.firebase:firebase-auth:21.0.6")
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-analytics")
    implementation("androidx.credentials:credentials")
    implementation("androidx.credentials:credentials-play-services-auth")
    implementation("com.google.android.libraries.identity.googleid:googleid")
    implementation("com.google.firebase:firebase-firestore:25.1.1")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0") // Latest version
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.4")
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.cardview)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}