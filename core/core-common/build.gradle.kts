plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.meadow.core.common"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
        compose = true
    }


    defaultConfig {
        minSdk = 31
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
    sourceSets {
        getByName("main") {
            java.srcDirs(
                "src/main/java",
                "functional"
            )
        }
    }
}
dependencies {

    // ── APP ─────────────────────────────
    implementation(project(":core:core-utils"))

    // ── COROUTINES ──────────────────────
    implementation(libs.kotlinx.coroutines.core)

    // ── COMPOSE (RUNTIME ONLY) ──────────
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)

    // ── DATASTORE ───────────────────────
    implementation(libs.androidx.datastore.preferences)

    // ── INJECTION ───────────────────────
    implementation("javax.inject:javax.inject:1")
}
