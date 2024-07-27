
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.zayass.buildlogic.configureKotlinJvm
import org.zayass.buildlogic.configureTestLogging

class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
            }
            configureKotlinJvm()
            configureTestLogging()
        }
    }
}
