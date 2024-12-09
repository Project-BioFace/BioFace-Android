plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.bangkit.bioface"
    compileSdk = 35


    viewBinding {
        enable = true
    }

    defaultConfig {
        applicationId = "com.bangkit.bioface"
        minSdk = 23
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures{
        dataBinding = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)
    implementation(libs.vision.common)
    implementation(libs.play.services.mlkit.face.detection)
    implementation(libs.androidx.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore:24.9.0")
    implementation ("com.google.android.gms:play-services-auth:20.0.1")

    implementation("com.github.1902shubh:SendMail:1.0.0")

    implementation ("np.com.susanthapa:curved_bottom_navigation:0.6.5")

    implementation("com.github.Foysalofficial:NafisBottomNav:5.0")

    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation ("com.github.bumptech.glide:glide:4.15.0")
    implementation ("androidx.activity:activity-ktx:1.7.2")
    implementation ("com.github.yalantis:ucrop:2.2.8")
    implementation ("com.facebook.shimmer:shimmer:0.5.0")

    // CameraX dependencies
    implementation ("androidx.camera:camera-core:1.4.0")
    implementation ("androidx.camera:camera-camera2:1.4.0") // Menambahkan kamera2 sebagai implementasi default
    implementation ("androidx.camera:camera-lifecycle:1.4.0")  // Untuk lifecycle camera
    implementation ("androidx.camera:camera-view:1.4.0")  // Untuk menggunakan camera view


    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    implementation ("androidx.fragment:fragment-ktx:1.5.5")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

}