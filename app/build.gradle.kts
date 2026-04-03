import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

val jsonAssetsPath = localProps.getProperty(
    "json.assets.path",
    "app/src/main/assets/"
)

val geminiKey = localProps
    .getProperty("GEMINI_API_KEY")
    ?.trim()
    .orEmpty()

require(geminiKey.isNotBlank()) {
    "GEMINI_API_KEY missing or blank in local.properties"
}

val googleWebClientId = localProps
    .getProperty("GOOGLE_WEB_CLIENT_ID")
    ?.trim()
    .orEmpty()

val googleClientSecret = localProps
    .getProperty("GOOGLE_CLIENT_SECRET")
    ?.trim()
    .orEmpty()

android {
    namespace = "com.meadow.app"
    compileSdk = 35

    buildFeatures {
        compose = true
        buildConfig = true
    }

    sourceSets {
        getByName("main") {
            java.srcDirs(
                "src/main/java",
                "di"
            )
        }
    }

    defaultConfig {
        applicationId = "com.meadow.app"
        minSdk = 31
        targetSdk = 35
        versionCode = 2
        versionName = "2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true

        buildConfigField(
            "String",
            "GOOGLE_WEB_CLIENT_ID",
            "\"$googleWebClientId\""
        )

        buildConfigField(
            "String",
            "GOOGLE_CLIENT_SECRET",
            "\"$googleClientSecret\""
        )

        buildConfigField(
            "String",
            "JSON_ASSETS_PATH",
            "\"$jsonAssetsPath\""
        )

        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"$geminiKey\""
        )
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
        freeCompilerArgs += listOf(
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.FlowPreview"
        )
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }

    sourceSets {
        getByName("main") {
            assets.srcDirs(
                file("src/main/assets"),
                project(":feature:project").file("src/main/assets")
            )
        }
    }
}

dependencies {

    // ── APP ─────────────────────────────
    implementation(project(":core:core-ai"))
    implementation(project(":core:core-auth"))
    implementation(project(":core:core-common"))
    implementation(project(":core:core-data"))
    implementation(project(":core:core-database"))
    implementation(project(":core:core-domain"))
    implementation(project(":core:core-export"))
    implementation(project(":core:core-google"))
    implementation(project(":core:core-media"))
    implementation(project(":core:core-network"))
    implementation(project(":core:core-node"))
    implementation(project(":core:core-sync"))
    implementation(project(":core:core-ui"))
    implementation(project(":core:core-utils"))
    implementation(project(":feature:common"))
    implementation(project(":feature:project"))
    implementation(project(":feature:catalog"))
    implementation(project(":feature:calendar"))
    implementation(project(":feature:script"))
    implementation(project(":feature:timeline"))
    implementation(project(":feature:wiki"))
    implementation(project(":feature:familytree"))

    // ── Compose BOM ─────────────────────
    implementation(platform(libs.androidx.compose.bom))

    // ── COMPOSE ─────────────────────────
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // ── ACTIVITY / NAVIGATION ───────────
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)

    // ── LIFECYCLE / COROUTINES ──────────
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // ── SPLASHSCREEN ────────────────────
    implementation(libs.androidx.core.splashscreen)

    // ── HILT / WORK ─────────────────────
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.work.runtime.ktx)

    // ── SERIALIZATION ───────────────────
    implementation(libs.kotlinx.serialization.json)

    // ── DATASTORE ───────────────────────
    implementation(libs.androidx.datastore.preferences)

    // ── MEDIA / TEXT ────────────────────
    implementation(libs.coil.compose)
    implementation(libs.commonmark)
    implementation("com.tom-roush:pdfbox-android:2.0.27.0")

    // ── LOGGING ─────────────────────────
    implementation("com.jakewharton.timber:timber:5.0.1")
}
