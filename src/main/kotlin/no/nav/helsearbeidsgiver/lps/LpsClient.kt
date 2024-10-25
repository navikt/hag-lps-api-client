package no.nav.helsearbeidsgiver.lps

import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import no.nav.helsearbeidsgiver.maskinporten.MaskinportenClient
import no.nav.helsearbeidsgiver.maskinporten.MaskinportenClientConfigPkey
import no.nav.helsearbeidsgiver.maskinporten.createHttpClient

@Serializable
data class Inntektsmelding(
    val dokument: JsonObject,
    val orgnr: String,
    val fnr: String,
    val foresporselid: String?,
    val innsendt: String,
    val mottattEvent: String,
)
@Serializable
data class Forespoersel(
    val forespoerselId: String,
    val orgnr: String,
    val fnr: String,
    val status: String,
)

class LpsClient {
    suspend fun hentInntektsmeldinger(
        privateKey: String,
        kid: String,
        iss: String,
        consumerOrgNr: String,
    ): List<Inntektsmelding> {
        val fetchNewAccessToken =
            MaskinportenClient(
                maskinportenClientConfig =
                    MaskinportenClientConfigPkey(
                        kid = kid,
                        privateKey = privateKey,
                        issuer = iss,
                        consumerOrgNr = consumerOrgNr,
                    ),
            ).fetchNewAccessToken()
        val response =
            createHttpClient().get {
                url("https://sykepenger-im-lps-api.ekstern.dev.nav.no/inntektsmeldinger")
                bearerAuth(fetchNewAccessToken.tokenResponse.accessToken)
                contentType(ContentType.Application.Json)
            }
        return response.body<List<Inntektsmelding>>()
    }

    suspend fun hentForespoersler(
        privateKey: String,
        kid: String,
        iss: String,
        consumerOrgNr: String,
    ): List<Forespoersel> {
        val fetchNewAccessToken =
            MaskinportenClient(
                maskinportenClientConfig =
                    MaskinportenClientConfigPkey(
                        kid = kid,
                        privateKey = privateKey,
                        issuer = iss,
                        consumerOrgNr = consumerOrgNr,
                    ),
            ).fetchNewAccessToken()
        val response =
            createHttpClient().get {
                url("https://sykepenger-im-lps-api.ekstern.dev.nav.no/forespoersler")
                bearerAuth(fetchNewAccessToken.tokenResponse.accessToken)
                contentType(ContentType.Application.Json)
            }
        return response.body<List<Forespoersel>>()
    }
}
