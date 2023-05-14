plugins {
    id(libs.plugins.android.library.get().pluginId).apply(true)
    id(libs.plugins.kotlin.android.get().pluginId).apply(true)
    id(libs.plugins.kotlin.kapt.get().pluginId).apply(true)

}

android {
    namespace = "ru.example.gnt.common"
    compileSdk = 33
    defaultConfig {
        minSdk = 26
        targetSdk = 33
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
    implementation(libs.moshi)
    implementation(libs.androidx.paging)

    implementation(libs.bundles.retrofit)

    implementation(libs.androidx.test.core)


    implementation(libs.bundles.dagger.impl)
    kapt(libs.bundles.dagger.kapt)


    implementation(libs.androidx.splash)


    testImplementation(libs.kotlin.coroutines.test)


    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)



    //tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.espresso)



    implementation(libs.bundles.dagger.impl)
    kapt(libs.bundles.dagger.kapt)

    implementation(project(":core:ui"))
}
