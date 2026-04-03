plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.meadow.core.export"
    compileSdk = 35

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

    //  Build consistency and Windows stability
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

configurations.all {
    exclude(group = "xmlpull", module = "xmlpull")
    exclude(group = "net.sf.kxml", module = "kxml2")
}

dependencies {

    // ── APP ─────────────────────────────
    implementation(project(":core:core-data"))
    implementation(project(":core:core-utils"))

    // ── COROUTINES ──────────────────────
    implementation(libs.kotlinx.coroutines.android)

    // ── MEDIA ───────────────────────────
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.coil.compose)
}
