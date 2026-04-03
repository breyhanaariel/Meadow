plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}
android {
    namespace = "com.meadow.feature.mindmap"
    compileSdk = 35
    buildToolsVersion = "35.0.0"

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
                "data",
                "di",
                "domain"
            )
        }
    }
}

dependencies {
    implementation(project(":core:core-common"))
    implementation(project(":core:core-utils"))
    implementation(libs.kotlinx.coroutines.core)
}