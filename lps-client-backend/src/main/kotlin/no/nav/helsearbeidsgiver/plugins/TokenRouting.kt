package no.nav.helsearbeidsgiver.plugins

import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import no.nav.helsearbeidsgiver.maskinporten.MaskinportenService
import no.nav.helsearbeidsgiver.maskinporten.createHttpClient
import no.nav.helsearbeidsgiver.utils.logger

fun Application.configureTokenRouting(maskinportenService: MaskinportenService) {
    routing {
        tokenMedOrgnrOgScope(maskinportenService)
        tokenForScope(maskinportenService)
        tokenForAltinn(maskinportenService)
    }
}

private fun Routing.tokenForScope(maskinportenService: MaskinportenService) {
    get("/token") {
        val parametre = call.receiveParameters()
        val scope =
            parametre["scope"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "Mangler 'scope' parameter",
            )
        val token = maskinportenService.getSimpleMaskinportenTokenForScope(scope).fetchNewAccessToken()
        call.respond(HttpStatusCode.OK, token)
    }
}

private fun Routing.tokenForAltinn(maskinportenService: MaskinportenService) {
    get("/token/altinn") {
        try {
            val parametre = call.receiveParameters()
            val scope =
                parametre["scope"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Mangler 'scope' parameter",
                )
            val token = maskinportenService.getSimpleMaskinportenTokenForScope(scope).fetchNewAccessToken()
            val altinnToken =
                createHttpClient()
                    .get("https://platform.tt02.altinn.no/authentication/api/v1/exchange/maskinporten") {
                        bearerAuth(token.tokenResponse.accessToken)
                        contentType(ContentType.Application.Json)
                        accept(ContentType.Application.Json)
                    }.body<String>()

            call.respond(HttpStatusCode.OK, altinnToken)
        } catch (e: Exception) {
            logger().error("Feil ved henting av token", e)
            call.respond(HttpStatusCode.InternalServerError, e.message ?: "Noe gikk galt")
        }
    }
}

private fun Routing.tokenMedOrgnrOgScope(maskinportenService: MaskinportenService) {
    get("/token/consumer") {
        try {
            val params = call.receiveParameters()

            val scope = params["scope"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Mangler 'scope' parameter")
            val consumerOrgNr =
                params["consumerOrgNr"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Mangler 'consumerOrgNr' parameter")

            val message =
                maskinportenService
                    .getMaskinportenTokenForOrgNr(
                        consumerOrgNr,
                        scope,
                    ).fetchNewAccessToken()
                    .tokenResponse.accessToken
            call.respond(
                HttpStatusCode.OK,
                message,
            )
        } catch (e: Exception) {
            logger().info("Feilet å hente token: $e")
            call.respond(HttpStatusCode.InternalServerError, "Feilet å hente token: ${e.message}")
        }
    }
}
