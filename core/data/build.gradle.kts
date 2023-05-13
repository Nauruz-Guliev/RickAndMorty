plugins {
    id(libs.plugins.android.library.get().pluginId).apply(true)
    id(libs.plugins.kotlin.android.get().pluginId).apply(true)
    id(libs.plugins.kotlin.kapt.get().pluginId).apply(true)

}

android {
    namespace = "ru.example.gnt.data"
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

    //libraries
    implementation(libs.bundles.retrofit)

    implementation(libs.gson)

    implementation(libs.bundles.dagger.impl)
    kapt(libs.bundles.dagger.kapt)

    //room
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    implementation(libs.room.paging)
    implementation(libs.room.rxjava)
    kapt(libs.room.compiler)
    annotationProcessor(libs.room.compiler)

    implementation(libs.androidx.splash)


    //paging
    implementation(libs.room.paging)

    implementation(libs.bundles.okHttp)


    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.espresso)

    implementation(project(":common"))
}
