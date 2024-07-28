plugins {
    alias(libs.plugins.convention.android.application)
    alias(libs.plugins.convention.android.application.compose)
    alias(libs.plugins.convention.hilt)
}

android {
    namespace = "org.zayass.assessment.storage"

    defaultConfig {
        applicationId = "org.zayass.assessment.storage"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

dependencies {
    implementation(projects.core.storage.impl)
    implementation(projects.feature.formBased.ui)

    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.lifecycle)
    implementation(libs.androidx.compose.material3)
    implementation(libs.hilt.navigation.compose)
}
