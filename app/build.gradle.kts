plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.nutrisense"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.nutrisense"
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        mlModelBinding = false
    }

    // --- ADDED THIS BLOCK ---
    // This ensures your crop_recommendation.tflite file is not compressed,
    // allowing your Kotlin code to read it directly from memory.
    androidResources {
        noCompress.add("tflite")
    }
    // ------------------------
}

dependencies {
    // Add the dependency for Cloud Firestore
    implementation("com.google.firebase:firebase-firestore")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // --- FIREBASE ADDITION ---
    // Import the Firebase BoM to automatically manage Firebase library versions
    implementation(platform("com.google.firebase:firebase-bom:34.10.0"))

    // Your Auth dependency is already perfect here
    implementation(libs.firebase.auth)

    // --- TENSORFLOW ADDITIONS ---
    // Core library required for the Interpreter to run the model
    implementation("org.tensorflow:tensorflow-lite:2.16.1")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.16.1")
    // ----------------------------

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}