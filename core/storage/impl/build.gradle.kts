plugins {
    alias(libs.plugins.convention.jvm.library)
}

dependencies {
    api(projects.core.storage.api)

    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.bundles.test)
}
