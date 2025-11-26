plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Firebase / Google Services
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.perf)
}

android {
    namespace = "com.example.uth_hub"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.uth_hub"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // Bật App Check debug nếu cần test (sẽ dùng lib appcheck-debug)
            // buildConfigField("boolean", "APP_CHECK_DEBUG", "true")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("com.google.firebase:firebase-messaging-ktx")


    implementation(libs.font.awesome)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.foundation.layout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // === Firebase qua BoM (chỉ 1 dòng platform) ===
    implementation(platform(libs.firebase.bom))

    // GoogleSignIn / GoogleSignInOptions
    implementation(libs.play.services.auth)

    // Core & Analytics
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.installations)

    // Auth
    implementation(libs.firebase.auth)

    // Database
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)

    // Cloud Messaging (FCM)
    implementation(libs.firebase.messaging)

    // Remote Config
    implementation(libs.firebase.config)

    // App Check (Play Integrity + Debug build)
    implementation(libs.firebase.appcheck)
    implementation(libs.firebase.appcheck.playintegrity)
    debugImplementation(libs.firebase.appcheck.debug)

    // Cloud Functions (client)
    implementation(libs.firebase.functions)

    // In-App Messaging
    implementation(libs.firebase.inappmessaging)
    implementation(libs.firebase.inappmessaging.display)

    // Model Downloader (ML)
    implementation(libs.firebase.ml.modeldownloader)

    // Crashlytics & Performance
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.perf)

    // Coil
    implementation(libs.coil.compose)

}
