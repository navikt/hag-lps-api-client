val kotlin_version: String by project
val logback_version: String by project
val kotlinxSerializationVersion: String by project

plugins {
    application
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization")
    id("io.ktor.plugin") version "2.3.12"
    id("org.jmailen.kotlinter")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("maven-publish")
}

group = "no.nav.helsearbeidsgiver"
version = "0.0.1"

application {
    mainClass.set("no.nav.helsearbeidsgiver.ApplicationKt")
}

repositories {
    mavenCentral()

}
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

}
tasks.register("printVersion") {
    println(project.version)
}
dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-swagger")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
    implementation("io.ktor:ktor-client-apache5")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-client-core")
    implementation("com.nimbusds:nimbus-jose-jwt:9.40")

    implementation("org.bouncycastle:bcprov-jdk15on:1.70")

    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "no.nav.helsearbeidsgiver.ApplicationKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}


