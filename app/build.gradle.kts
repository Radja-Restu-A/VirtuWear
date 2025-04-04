plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id ("com.google.devtools.ksp")
    id ("com.google.dagger.hilt.android")
    id ("com.google.gms.google-services")
}

android {
    namespace = "com.example.virtuwear"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.virtuwear"
        minSdk = 28
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation (libs.androidx.activity.ktx)
    implementation (libs.androidx.core.ktx.v1101)
    implementation (libs.androidx.lifecycle.runtime.ktx.v261)
    implementation (libs.androidx.activity.compose.v180)
    implementation(libs.androidx.monitor)
    implementation(libs.androidx.junit.ktx)
    testImplementation(libs.junit)

    // Jetpack Compose & Material3
    implementation (libs.ui)
    implementation (libs.material3)
    implementation (libs.ui.tooling.preview)
    implementation (libs.androidx.runtime.livedata)
    androidTestImplementation(libs.junit.junit)
    debugImplementation (libs.ui.tooling)

    // Navigation Compose
    implementation (libs.androidx.navigation.compose)

    // Room Database
    implementation (libs.androidx.room.runtime)
    ksp (libs.androidx.room.compiler)
    implementation (libs.androidx.room.ktx)

    // Retrofit and Gson converter
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.logging.interceptor)

    //paging
    implementation (libs.androidx.paging.compose)
    implementation (libs.androidx.paging.runtime.ktx)

    //hilt DI
    implementation(libs.hilt.android.v250)
    ksp(libs.hilt.android.compiler.v250)
    implementation(libs.androidx.hilt.navigation.compose)

    //onboarding
    implementation (libs.androidx.datastore.preferences)

    //coil
    implementation(libs.coil) // Untuk View
    implementation(libs.coil.compose) // Untuk Jetpack Compose
    implementation(libs.coil.network.okhttp) //hande api image url


    ///Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.ui.auth)

    //google
    implementation(libs.play.services.auth)
    implementation(libs.accompanist.drawablepainter)


    //facebook
    implementation (libs.facebook.android.sdk)

}