@file:UseSerializers(LocalDateSerializer::class, LocalDateTimeSerializer::class)

package no.nav.helsearbeidsgiver.lps

import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.server.util.url
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.JsonObject
import no.nav.helsearbeidsgiver.maskinporten.MaskinportenClient
import no.nav.helsearbeidsgiver.maskinporten.MaskinportenClientConfigPkey
import no.nav.helsearbeidsgiver.maskinporten.createHttpClient
import no.nav.helsearbeidsgiver.utils.LocalDateSerializer
import no.nav.helsearbeidsgiver.utils.LocalDateTimeSerializer
import java.time.LocalDateTime

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

@Serializable
data class InntektsmeldingRequest(
    val fnr: String? = null,
    val foresporselid: String? = null,
    val datoFra: LocalDateTime? = null,
    val datoTil: LocalDateTime? = null,
)

@Serializable
data class InntektsmeldingResponse(
    val antallInntektsmeldinger: Int = 0,
    val inntektsmeldinger: List<Inntektsmelding>,
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

    suspend fun filtrerInntektsmeldinger(
        privateKey: String,
        kid: String,
        iss: String,
        consumerOrgNr: String,
        request: InntektsmeldingRequest,
    ): InntektsmeldingResponse {
        val accessToken =
            MaskinportenClient(
                maskinportenClientConfig =
                    MaskinportenClientConfigPkey(
                        kid = kid,
                        privateKey = privateKey,
                        issuer = iss,
                        consumerOrgNr = consumerOrgNr,
                    ),
            ).fetchNewAccessToken().tokenResponse.accessToken

        val response =
            createHttpClient().post {
                url("https://sykepenger-im-lps-api.ekstern.dev.nav.no/inntektsmeldinger")
                setBody(request)
                bearerAuth(accessToken)
                contentType(ContentType.Application.Json)
            }
        return response.body<InntektsmeldingResponse>()
    }

    suspend fun filtrerInntektsmeldingerWithToken(
        request: InntektsmeldingRequest,
        accessToken: String,
    ): InntektsmeldingResponse {
        val response =
            createHttpClient().post {
                url("https://sykepenger-im-lps-api.ekstern.dev.nav.no/inntektsmeldinger")
                setBody(request)
                bearerAuth(accessToken)
                contentType(ContentType.Application.Json)
            }
        return response.body<InntektsmeldingResponse>()
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
