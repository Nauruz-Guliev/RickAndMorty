plugins {
    // почему-то студия ругается на libs, но при этом всё работает
    id(libs.plugins.android.application.get().pluginId).apply(true)
    id(libs.plugins.kotlin.android.get().pluginId).apply(true)
}

android {
    namespace = "ru.example.gnt.rickandmorty"
    compileSdk = 33

    defaultConfig {
        applicationId = "ru.example.gnt.rickandmorty"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    val compileJavaVersion = rootProject.extra["CompileJavaVersion"] as JavaVersion

    compileOptions {
        sourceCompatibility = compileJavaVersion
        sourceCompatibility = compileJavaVersion
    }
    kotlinOptions {
        jvmTarget = rootProject.extra["JavaVersion"] as String
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    implementation(libs.androidx.constraint)

    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.espresso)
}
