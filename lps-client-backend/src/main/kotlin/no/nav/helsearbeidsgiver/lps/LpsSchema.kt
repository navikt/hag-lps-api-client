@file:UseSerializers(LocalDateSerializer::class, LocalDateTimeSerializer::class)

package no.nav.helsearbeidsgiver.lps

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.JsonObject
import no.nav.helsearbeidsgiver.utils.LocalDateSerializer
import no.nav.helsearbeidsgiver.utils.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class ForespoerselRequest(
    val fnr: String? = null,
    val forespoerselId: String? = null,
    val status: Status? = null,
)

@Serializable
data class ForespoerselResponse(
    val antallForespoersler: Int,
    val forespoerseler: List<Forespoersel>,
)

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

@Serializable
data class Forespoersel(
    val forespoerselId: String,
    val orgnr: String,
    val fnr: String,
    val status: Status,
    val dokument: JsonObject,
)

@Serializable
enum class Status {
    AKTIV,
    MOTTATT,
    FORKASTET,
}

enum class Type {
    KOMPLETT,
    BEGRENSET,
}
