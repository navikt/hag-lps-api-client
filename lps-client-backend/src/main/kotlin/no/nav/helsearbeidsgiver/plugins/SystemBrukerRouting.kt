package no.nav.helsearbeidsgiver.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.request.receive
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import no.nav.helsearbeidsgiver.altinn.AltinnService
import no.nav.helsearbeidsgiver.altinn.RegistrerRespons
import no.nav.helsearbeidsgiver.maskinporten.MaskinportenService
import no.nav.helsearbeidsgiver.utils.logger

fun Application.configureSystembrukerRouting(maskinportenService: MaskinportenService) {
    val altinnService = AltinnService()
    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
        registrerNyBedrift(altinnService)
        hentSystembruker(maskinportenService)
        singlePageApplication {
            useResources = true
            filesPath = "lps-client-front"
            defaultPage = "index.html"
        }
    }
}

private fun Routing.hentSystembruker(maskinportenService: MaskinportenService) {
    // Henter systembruker token for en gitt orgnr
    post("/systembruker") {
        val parameters = call.receiveParameters()
        val orgnr = parameters["orgnr"] ?: return@post call.respond(
            HttpStatusCode.BadRequest,
            "Missing 'orgnr' parameter"
        )

        try {
            val systemBrukerClaim =
                maskinportenService
                    .getMaskinportenTokenForSystembruker(
                        orgnr,
                        "nav:helse/im.read",
                    ).fetchNewAccessToken()

            logger().info("token: $systemBrukerClaim")
            call.respond(HttpStatusCode.OK, systemBrukerClaim)
        } catch (e: Exception) {
            if (e.message?.contains("System user not found") == true) {
                call.respond(
                    HttpStatusCode.NotFound,
                    "Fant ikke systembruker for orgnr: $orgnr eller orgamisasjonen ikke har tilgang til tjenesten",
                )
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Feilet å hente systembruker: ${e.message}")
            }
        }
    }
}

private fun Routing.registrerNyBedrift(altinnService: AltinnService) {
    post("/registrer-ny-bedrift") {
        val parametre = call.receiveParameters()
        val kundeOrgnr =
            parametre["kundeOrgnr"] ?: return@post call.respond(
                HttpStatusCode.BadRequest,
                "Mangler 'kundeOrgnr' parameter",
            )

        logger().info("Prøver å registrere bedriften med orgnr: $kundeOrgnr som ny kunde.")
        try {
            val systemBrukerForespoerselRespons = altinnService.lagSystembrukerForespoersel(kundeOrgnr)
            logger().info(
                "Registrerte bedriften med orgnr: ${systemBrukerForespoerselRespons.redirectUrl} and ${systemBrukerForespoerselRespons.status} and ${systemBrukerForespoerselRespons.confirmUrl}",
            )
            logger().info("Registrerte bedriften med orgnr: $kundeOrgnr som ny kunde.")
            call.respond(HttpStatusCode.OK, RegistrerRespons(systemBrukerForespoerselRespons.confirmUrl))
        } catch (e: Exception) {
            logger().error("Noe gikk galt under registrering av bedriften med orgnr: $kundeOrgnr som ny kunde", e)
            call.respond(
                HttpStatusCode.InternalServerError,
                "Noe gikk galt under registrering av bedriften med orgnr: $kundeOrgnr som ny kunde: ${e.message}",
            )
        }
    }
}
