plugins {
    id(libs.plugins.android.library.get().pluginId).apply(true)
    id(libs.plugins.kotlin.android.get().pluginId).apply(true)

    id(libs.plugins.kotlin.kapt.get().pluginId).apply(true)
}

android {
    namespace = "ru.example.gnt.episodes"
    compileSdk = 33

    defaultConfig {
        minSdk = 26
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    kaptAndroidTest(libs.androidx.databinding.compiler)


    //libraries
    implementation(libs.bundles.retrofit)

    implementation(libs.bundles.dagger.impl)
    kapt(libs.bundles.dagger.kapt)

    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.androidx.lifecycle.livedata)
    implementation(libs.glide)
    implementation(libs.bundles.android.java.rx)

    implementation(libs.androidx.splash)

    testImplementation(libs.mockk)


    implementation(libs.androidx.paging)
    implementation(libs.androidx.swiperefresh)

    implementation(libs.bundles.okHttp)
    implementation(libs.gson)

    //tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.espresso)

    implementation(project(":core:ui"))
    implementation(project(":core:data"))
    implementation(project(":common"))


}
