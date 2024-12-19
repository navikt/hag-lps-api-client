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
import java.time.LocalDateTime

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
            if (e.message?.contains("Ingen inntektsmeldinger funnet") == true) {
                call.respond(HttpStatusCode.NotFound, "Ingen inntektsmeldinger funnet")
            }
            logger().error("Feilet å hente inntektsmeldinger: ${e.printStackTrace()}")
            call.respond(HttpStatusCode.InternalServerError, "Feilet å hente inntektsmeldinger: ${e.message}")
        }
    }
}
