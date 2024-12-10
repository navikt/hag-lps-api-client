package no.nav.helsearbeidsgiver.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import no.nav.helsearbeidsgiver.altinn.AltinnService
import no.nav.helsearbeidsgiver.altinn.RegistrerRespons
import no.nav.helsearbeidsgiver.lps.ForespoerselRequest
import no.nav.helsearbeidsgiver.lps.InntektsmeldingRequest
import no.nav.helsearbeidsgiver.lps.LpsClient
import no.nav.helsearbeidsgiver.lps.Status
import no.nav.helsearbeidsgiver.maskinporten.MaskinportenService
import no.nav.helsearbeidsgiver.utils.logger
import java.time.LocalDateTime

fun Application.configureRouting(maskinportenService: MaskinportenService) {
    val lpsClient = LpsClient(maskinportenService)
    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.json")
        registrerNyBedrift(maskinportenService)
        inntektsmeldinger(lpsClient)
        filtererInntektsmeldinger(lpsClient)
        filtererInntektsmeldingerWithToken(lpsClient)
        forespoersler(maskinportenService)
        filtererForespoersler(lpsClient)
        getToken(maskinportenService)
        hentSystembruker(maskinportenService)
        singlePageApplication {
            useResources = true
            filesPath = "lps-client-front"
            defaultPage = "index.html"
        }
    }
}

private fun Routing.hentSystembruker(maskinportenService: MaskinportenService) {
    post("/systembruker") {
        val params = call.receiveParameters()
        val orgnr = params["orgnr"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'orgnr' parameter")

        try {
            val systemBrukerClaim =
                maskinportenService
                    .getMaskinportenTokenForSystembruker(
                        orgnr,
                        "nav:helse/im.read",
                    ).fetchNewAccessToken()
                    .tokenResponse.accessToken
            logger().info("token: $systemBrukerClaim")
            call.respond(HttpStatusCode.OK, systemBrukerClaim)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Feilet å hente systembruker: ${e.message}")
        }
    }
}

private fun Routing.registrerNyBedrift(maskinportenService: MaskinportenService) {
    post("/registrer-ny-bedrift") {
        val parametre = call.receiveParameters()
        val kundeOrgnr =
            parametre["kundeOrgnr"] ?: return@post call.respond(
                HttpStatusCode.BadRequest,
                "Mangler 'kundeOrgnr' parameter",
            )

        logger().info("Prøver å registrere bedriften med orgnr: $kundeOrgnr som ny kunde.")
        try {
            val maskinportenToken =
                maskinportenService
                    .getSimpleMaskinportenTokenForScope("altinn:authentication/systemuser.request.write")
                    .fetchNewAccessToken()
                    .tokenResponse.accessToken

            val systemBrukerForespoerselRespons = AltinnService().lagSystembrukerForespoersel(kundeOrgnr, maskinportenToken)
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

private fun Routing.inntektsmeldinger(lpsClient: LpsClient) {
    post("/inntektsmeldinger") {
        val params = call.receiveParameters()

        val consumerOrgNr =
            params["consumerOrgNr"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'consumerOrgNr' parameter")

        try {
            val hentInntektsmeldinger = lpsClient.hentInntektsmeldinger(consumerOrgNr)
            call.respond(HttpStatusCode.OK, hentInntektsmeldinger)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Feilet å hente inntektsmeldinger: ${e.message}")
        }
    }
}

private fun Routing.getToken(maskinportenService: MaskinportenService) {
    post("/getToken") {
        try {
            val params = call.receiveParameters()

            val scope = params["scope"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'scope' parameter")
            val consumerOrgNr =
                params["consumerOrgNr"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'consumerOrgNr' parameter")

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

private fun Routing.forespoersler(maskinportenService: MaskinportenService) {
    post("/forespoersler") {
        val params = call.receiveParameters()

        val consumerOrgNr =
            params["consumerOrgNr"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'consumerOrgNr' parameter")

        try {
            val forespoerseler = maskinportenService.getMaskinportenTokenForOrgNr(consumerOrgNr)
            call.respond(HttpStatusCode.OK, forespoerseler)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Feilet å hente Forespørseler: ${e.message}")
        }
    }
}

private fun Routing.filtererInntektsmeldinger(lpsClient: LpsClient) {
    post("/filterInntektsmeldinger") {
        val params = call.receiveParameters()

        val consumerOrgNr =
            params["consumerOrgNr"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'consumerOrgNr' parameter")
        val fnr = params["fnr"]?.takeIf { it.isNotBlank() }
        val forespoerselId = params["forespoerselId"]?.takeIf { it.isNotBlank() }
        val datoFra = params["datoFra"]?.takeIf { it.isNotBlank() }?.let { LocalDateTime.parse(it) }
        val datoTil = params["datoTil"]?.takeIf { it.isNotBlank() }?.let { LocalDateTime.parse(it) }
        logger().info("filterInntektsmeldinger: $params")
        try {
            val hentInntektsmeldinger =
                lpsClient.filtrerInntektsmeldinger(
                    consumerOrgNr = consumerOrgNr,
                    request = InntektsmeldingRequest(fnr, forespoerselId, datoFra, datoTil),
                )
            call.respond(HttpStatusCode.OK, hentInntektsmeldinger)
        } catch (e: Exception) {
            logger().error("Feilet å hente inntektsmeldinger", e)
            call.respond(HttpStatusCode.InternalServerError, "Feilet å hente inntektsmeldinger: ${e.message}")
        }
    }
}

private fun Routing.filtererForespoersler(lpsClient: LpsClient) {
    post("/filterForespoersler") {
        val params = call.receiveParameters()
        val consumerOrgNr =
            params["consumerOrgNr"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'consumerOrgNr' parameter")
        val fnr = params["fnr"]?.takeIf { it.isNotBlank() }
        val forespoerselId = params["forespoerselId"]?.takeIf { it.isNotBlank() }
        val status = params["status"]?.takeIf { it.isNotBlank() }
        logger().info("filterForespoersler: $params")
        try {
            val hentForespoersler =
                lpsClient.filtrerForespoersler(
                    consumerOrgNr,
                    request = ForespoerselRequest(fnr, forespoerselId, status?.let { Status.valueOf(it) }),
                )
            call.respond(HttpStatusCode.OK, hentForespoersler)
        } catch (e: Exception) {
            logger().error("Feilet å hente forespørsler", e)
            call.respond(HttpStatusCode.InternalServerError, "Feilet å hente forespørsler: ${e.message}")
        }
    }
}

private fun Routing.filtererInntektsmeldingerWithToken(lpsClient: LpsClient) {
    post("/filterInntektsmeldingerToken") {
        val params = call.receiveParameters()
        logger().info("filterInntektsmeldingerToken: $params")
        val fnr = params["fnr"]?.takeIf { it.isNotBlank() }
        val forespoerselId = params["forespoerselId"]?.takeIf { it.isNotBlank() }
        val datoFra = params["datoFra"]?.takeIf { it.isNotBlank() }?.let { LocalDateTime.parse(it) }
        val datoTil = params["datoTil"]?.takeIf { it.isNotBlank() }?.let { LocalDateTime.parse(it) }
        val authorizationHeader: String =
            call.request.headers["Authorization"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'Authorization' header")

        try {
            val hentInntektsmeldinger =
                lpsClient.filtrerInntektsmeldingerWithToken(
                    request = InntektsmeldingRequest(fnr, forespoerselId, datoFra, datoTil),
                    accessToken = authorizationHeader,
                )
            call.respond(HttpStatusCode.OK, hentInntektsmeldinger)
        } catch (e: Exception) {
            logger().error("Feilet å hente inntektsmeldinger: ${e.printStackTrace()}")
            call.respond(HttpStatusCode.InternalServerError, "Feilet å hente inntektsmeldinger: ${e.message}")
        }
    }
}
