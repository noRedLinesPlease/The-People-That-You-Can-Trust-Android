
plugins {
    id("com.android.application")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
}

android {
    namespace = "cornhole.beanbag.thepeopleyoucantrust"
    compileSdk = 37

    defaultConfig {
        applicationId = "cornhole.beanbag.thepeopleyoucantrust"
        minSdk = 30
        targetSdk = 35
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
        compose = true
    }
    buildToolsVersion = "35.0.0"
}

kotlin {
    jvmToolchain(17) // Or 11, but 17 is the modern standard for new Android versions
}

dependencies {
    implementation("androidx.compose.ui:ui-test-junit4:1.11.3")
    val composeBom = platform("androidx.compose:compose-bom:2026.05.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("io.coil-kt.coil3:coil-compose:3.5.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.5.0")
    implementation("androidx.core:core-ktx:1.19.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.14.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")
    implementation("com.google.android.play:asset-delivery-ktx:2.3.0")
    implementation("com.google.android.gms:play-services-tasks:18.4.1")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.11.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.8")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.8")
    implementation("androidx.navigation:navigation-compose:2.9.8")
    implementation ("com.squareup.retrofit2:retrofit:3.0.0")
    implementation ("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation("androidx.webkit:webkit:1.16.0")
    implementation ("org.jsoup:jsoup:1.22.2")
    implementation ("com.github.bumptech.glide:glide:5.0.7")
    implementation("com.google.android.gms:play-services-base:18.10.0")
    implementation("androidx.compose.animation:animation-graphics")
    implementation("androidx.core:core-animation:1.0.0")
    implementation("com.google.firebase:firebase-analytics:23.2.0")
    implementation("com.onesignal:OneSignal:5.9.5")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    annotationProcessor ("com.github.bumptech.glide:compiler:5.0.7")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation(composeBom)
    testImplementation(kotlin("test"))
}
