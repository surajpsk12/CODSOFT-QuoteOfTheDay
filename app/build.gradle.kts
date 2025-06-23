plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.surajvanshsv.quoteapps"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.surajvanshsv.quoteapps"
        minSdk = 24
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
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
// Retrofit
    implementation (libs.retrofit)
    implementation (libs.converter.gson)

// Lifecycle (ViewModel + LiveData)
    implementation (libs.androidx.lifecycle.extensions)
    implementation (libs.gson)

// Room Database
    implementation (libs.androidx.room.runtime)
    annotationProcessor (libs.androidx.room.compiler)

// RecyclerView
    implementation (libs.androidx.recyclerview)

    implementation (libs.material)

    implementation (libs.androidx.work.runtime)


// Glide for Image Loading (Optional)
    implementation (libs.github.glide)
    annotationProcessor (libs.github.compiler)

    implementation(libs.appcompat)
    implementation (libs.androidx.core.splashscreen)


    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}