// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Update the Android Gradle Plugin to a version that supports API level 35
        classpath("com.android.tools.build:gradle:8.5.0") // Use the latest stable version
    }
}

// Ensure that you have the Gradle wrapper updated to a compatible version
// In gradle/wrapper/gradle-wrapper.properties
// distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip