plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.benessere.personalphysicaltrackerapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.benessere.personalphysicaltrackerapp"
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
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.core.ktx)
    implementation(libs.room.rxjava2)
    implementation(libs.play.services.location)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("androidx.work:work-runtime:2.9.0")

}