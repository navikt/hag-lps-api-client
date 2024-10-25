package no.nav.helsearbeidsgiver.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import no.nav.helsearbeidsgiver.lps.LpsClient
import no.nav.helsearbeidsgiver.maskinporten.MaskinportenClient
import no.nav.helsearbeidsgiver.maskinporten.MaskinportenClientConfigPkey

fun Application.configureRouting() {
    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")

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
        post("/getToken") {
            val params = call.receiveParameters()

            val kid = params["kid"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'kid' parameter")
            val privateKey = params["privateKey"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'privateKey' parameter")
            val issuer = params["issuer"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'issuer' parameter")
            val consumerOrgNr =
                params["consumerOrgNr"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'consumerOrgNr' parameter")
            val scope = params["scope"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Mangler 'scope' parameter")

            call.respond(
                HttpStatusCode.OK,
                MaskinportenClient(
                    maskinportenClientConfig =
                        MaskinportenClientConfigPkey(
                            kid = kid,
                            privateKey = privateKey,
                            issuer = issuer,
                            consumerOrgNr = consumerOrgNr,
                            scope = scope,
                        ),
                ).fetchNewAccessToken(),
            )
        }
    }
}
