plugins {
    alias(libs.plugins.convention.android.library)
    alias(libs.plugins.convention.android.library.compose)
    alias(libs.plugins.convention.hilt)
}

android {
    namespace = "org.zayass.assessment.storage.core.designsystem"
}

dependencies {
    implementation(libs.androidx.compose.material3)
}
