import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.aboutLibraries)
}

val appName = "Momentum"
val appVersionCode = 1000
val appVersionName = "1.0.0"
val appNameSpace = "shub39.momentum"

android {
    namespace = appNameSpace
    compileSdk = 36

    defaultConfig {
        applicationId = appNameSpace
        minSdk = 31
        targetSdk = 36
        versionCode = appVersionCode
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            resValue("string", "app_name", appName)
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            resValue("string", "app_name", "$appName Debug")
            applicationIdSuffix = ".debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JvmTarget.JVM_17.target
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

dependencies {
    configurations.all {
        exclude(group = "com.intellij", module = "annotations")
    }

    implementation(libs.material.icons.core)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.calendar)
    implementation(libs.filekit.core)
    implementation(libs.filekit.dialogs.compose)
    implementation(libs.landscapist.coil)
//    implementation(libs.reorderable)
    implementation(libs.materialKolor)
    implementation(libs.colorpicker.compose)
//    implementation(libs.aboutLibraries)
//    implementation(libs.aboutLibraries.compose.m3)
    implementation(libs.composeIcons.fontAwesome)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.compiler)
    implementation(libs.sqlite.bundled)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.compose.navigation)
}