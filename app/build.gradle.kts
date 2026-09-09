plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hiltAndroid)
    id("kotlin-kapt")
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "uz.kmax.fizikatest"
    compileSdk = 36

    defaultConfig {
        applicationId = "uz.kmax.fizikatest"
        minSdk = 24
        targetSdk = 36
        versionCode = 7
        versionName = "1.8.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    buildFeatures {
        viewBinding = true
    }

    hilt {
        enableAggregatingTask = false
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.crashlytics)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.baselibrary)
    implementation(libs.play.services.ads)
    implementation(libs.review.ktx)
    implementation(libs.konfetti.xml)
    implementation(libs.konfetti)
    implementation (libs.lottie)
    implementation(libs.shimmer)
    implementation(libs.app.update.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // coroutine
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    
    implementation(libs.pdf.viewer)
    implementation(libs.yandex.mobileads)

    implementation("androidx.work:work-runtime-ktx:2.9.1")
    implementation("androidx.lifecycle:lifecycle-process:2.8.7")
    implementation("com.google.firebase:firebase-messaging-ktx:24.1.0")

    // Math & Physics formula rendering (LaTeX)
    implementation("io.noties.markwon:core:4.6.2")
    implementation("io.noties.markwon:ext-latex:4.6.2")
    implementation("io.noties.markwon:inline-parser:4.6.2")
    implementation("ru.noties:jlatexmath-android:0.2.0")
    implementation("ru.noties:jlatexmath-android-font-cyrillic:0.2.0")
    implementation("ru.noties:jlatexmath-android-font-greek:0.2.0")

    // dagger
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.android)
}