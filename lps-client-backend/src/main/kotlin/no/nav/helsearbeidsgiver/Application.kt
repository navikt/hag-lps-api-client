package no.nav.helsearbeidsgiver

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.request.path
import kotlinx.serialization.json.Json
import no.nav.helsearbeidsgiver.maskinporten.MaskinportenClient
import no.nav.helsearbeidsgiver.maskinporten.MaskinportenClientConfigPkey
import no.nav.helsearbeidsgiver.plugins.configureRouting
import org.slf4j.event.Level

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain
        .main(args)
}

fun Application.module() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            },
        )
    }

    val maskinportenClient =
        MaskinportenClient(
            maskinportenClientConfig =
                MaskinportenClientConfigPkey(
                    scope = "altinn:authentication/systemuser.request.write",
                    kid = System.getenv("MASKINPORTEN_KID"),
                    clientId = System.getenv("MASKINPORTEN_INTEGRATION_ID"),
                    privateKey = System.getenv("MASKINPORTEN_PKEY"),
                    issuer = "https://test.maskinporten.no/",
                    endpoint = "https://test.maskinporten.no/token",
                ),
        )

    configureRouting(maskinportenClient)
    configureCORS()
}

fun Application.configureCORS() {
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
    }
}
