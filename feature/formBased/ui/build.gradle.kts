plugins {
    alias(libs.plugins.convention.android.library)
    alias(libs.plugins.convention.android.library.compose)
    alias(libs.plugins.convention.hilt)
}

android {
    namespace = "org.zayass.assessment.storage.feature.formBased"
}

dependencies {
    api(projects.core.storage.api)

    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.lifecycle)
    implementation(libs.hilt.navigation.compose)
}