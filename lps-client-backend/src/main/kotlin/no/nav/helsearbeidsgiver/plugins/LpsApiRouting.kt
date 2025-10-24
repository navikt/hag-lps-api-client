package no.nav.helsearbeidsgiver.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import no.nav.helsearbeidsgiver.lps.ForespoerselRequest
import no.nav.helsearbeidsgiver.lps.InntektsmeldingRequest
import no.nav.helsearbeidsgiver.lps.LpsClient
import no.nav.helsearbeidsgiver.lps.Status
import no.nav.helsearbeidsgiver.maskinporten.MaskinportenService
import no.nav.helsearbeidsgiver.utils.logger
import java.time.LocalDate

fun Application.configureLpsApiRouting(maskinportenService: MaskinportenService) {
    val lpsClient = LpsClient(maskinportenService)
    routing {
        inntektsmeldinger(lpsClient)
        filtererInntektsmeldinger(lpsClient)
        filtererForespoersler(lpsClient)
        filtererInntektsmeldingerWithToken(lpsClient)
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

private fun Routing.filtererInntektsmeldinger(lpsClient: LpsClient) {
    post("/filterInntektsmeldinger") {
        val params = call.receiveParameters()

        val consumerOrgNr =
            params["consumerOrgNr"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'consumerOrgNr' parameter")
        val underenhetOrgnr =
            params["underenhetOrgnr"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'underenhetOrgnr' parameter")
        val fnr = params["fnr"]?.takeIf { it.isNotBlank() }
        val innsendingId = params["innsendingId"]?.takeIf { it.isNotBlank() }
        val navReferanseId = params["navReferanseId"]?.takeIf { it.isNotBlank() }
        val fom = params["fom"]?.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) }
        val tom = params["tom"]?.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) }
        logger().info("filterInntektsmeldinger: $params")
        try {
            val hentInntektsmeldinger =
                lpsClient.filtrerInntektsmeldinger(
                    consumerOrgNr = consumerOrgNr,
                    request = InntektsmeldingRequest(underenhetOrgnr, fnr, innsendingId, navReferanseId, fom, tom),
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
        val navReferanseId = params["navReferanseId"]?.takeIf { it.isNotBlank() }
        val status = params["status"]?.takeIf { it.isNotBlank() }
        logger().info("filterForespoersler: $params")
        try {
            val hentForespoersler =
                lpsClient.filtrerForespoersler(
                    consumerOrgNr,
                    request = ForespoerselRequest(fnr, navReferanseId, status?.let { Status.valueOf(it) }),
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
        val underenhetOrgnr =
            params["underenhetOrgnr"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'underenhetOrgnr' parameter")
        val fnr = params["fnr"]?.takeIf { it.isNotBlank() }?.trim()
        val navReferanseId = params["navReferanseId"]?.takeIf { it.isNotBlank() }?.trim()
        val innsendingId = params["innsendingId"]?.takeIf { it.isNotBlank() }
        val fom = params["fom"]?.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) }
        val tom = params["tom"]?.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) }
        val authorizationHeader: String =
            call.request.headers["Authorization"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'Authorization' header")

        try {
            val hentInntektsmeldinger =
                lpsClient.filtrerInntektsmeldingerWithToken(
                    request = InntektsmeldingRequest(underenhetOrgnr, fnr, innsendingId, navReferanseId, fom, tom),
                    accessToken = authorizationHeader,
                )
            call.respond(HttpStatusCode.OK, hentInntektsmeldinger)
        } catch (e: Exception) {
            if (e.message?.contains("Ingen inntektsmeldinger funnet") == true) {
                call.respond(HttpStatusCode.NotFound, "Ingen inntektsmeldinger funnet")
            }
            logger().error("Feilet å hente inntektsmeldinger: ${e.printStackTrace()}")
            call.respond(HttpStatusCode.InternalServerError, "Feilet å hente inntektsmeldinger: ${e.message}")
        }
    }
}
