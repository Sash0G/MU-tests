plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    alias(libs.plugins.google.gms.google.services)
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.example.mu_tests"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mu_tests"
        minSdk = 26
        targetSdk = 34
        versionCode = 22
        versionName = "v1.0.22"

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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.opencsv.opencsv)
    implementation (libs.gson)
    implementation (libs.androidx.recyclerview.v121)
    implementation(libs.firebase.config)
    implementation(libs.play.services.measurement.api)
    implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation (libs.firebase.auth)
    implementation (libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics)
    implementation(platform(libs.firebase.bom))
    implementation (libs.firebase.storage)
    implementation ("com.github.Dimezis:BlurView:version-2.0.6")
    implementation (libs.firebase.firestore.ktx.v24100)
    implementation (libs.play.services.base)
    implementation (libs.firebase.firestore)
    implementation("com.google.android.gms:play-services-base:18.2.0")
}
