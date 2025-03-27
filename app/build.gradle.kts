plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.quizzify"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.quizzify"
        minSdk = 26
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.animation.graphics.android)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation("com.google.dagger:dagger-android:2.20")
    implementation("com.github.sephiroth74:NumberSlidingPicker:v1.0.3")
    implementation("com.squareup.inject:assisted-inject-annotations:0.6.0")
    implementation("com.airbnb.android:lottie:3.4.0")
    implementation("com.mikhaellopez:circularprogressbar:3.1.0")
    implementation("com.github.cachapa:ExpandableLayout:2.9.2")
    implementation("com.github.skydoves:powermenu:2.2.4")
    //implementation ("com.google.dagger:dagger-android-support:2.20")// if you use the support libraries
    kapt ("com.google.dagger:dagger-android-processor:2.20")
    kapt ("com.google.dagger:dagger-compiler:2.20")
    kapt ("com.squareup.inject:assisted-inject-processor:0.6.0")
    implementation("app.futured.donut:donut:2.2.4")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("nl.dionsegijn:konfetti-xml:2.0.5")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation ("com.google.android.gms:play-services-auth:21.0.0")


    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")


}