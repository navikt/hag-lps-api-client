rootProject.name = "hag-lps-api-client"
include("lps-client-backend")
pluginManagement {
    plugins {
        val kotlinterVersion: String by settings

        kotlin("plugin.serialization") version "1.9.23"
        id("org.jmailen.kotlinter") version "4.4.0"
        id("maven-publish")
    }
}