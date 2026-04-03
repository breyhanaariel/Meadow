plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}


android {
    namespace = "com.meadow.core.ui"
    compileSdk = 35

    defaultConfig {
        minSdk = 31
        consumerProguardFiles("proguard-rules.pro")
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeBom.get()
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
}

dependencies {

    // ── APP ─────────────────────────────
    implementation(project(":core:core-common"))
    implementation(project(":core:core-media"))
    implementation(project(":core:core-utils"))
    implementation(project(":core:core-data"))

    // ── COMPOSE ─────────────────────────
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.browser)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // ── ACTIVITY ────────────────────────
    implementation(libs.androidx.activity.compose)

    // ── MATERIAL ────────────────────────
    api(libs.google.material)
    implementation(libs.material3.window.size)

    // ── LIFECYCLE ───────────────────────
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // ── MEDIA ───────────────────────────
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.coil.compose)

    // ── COROUTINES ──────────────────────
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // ── DATASTORE ───────────────────────
    implementation(libs.androidx.datastore.preferences)
}
