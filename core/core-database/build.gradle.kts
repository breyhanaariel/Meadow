plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}


android {
    namespace = "com.meadow.core.database"
    compileSdk = 35

    defaultConfig {
        minSdk = 31
        consumerProguardFiles("consumer-rules.pro")
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
                "converters",
                "di",
                "history",
                "repository",
                "migrations"
            )
        }
    }
}
dependencies {

    // ── APP ─────────────────────────────
    implementation(project(":core:core-common"))
    implementation(project(":core:core-domain"))
    implementation(project(":core:core-utils"))

    // ── ROOM ────────────────────────────
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // ── COROUTINES ──────────────────────
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // ── ANDROIDX ────────────────────────
    implementation(libs.androidx.core.ktx)

    // ── SERIALIZATION ───────────────────
    implementation(libs.gson.core)

    // ── HILT ────────────────────────────
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // ── TESTING ─────────────────────────
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.room.testing)
}
