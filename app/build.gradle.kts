import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.safeArgs)
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
    javacOptions {
        // Increase the max count of errors from annotation processors.
        // Default is 100.
        option("-Xmaxerrs", 500)
    }
}

ksp {
    arg("maxerrs", "500")
}

fun loadLocalProperties(file: File): Properties {
    val properties = Properties()
    if(file.exists()) {
        properties.load(file.inputStream())
    }
    return properties
}

val localPropertiesFile = rootProject.file("local.properties")
val localProperties = loadLocalProperties(localPropertiesFile)

private fun getPropertyValue(key: String) = localProperties.getProperty(key)

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
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            val baseUrl = getPropertyValue("BASE_URL")
            val imgBaseURL = getPropertyValue("IMG_BASE_URL")
            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
            buildConfigField("String", "IMG_BASE_URL", "\"$imgBaseURL\"")
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            val baseUrl = getPropertyValue("BASE_URL")
            val imgBaseURL = getPropertyValue("IMG_BASE_URL")
            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
            buildConfigField("String", "IMG_BASE_URL", "\"$imgBaseURL\"")
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

    // Hilt
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    // Worker
    implementation(libs.hilt.worker)
    implementation(libs.androidx.hilt.common)
    implementation(libs.worrManager)

    implementation(libs.viewmodel)
    implementation(libs.livedata)
    implementation(libs.lifecycle)

    ksp(libs.room)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)

    implementation(libs.coroutines)

    implementation(libs.navigation)
    implementation(libs.navigation.ui)

    implementation(libs.glide)
    implementation(libs.glide.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}