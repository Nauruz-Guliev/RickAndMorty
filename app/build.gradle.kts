plugins {
    // почему-то студия ругается на libs, но при этом всё работает
    id(libs.plugins.android.application.get().pluginId).apply(true)
    id(libs.plugins.kotlin.android.get().pluginId).apply(true)
    id(libs.plugins.kotlin.kapt.get().pluginId).apply(true)
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
        signingConfig = signingConfigs.getByName("debug")
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
        targetCompatibility = compileJavaVersion
    }
    kotlinOptions {
        jvmTarget = rootProject.extra["JavaVersion"] as String
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
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


    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.okHttp)

    implementation(libs.mockk)

    testImplementation(libs.kotlin.coroutines.test)

    implementation(libs.bundles.dagger.impl)
    kapt(libs.bundles.dagger.kapt)

    implementation(libs.androidx.splash)

    implementation(project(":core:ui"))
    implementation(project(":core:data"))
    implementation(project(":common"))
    implementation(project(":feature:characters"))
    implementation(project(":feature:episodes"))
    implementation(project(":feature:locations"))

}
