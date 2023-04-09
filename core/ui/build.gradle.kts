plugins {
    id(libs.plugins.android.library.get().pluginId).apply(true)
    id(libs.plugins.kotlin.android.get().pluginId).apply(true)
}

android {
    namespace = "ru.example.gnt.ui"
    compileSdk = 33

    defaultConfig {
        minSdk = 26
        targetSdk = 33
    }

    val compileJavaVersion = rootProject.extra["CompileJavaVersion"] as JavaVersion
    compileOptions {
        sourceCompatibility = compileJavaVersion
        sourceCompatibility = compileJavaVersion
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
}
