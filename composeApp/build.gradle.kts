plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

group = "br.com.mykytadu"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(21)

    androidTarget()

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")

    // Apply Kotlin Multiplatform default source set hierarchy so iosMain is created properly
    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.jetbrains.compose.runtime)
                implementation(libs.jetbrains.compose.foundation)
                implementation(libs.jetbrains.compose.material3)
                implementation(libs.jetbrains.compose.ui)
                implementation(libs.jetbrains.compose.resources)
                implementation(libs.koin.core)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.jetbrains.navigation3.ui)
                implementation(libs.androidx.navigation3.runtime)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.appcompat)
                implementation(libs.jetbrains.compose.ui.tooling.preview)
                implementation(libs.jetbrains.compose.ui.tooling)
                //koin
                implementation(libs.koin.android)
                // Ktor Android engine
                implementation(libs.ktor.client.okhttp)
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }


        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                // Ktor Desktop engine
                implementation(libs.ktor.client.cio)
            }
        }


        val iosMain by getting {
            dependencies {
                // Ktor iOS engine
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}

android {
    namespace = "br.com.mykytadu"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        applicationId = "br.com.mykytadu"
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}
