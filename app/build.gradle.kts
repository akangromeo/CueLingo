plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs")

}

android {
    namespace = "com.example.cuelingo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cuelingo"
        minSdk = 24
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
        viewBinding = true
        mlModelBinding = true
    }
}



dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")

    implementation("org.tensorflow:tensorflow-lite-metadata:0.1.0")
//    implementation("org.tensorflow:tensorflow-lite-support:0.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")

    implementation ("androidx.localbroadcastmanager:localbroadcastmanager:1.0.0")

    // CameraX core library
    val camerax_version = "1.1.0-beta03"
    implementation ("androidx.camera:camera-core:$camerax_version")

    // CameraX Camera2 extensions
    implementation ("androidx.camera:camera-camera2:$camerax_version")

    // CameraX Lifecycle library
    implementation ("androidx.camera:camera-lifecycle:$camerax_version")

    // CameraX View class
    implementation ("androidx.camera:camera-view:$camerax_version")

    //WindowManager
    implementation ("androidx.window:window:1.0.0-alpha09")

    implementation ("org.tensorflow:tensorflow-lite-task-vision:0.4.0")
    // Import the GPU delegate plugin Library for GPU inference
    implementation ("org.tensorflow:tensorflow-lite-gpu-delegate-plugin:0.4.0")
    implementation ("org.tensorflow:tensorflow-lite-gpu:2.9.0")

    //livedata
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.activity:activity-ktx:1.7.2")

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1") //untuk lifecycleScope
    //datastore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation ("com.google.mediapipe:tasks-vision:0.10.2")

    //glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
}