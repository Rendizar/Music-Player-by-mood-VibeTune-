// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.services) apply false
    
    // Gunakan alias KSP dari libs.versions.toml
    alias(libs.plugins.ksp) apply false
}
