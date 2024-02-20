plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
//    id("org.jetbrains.kotlin.kotlin-dsl")
    kotlin("kapt")
}

android {
    namespace = "com.codelab.advancedcoroutine"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.codelab.advancedcoroutine"
        minSdk = 24
        targetSdk = 33
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        dataBinding = true
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//    implementation(project(":sunflower"))

    // local variables (use val)
    val androidx_test_version = "1.4.0"
    val annotations_version = "1.3.0"
    val appcompat_version = "1.4.1"
    val arch_version = "2.1.0"
    val constraint_layout_version = "2.1.3"
    val coroutines_android_version = "1.6.0"
    val espresso_version = "3.4.0"
    val glide_version = "4.13.0"
    val gson_version = "2.8.8"
    val junit_version = "4.13.2"
    val lifecycle_version = "2.4.1"
    val material_version = "1.5.0"
    val retrofit_version = "2.9.0"
    val room_version = "2.4.1"
    val truth_version = "1.0"
    val work_version = "2.7.0"

    val libraries = arrayOf(
        // Coroutines
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_android_version",
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_android_version",

        // Android UI and appcompat
        "androidx.fragment:fragment-ktx:1.1.0",

        // Glide
        "com.github.bumptech.glide:glide:$glide_version",

        // ViewModel and LiveData
        "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version",
        "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version",

        // network & serialization
        "com.google.code.gson:gson:$gson_version",
        "com.squareup.retrofit2:converter-gson:$retrofit_version",
        "com.squareup.retrofit2:retrofit:$retrofit_version",

        // threading
        "androidx.annotation:annotation:$annotations_version"
    )

    val arch_libraries = arrayOf(
        "androidx.work:work-runtime-ktx:$work_version",

        // Room for database
        "androidx.room:room-ktx:$room_version"
    )

    val librariesKapt = arrayOf(
        "androidx.room:room-compiler:$room_version",
        "com.github.bumptech.glide:compiler:$glide_version"
    )

    val testLibraries = arrayOf(
        "junit:junit:$junit_version",
        // Coroutines testing
        "org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_android_version",

        // mocks
        "org.mockito:mockito-core:2.23.0",

        //  Architecture Components testing libraries
        "androidx.arch.core:core-testing:$arch_version",

        "com.google.truth:truth:$truth_version",
    )

    val androidTestLibraries = arrayOf(
        "junit:junit:$junit_version",
        "androidx.test:runner:$androidx_test_version",
        "androidx.test:rules:$androidx_test_version",

        // Espresso
        "androidx.test.espresso:espresso-core:$espresso_version",
        "androidx.test.espresso:espresso-contrib:$espresso_version",
        "androidx.test.espresso:espresso-intents:$espresso_version",

        //  Architecture Components testing libraries
        "androidx.arch.core:core-testing:$arch_version",
        "androidx.work:work-testing:$work_version",
    )
    (libraries + arch_libraries + librariesKapt + testLibraries + androidTestLibraries).forEach {
        implementation(it)
    }
}
