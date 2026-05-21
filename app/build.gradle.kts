
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "cornhole.beanbag.thepeopleyoucantrust"
    compileSdk = 36

    defaultConfig {
        applicationId = "cornhole.beanbag.thepeopleyoucantrust"
        minSdk = 30
        targetSdk = 34
        versionCode = 14
        versionName = "1.3.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    kotlin {
        compilerOptions {
            optIn.add("kotlin.RequiresOptIn")
        }
    }
    buildFeatures {
        viewBinding = true
    }
    buildToolsVersion = "35.0.0 rc2"
}

kotlin {
    jvmToolchain(17) // Or 11, but 17 is the modern standard for new Android versions
}

dependencies {
    implementation("androidx.core:core-ktx:1.18.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")
    implementation("com.google.android.play:asset-delivery-ktx:2.3.0")
    implementation("com.google.android.gms:play-services-tasks:18.4.1")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.7")
    implementation ("com.squareup.retrofit2:retrofit:3.0.0")
    implementation ("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation("androidx.webkit:webkit:1.15.0")
    implementation ("org.jsoup:jsoup:1.22.1")
    implementation ("com.github.bumptech.glide:glide:5.0.5")
    implementation("com.google.android.gms:play-services-base:18.10.0")
    implementation("androidx.compose.animation:animation-graphics:1.10.6")
    implementation("androidx.core:core-animation:1.0.0")
    implementation("com.google.firebase:firebase-analytics:23.2.0")
    implementation("com.onesignal:OneSignal:5.7.7")
    annotationProcessor ("com.github.bumptech.glide:compiler:5.0.5")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}
