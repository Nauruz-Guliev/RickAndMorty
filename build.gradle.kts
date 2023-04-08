// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.kotlin.dsl).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)

    alias(libs.plugins.toml.updater.github).apply(true)
    alias(libs.plugins.toml.updater.version.catalog).apply(true)

}
buildscript {
    extra.apply {
        set("JavaVersion", "1.8")
        set("CompileJavaVersion", JavaVersion.VERSION_1_8)
    }
}

