plugins {
    alias(libs.plugins.android.application)
    id("androidx.navigation.safeargs")
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.optlab.banhangso"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.optlab.banhangso"
        minSdk = 30
        //noinspection ExpiredTargetSdkVersion
        targetSdk = 30
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        //noinspection DataBindingWithoutKapt
        dataBinding = true
        buildConfig = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    // Kotlin Symbol Processing
    ksp(libs.room.compiler)

    // Hilt dependencies
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Lombok dependencies
    compileOnly(libs.lombok)
    ksp(libs.lombok)

    // Base dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.core.ktx)
    implementation(libs.recyclerview.selection)
    implementation(libs.activity)


    // Glide dependency
    implementation(libs.glide)

    // Timber dependency for logging
    implementation(libs.timber)

    // Firebase dependencies for authentication and Firestore
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    // Google Play Services dependencies for authentication
    implementation(libs.play.services.auth)
    implementation(libs.com.google.gms.google.services.gradle.plugin)
    // Facebook SDK dependencies for login
    implementation(libs.facebook.login)

    // Credentials dependencies for Google Play Services
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)

    implementation(libs.googleid)
    // Gson for JSON serialization/deserialization
    implementation(libs.gson)

    // Lottile dependency for animations
    implementation(libs.lottie)

    // CircleImageView dependency for displaying circular images
    implementation(libs.circleimageview)

    // Junit dependencies for testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    // Espresso dependencies for UI testing
    androidTestImplementation(libs.espresso.core)
}
