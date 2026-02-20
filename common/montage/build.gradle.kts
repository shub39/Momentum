plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "shub39.montage"
    compileSdk = libs.versions.compileSdk.get().toInt()

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(project(":common:core"))

    implementation(libs.androidx.annotation.jvm)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.exifinterface)
    implementation(libs.androidx.core)
}