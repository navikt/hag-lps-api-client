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
import no.nav.helsearbeidsgiver.logger
import no.nav.helsearbeidsgiver.lps.ForespoerselRequest
import no.nav.helsearbeidsgiver.lps.InntektsmeldingRequest
import no.nav.helsearbeidsgiver.lps.LpsClient
import no.nav.helsearbeidsgiver.lps.Status
import no.nav.helsearbeidsgiver.maskinporten.MaskinportenClient
import no.nav.helsearbeidsgiver.maskinporten.MaskinportenClientConfigPkey
import java.time.LocalDateTime

fun Application.configureRouting() {
    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.json")
        inntektsmeldinger()
        filtererInntektsmeldinger()
        filtererInntektsmeldingerWithToken()
        forespoersler()
        filtererForespoersler()
        getToken()
        singlePageApplication {
            useResources = true
            filesPath = "lps-client-front"
            defaultPage = "index.html"
        }
    }
}

private fun Routing.inntektsmeldinger() {
    post("/inntektsmeldinger") {
        val params = call.receiveParameters()
        val kid = params["kid"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'kid' parameter")
        val privateKey = params["privateKey"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'privateKey' parameter")
        val issuer = params["issuer"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'issuer' parameter")
        val consumerOrgNr =
            params["consumerOrgNr"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'consumerOrgNr' parameter")

        try {
            val hentInntektsmeldinger = LpsClient().hentInntektsmeldinger(privateKey, kid, issuer, consumerOrgNr)
            call.respond(HttpStatusCode.OK, hentInntektsmeldinger)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Feilet å hente inntektsmeldinger: ${e.message}")
        }
    }
}

private fun Routing.getToken() {

    post("/getToken") {
        try {
            val params = call.receiveParameters()

            val kid = params["kid"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'kid' parameter")
            val privateKey = params["privateKey"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'privateKey' parameter")
            val issuer = params["issuer"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'issuer' parameter")
            val consumerOrgNr =
                params["consumerOrgNr"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'consumerOrgNr' parameter")
            val scope = params["scope"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'scope' parameter")

            val message = LpsClient().getMaskinportenClient(kid, privateKey, issuer, consumerOrgNr).fetchNewAccessToken()
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

private fun Routing.forespoersler() {
    post("/forespoersler") {
        val params = call.receiveParameters()

        val kid = params["kid"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'kid' parameter")
        val privateKey =
            params["privateKey"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'privateKey' parameter")
        val issuer = params["issuer"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'issuer' parameter")
        val consumerOrgNr =
            params["consumerOrgNr"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'consumerOrgNr' parameter")

        try {
            val forespoerseler = LpsClient().hentForespoersler(privateKey, kid, issuer, consumerOrgNr)
            call.respond(HttpStatusCode.OK, forespoerseler)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Feilet å hente Forespørseler: ${e.message}")
        }
    }
}

private fun Routing.filtererInntektsmeldinger() {
    post("/filterInntektsmeldinger") {
        val params = call.receiveParameters()
        val kid = params["kid"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'kid' parameter")
        val privateKey = params["privateKey"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'privateKey' parameter")
        val issuer = params["issuer"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'issuer' parameter")
        val consumerOrgNr =
            params["consumerOrgNr"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'consumerOrgNr' parameter")
        val fnr = params["fnr"]?.takeIf { it.isNotBlank() }
        val forespoerselId = params["forespoerselId"]?.takeIf { it.isNotBlank() }
        val datoFra = params["datoFra"]?.takeIf { it.isNotBlank() }?.let { LocalDateTime.parse(it) }
        val datoTil = params["datoTil"]?.takeIf { it.isNotBlank() }?.let { LocalDateTime.parse(it) }
        logger().info("filterInntektsmeldinger: $params")
        try {
            val hentInntektsmeldinger =
                LpsClient().filtrerInntektsmeldinger(
                    privateKey,
                    kid,
                    issuer,
                    consumerOrgNr,
                    request = InntektsmeldingRequest(fnr, forespoerselId, datoFra, datoTil),
                )
            call.respond(HttpStatusCode.OK, hentInntektsmeldinger)
        } catch (e: Exception) {
            logger().error("Feilet å hente inntektsmeldinger", e)
            call.respond(HttpStatusCode.InternalServerError, "Feilet å hente inntektsmeldinger: ${e.message}")
        }
    }
}

private fun Routing.filtererForespoersler() {
    post("/filterForespoersler") {
        val params = call.receiveParameters()
        val kid = params["kid"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'kid' parameter")
        val privateKey = params["privateKey"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'privateKey' parameter")
        val issuer = params["issuer"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'issuer' parameter")
        val consumerOrgNr =
            params["consumerOrgNr"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'consumerOrgNr' parameter")
        val fnr = params["fnr"]?.takeIf { it.isNotBlank() }
        val forespoerselId = params["forespoerselId"]?.takeIf { it.isNotBlank() }
        val status = params["status"]?.takeIf { it.isNotBlank() }
        logger().info("filterForespoersler: $params")
        try {
            val hentForespoersler =
                LpsClient().filtrerForespoersler(
                    privateKey,
                    kid,
                    issuer,
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

private fun Routing.filtererInntektsmeldingerWithToken() {
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
                LpsClient().filtrerInntektsmeldingerWithToken(
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
