plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}
android {
    namespace = "com.meadow.core.utils"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
    }

    sourceSets {
        getByName("main") {
            java.srcDirs(
                "src/main/java",
                "files",
                "compare",
                "coroutines",
                "datetime",
                "extensions",
                "logging",
                "strings"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {

    // ── SERIALIZATION ───────────────────
    implementation(libs.kotlinx.serialization.json)

    // ── COROUTINES ──────────────────────
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
}
