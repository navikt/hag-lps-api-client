package no.nav.helsearbeidsgiver.lps

import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import no.nav.helsearbeidsgiver.maskinporten.MaskinportenClient
import no.nav.helsearbeidsgiver.maskinporten.MaskinportenClientConfigPkey
import no.nav.helsearbeidsgiver.maskinporten.createHttpClient
import no.nav.helsearbeidsgiver.maskinporten.getSystemBrukerClaim

private const val LPS_API_ENDPOINT = "https://sykepenger-im-lps-api.ekstern.dev.nav.no/"

class LpsClient {
    suspend fun hentInntektsmeldinger(
        privateKey: String,
        kid: String,
        iss: String,
        consumerOrgNr: String,
    ): List<Inntektsmelding> {
        val fetchNewAccessToken =
            getMaskinportenClient(kid, privateKey, iss, consumerOrgNr).fetchNewAccessToken()
        val response =
            createHttpClient().get {
                url("${LPS_API_ENDPOINT}inntektsmeldinger")
                bearerAuth(fetchNewAccessToken.tokenResponse.accessToken)
                contentType(ContentType.Application.Json)
            }
        return response.body<List<Inntektsmelding>>()
    }

    fun getMaskinportenClient(
        kid: String,
        privateKey: String,
        iss: String,
        consumerOrgNr: String,
    ) = MaskinportenClient(
        maskinportenClientConfig =
            MaskinportenClientConfigPkey(
                kid = kid,
                privateKey = privateKey,
                issuer = "https://test.maskinporten.no/",
                scope = "nav:inntektsmelding/lps.write",
                clientId = iss,
                endpoint = "https://test.maskinporten.no/token",
                additionalClaims = getSystemBrukerClaim(consumerOrgNr),
            ),
    )

    suspend fun filtrerInntektsmeldinger(
        privateKey: String,
        kid: String,
        iss: String,
        consumerOrgNr: String,
        request: InntektsmeldingRequest,
    ): InntektsmeldingResponse {
        try {
            val accessToken =
                getMaskinportenClient(kid, privateKey, iss, consumerOrgNr).fetchNewAccessToken().tokenResponse.accessToken

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
        privateKey: String,
        kid: String,
        iss: String,
        consumerOrgNr: String,
        request: ForespoerselRequest,
    ): ForespoerselResponse {
        try {
            val accessToken =
                getMaskinportenClient(kid, privateKey, iss, consumerOrgNr).fetchNewAccessToken().tokenResponse.accessToken

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
            throw e
        }
    }

    suspend fun hentForespoersler(
        privateKey: String,
        kid: String,
        iss: String,
        consumerOrgNr: String,
    ): List<Forespoersel> {
        val fetchNewAccessToken =
            getMaskinportenClient(kid, privateKey, iss, consumerOrgNr).fetchNewAccessToken()
        val response =
            createHttpClient().get {
                url("${LPS_API_ENDPOINT}forespoersler")
                bearerAuth(fetchNewAccessToken.tokenResponse.accessToken)
                contentType(ContentType.Application.Json)
            }
        return response.body<List<Forespoersel>>()
    }
}
