plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.room)
}

val appName = "Momentum"
val appVersionCode = 1510
val appVersionName = "1.5.1"
val appNameSpace = "shub39.momentum"

val gitHash = execute("git", "rev-parse", "HEAD").take(7)

android {
    namespace = appNameSpace
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = appNameSpace
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.compileSdk.get().toInt()
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

        create("beta") {
            resValue("string", "app_name", "$appName Beta")
            applicationIdSuffix = ".beta"
            versionNameSuffix = "-beta$gitHash"
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
            versionNameSuffix = "-debug"
        }
    }

    flavorDimensions += "version"

    productFlavors {
        create("play") {
            dimension = "version"
            applicationIdSuffix = ".play"
            versionNameSuffix = "-play"
        }

        create("foss") {
            dimension = "version"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    splits {
        abi {
            //noinspection WrongGradleMethod
            val isBuildingBundle =
                gradle.startParameter.taskNames.any { it.lowercase().contains("bundle") }

            isEnable = !isBuildingBundle
            reset()
            include("x86", "x86_64", "arm64-v8a", "armeabi-v7a")
            isUniversalApk = true
        }
    }
}

dependencies {
    implementation(project(":montage"))

    "playImplementation"(libs.purchases.ui)
    "playImplementation"(libs.purchases)

    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.calendar)
    implementation(libs.filekit.core)
    implementation(libs.filekit.dialogs.compose)
    implementation(libs.landscapist.coil)
    implementation(libs.materialKolor)
    implementation(libs.colorpicker.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.compose.viewmodel.navigation)
    ksp(libs.koin.ksp.compiler)
    api(libs.koin.annotations)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.exifinterface)
    implementation(libs.tasks.vision)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.room.testing)
    testImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.truth)
}

room {
    schemaDirectory("$projectDir/schemas")
}

fun execute(vararg command: String): String = providers.exec {
    commandLine(*command)
}.standardOutput.asText.get().trim()

val generateChangelogJson by tasks.registering {
    val inputFile = rootProject.file("CHANGELOG.md")
    val outputDir = file("$projectDir/src/main/assets/")
    val outputFile = File(outputDir, "changelog.json")

    inputs.file(inputFile)
    outputs.file(outputFile)

    doLast {
        if (!outputDir.exists()) outputDir.mkdirs()

        val lines = inputFile.readLines()

        val map = mutableMapOf<String, MutableList<String>>()
        var currentVersion: String? = null

        for (line in lines) {
            when {
                line.startsWith("## ") -> {
                    currentVersion = line.removePrefix("## ").trim()
                    map[currentVersion] = mutableListOf()
                }

                line.startsWith("- ") && currentVersion != null -> {
                    map[currentVersion]?.add(
                        line.removePrefix("- ").trim()
                    )
                }
            }
        }

        val json = buildString {
            append("[\n")

            map.entries.forEachIndexed { index, entry ->
                append("  {\n")
                append("    \"version\": \"${entry.key}\",\n")
                append("    \"changes\": [\n")

                entry.value.forEachIndexed { i, item ->
                    append("      \"${item.replace("\"", "\\\"")}\"")
                    if (i != entry.value.lastIndex) append(",")
                    append("\n")
                }

                append("    ]\n")
                append("  }")

                if (index != map.entries.size - 1) append(",")
                append("\n")
            }

            append("]")
        }


        outputFile.writeText(json)
    }
}

tasks.named("preBuild") {
    dependsOn(generateChangelogJson)
}