plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.kangqi.hic"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.kangqi.hic"
        minSdk = 30
        targetSdk = 35
        versionCode = 30
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Xposed API
    compileOnly("de.robv.android.xposed:api:82:sources")
    compileOnly("de.robv.android.xposed:api:82")

    // AndroidX & Compose
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")

    implementation(platform("androidx.compose:compose-bom:2025.05.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.animation:animation")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // DataStore for preferences
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Color picker
    implementation("com.github.skydoves:colorpicker-compose:1.1.2")

    // Miuix - HyperOS design language
    implementation("top.yukonga.miuix.kmp:miuix-android:0.8.8")

    // Backdrop (local module — source from Kyant0/AndroidLiquidGlass 2.0.0-alpha03)
    implementation(project(":backdrop"))

    debugImplementation("androidx.compose.ui:ui-tooling")
}
