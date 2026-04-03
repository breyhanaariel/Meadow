plugins {    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}
android {
    namespace = "com.meadow.core.sync"
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
                "src/main/kotlin",
                "api",
                "di",
                "engine",
                "local",
                "remote"
            )
        }
    }
}
dependencies {

    // ── APP ─────────────────────────────
    implementation(project(":core:core-database"))
    implementation(project(":core:core-network"))

    // ── WORK / FIREBASE ─────────────────
    implementation(libs.androidx.work.runtime.ktx)
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.firestore)

    // ── COROUTINES ──────────────────────
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    // ── SERIALIZATION ───────────────────
    implementation(libs.gson.core)

    // ── HILT ────────────────────────────
    implementation(libs.hilt.android)
}
