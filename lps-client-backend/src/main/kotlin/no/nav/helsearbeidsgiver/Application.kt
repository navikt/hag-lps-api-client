package no.nav.helsearbeidsgiver

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import no.nav.helsearbeidsgiver.maskinporten.MaskinportenService
import no.nav.helsearbeidsgiver.plugins.configureAltinnEventRouting
import no.nav.helsearbeidsgiver.plugins.configureLpsApiRouting
import no.nav.helsearbeidsgiver.plugins.configureSystembrukerRouting
import no.nav.helsearbeidsgiver.plugins.configureTokenRouting
import no.nav.helsearbeidsgiver.utils.jsonConfig

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(
            jsonConfig,
        )
    }

    val maskinportenService = MaskinportenService()
    configureSystembrukerRouting(maskinportenService)
    configureTokenRouting(maskinportenService)
    configureLpsApiRouting(maskinportenService)
    configureAltinnEventRouting()
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
