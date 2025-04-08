plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.deepakmediaplayer"
    compileSdk = 35 // Update to 35

    defaultConfig {
        applicationId = "com.example.deepakmediaplayer"
        minSdk = 26
        targetSdk = 35 // Update to 35
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

    kotlinOptions {
        jvmTarget = "1.8" // Change this to "17" if you want to use Java 17
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.media)
    implementation(libs.media3.session)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("androidx.core:core:1.12.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.media:media:1.6.0")

    // Use the latest media3 dependencies
    implementation("androidx.media3:media3-exoplayer:1.5.1") // Update to the latest version
    implementation("androidx.media3:media3-ui:1.5.1") // Update to the latest version
    implementation("androidx.media3:media3-session:1.5.1") // Update to the latest version
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10") // Ensure this is compatible with your Kotlin version

    // Remove redundant or conflicting dependencies
    // implementation(libs.media.v170) // Commented out as it may be redundant
    // implementation(libs.androidx.media3.exoplayer) // Commented out as it may be redundant
    // implementation(libs.media3.ui) // Commented out as it may be redundant
    // implementation(libs.androidx.media3.common) // Commented out as it may be redundant
}