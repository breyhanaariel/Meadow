plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}


android {
    namespace = "com.meadow.core.network"
    compileSdk = 35

    defaultConfig {
        minSdk = 31
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }
    sourceSets {
        getByName("main") {
            java.srcDirs(
                "src/main/java",
                "api",
                "config",
                "di",
                "interceptors",
                "models",
                "utils"
            )
        }
    }
}
dependencies {

    // ── APP ─────────────────────────────
    implementation(project(":core:core-common"))
    implementation(project(":core:core-utils"))

    // ── NETWORKING ──────────────────────
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)

    // ── SERIALIZATION ───────────────────
    implementation(libs.gson.core)

    // ── COROUTINES ──────────────────────
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // ── LIFECYCLE ───────────────────────
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // ── HILT ────────────────────────────
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
