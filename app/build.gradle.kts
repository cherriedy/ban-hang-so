plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("androidx.navigation.safeargs")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.google.gms.google.services)
    kotlin("plugin.serialization") version "1.9.0"
    id("com.google.protobuf") version "0.9.4"
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.optlab.banhangso"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.optlab.banhangso"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0-alpha1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("debug")
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

    kotlinOptions { jvmTarget = "17" }

    buildToolsVersion = "35.0.0"
}

protobuf {
    protoc { artifact = "com.google.protobuf:protoc:3.21.7" }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") { option("lite") }
                create("kotlin") { option("lite") }
            }
        }
    }
}

configurations {
    all {
        // Force using a specific version of protobuf
        resolutionStrategy {
            force("com.google.protobuf:protobuf-javalite:3.21.7")
            // Exclude specific incompatible versions
            exclude(group = "com.google.protobuf", module = "protobuf-java")
        }
    }
}

dependencies {
    // AndroidX & Jetpack
    implementation(libs.appcompat)
    implementation(libs.core.ktx)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.activity)
    implementation(libs.recyclerview.selection)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx.v291)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.firebase.crashlytics)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // ReactiveStreams bridge for LiveData
    implementation(libs.androidx.lifecycle.reactivestreams)

    // Data Binding
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.rxjava3)

    // DataStore with RxJava3 support
    implementation(libs.androidx.datastore.rxjava3)

    // AutoDispose for RxJava lifecycle handling
    implementation(libs.autodispose.core)
    implementation(libs.autodispose.android)
    implementation(libs.autodispose.lifecycle)
    implementation(libs.autodispose.androidx.lifecycle)

    // RxJava3 (RxAndroid)
    implementation(libs.rxandroid)
    implementation(libs.rxjava)

    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.fragment)
    ksp(libs.hilt.android.compiler)

    // Firebase & Google
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    implementation(libs.com.google.gms.google.services.gradle.plugin)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)

    // Utils & UI
    implementation(libs.glide)
    implementation(libs.timber)
    implementation(libs.gson)
    implementation(libs.lottie)
    implementation(libs.circleimageview)
    implementation(libs.shimmer)
//    implementation(libs.androidchart)
    implementation(libs.mpandroidchart)

    // QR Code Scanner
    implementation(libs.zxing.android.embedded)
    implementation(libs.core)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Retrofit with RxJava
    implementation(libs.retrofit)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.retrofit2.adapter.rxjava3)
    implementation(libs.okhttp3.logging.interceptor)
    implementation(libs.okhttp3.integration)

    // DataStore
    implementation("androidx.datastore:datastore:1.0.0") {
        exclude(group = "com.google.protobuf", module = "protobuf-java")
    }
    implementation("androidx.datastore:datastore-core:1.0.0") {
        exclude(group = "com.google.protobuf", module = "protobuf-java")
    }

    implementation(libs.protobuf.javalite)
    implementation(libs.protobuf.kotlin.lite)
}
