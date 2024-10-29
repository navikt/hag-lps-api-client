rootProject.name = "hag-lps-api-client"
pluginManagement {
    plugins {
        val kotlinterVersion: String by settings

        kotlin("plugin.serialization") version "1.9.23"
        id("org.jmailen.kotlinter") version kotlinterVersion
        id("maven-publish")
    }
}
