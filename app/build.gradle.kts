plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.safeArgs)
}

android {
    namespace = "com.jnasser.pokeapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jnasser.pokeapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        dataBinding = true
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

    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)

    implementation(libs.retrofit)
    implementation(libs.gson.converter)
    implementation(libs.okHttp)
    implementation(libs.okHttp.interceptor)

    implementation(libs.hilt)
    implementation(libs.hilt.compiler)
    implementation(libs.hilt.worker)
    implementation(libs.hilt.annotation)

    implementation(libs.viewmodel)
    implementation(libs.livedata)
    implementation(libs.lifecycle)

    implementation(libs.room)
    implementation(libs.room.runtime)

    implementation(libs.coroutines)

    implementation(libs.navigation)
    implementation(libs.navigation.ui)

    implementation(libs.worrManager)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}