package no.nav.helsearbeidsgiver.lps

import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import no.nav.helsearbeidsgiver.maskinporten.MaskinportenService
import no.nav.helsearbeidsgiver.maskinporten.createHttpClient
import no.nav.helsearbeidsgiver.utils.logger

private const val LPS_API_ENDPOINT = "https://sykepenger-im-lps-api.ekstern.dev.nav.no/v1/"

class LpsClient(
    var maskinportenService: MaskinportenService,
) {
    suspend fun hentInntektsmeldinger(consumerOrgNr: String): List<InnsendtInntektsmelding> {
        val fetchNewAccessToken =
            maskinportenService.getMaskinportenTokenForOrgNr(consumerOrgNr).fetchNewAccessToken()
        val response =
            createHttpClient().get {
                url("${LPS_API_ENDPOINT}inntektsmeldinger")
                bearerAuth(fetchNewAccessToken.tokenResponse.accessToken)
                contentType(ContentType.Application.Json)
            }
        return response.body<List<InnsendtInntektsmelding>>()
    }

    suspend fun filtrerInntektsmeldinger(
        consumerOrgNr: String,
        request: InntektsmeldingRequest,
    ): InntektsmeldingResponse {
        try {
            val accessToken =
                maskinportenService
                    .getMaskinportenTokenForOrgNr(consumerOrgNr)
                    .fetchNewAccessToken()
                    .tokenResponse.accessToken

            val response =
                createHttpClient().post {
                    url("${LPS_API_ENDPOINT}inntektsmeldinger")
                    setBody(request)
                    bearerAuth(accessToken)
                    contentType(ContentType.Application.Json)
                }
            return response.body<InntektsmeldingResponse>()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun filtrerForespoersler(
        consumerOrgNr: String,
        request: ForespoerselRequest,
    ): ForespoerselResponse {
        try {
            val accessToken =
                maskinportenService
                    .getMaskinportenTokenForOrgNr(consumerOrgNr)
                    .fetchNewAccessToken()
                    .tokenResponse.accessToken

            val response =
                createHttpClient().post {
                    url("${LPS_API_ENDPOINT}forespoersler")
                    setBody(request)
                    bearerAuth(accessToken)
                    contentType(ContentType.Application.Json)
                }
            return response.body<ForespoerselResponse>()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun filtrerInntektsmeldingerWithToken(
        request: InntektsmeldingRequest,
        accessToken: String,
    ): InntektsmeldingResponse {
        try {
            val response =
                createHttpClient().post {
                    url("${LPS_API_ENDPOINT}inntektsmeldinger")
                    setBody(request)
                    bearerAuth(accessToken)
                    contentType(ContentType.Application.Json)
                }
            return response.body<InntektsmeldingResponse>()
        } catch (e: Exception) {
            logger().error("Error in filtrerInntektsmeldingerWithToken {}", e.message)
            throw e
        }
    }

    suspend fun hentForespoersler(consumerOrgNr: String): List<Forespoersel> {
        val accessToken =
            maskinportenService
                .getMaskinportenTokenForOrgNr(consumerOrgNr)
                .fetchNewAccessToken()
                .tokenResponse.accessToken
        val response =
            createHttpClient().get {
                url("${LPS_API_ENDPOINT}forespoersler")
                bearerAuth(accessToken)
                contentType(ContentType.Application.Json)
            }
        return response.body<List<Forespoersel>>()
    }
}
