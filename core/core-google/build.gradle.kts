plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.meadow.core.google"
    compileSdk = 35

    defaultConfig {
        minSdk = 31

        buildConfigField(
            "String",
            "GOOGLE_WEB_CLIENT_ID",
            "\"${project.findProperty("GOOGLE_WEB_CLIENT_ID")}\""
        )

        buildConfigField(
            "String",
            "GOOGLE_CLIENT_SECRET",
            "\"${project.findProperty("GOOGLE_CLIENT_SECRET")}\""
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }


    composeOptions {
        kotlinCompilerExtensionVersion =
            libs.versions.composeCompiler.get()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }

    sourceSets["main"].java.srcDirs(
        "src/main/java",
        "api",
        "auth",
        "di",
        "engine",
        "repository"
    )
}

dependencies {

    // ── APP ─────────────────────────────
    implementation(project(":core:core-common"))
    implementation(project(":core:core-network"))
    implementation(project(":core:core-ui"))
    implementation(project(":core:core-utils"))

    // ── COMPOSE ─────────────────────────
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // ── COROUTINES ──────────────────────
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    // ── NETWORKING / GOOGLE ─────────────
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.google.play.services.auth)

    // ── SERIALIZATION ───────────────────
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.annotation)

    // ── HILT ────────────────────────────
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
