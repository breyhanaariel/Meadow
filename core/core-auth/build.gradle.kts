plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.meadow.core.auth"
    compileSdk = 35

    defaultConfig {
        minSdk = 31
    }

    buildFeatures {
        buildConfig = false
    }

    sourceSets {
        getByName("main") {
            java.srcDirs(
                "api",
                "di",
                "domain",
                "firebase",
                "util"
            )
        }
    }

}
dependencies {

    // ── APP ─────────────────────────────
    implementation(project(":core:core-common"))
    implementation(project(":core:core-google"))

    // ── COROUTINES ──────────────────────
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    // ── FIREBASE AUTH ───────────────────
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-auth-ktx")

    // ── HILT ────────────────────────────
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
