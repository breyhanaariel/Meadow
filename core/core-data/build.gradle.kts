plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)

}


android {
    namespace = "com.meadow.core.data"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
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
    sourceSets {
        getByName("main") {
            java.srcDirs(
                "src/main/java",
                "datastore",
                "di",
                "fields",
                "firebase",
                "repository",
                "sync"
            )
        }
    }
}

dependencies {

    // ── APP ─────────────────────────────
    implementation(project(":core:core-common"))
    implementation(project(":core:core-domain"))
    implementation(project(":core:core-utils"))

    // ── ROOM (LOCAL DATABASE) ───────────
    api(libs.androidx.room.runtime)
    api(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // ── FIREBASE / CLOUD ────────────────
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.firestore)
    implementation(libs.google.firebase.storage)

    // ── DATASTORE / SERIALIZATION ───────
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.gson.core)
    implementation(libs.kotlinx.serialization.json)

    // ── WORK / DI ───────────────────────
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.work)
    ksp(libs.hilt.compiler)
}
