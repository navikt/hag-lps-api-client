@file:UseSerializers(LocalDateSerializer::class, LocalDateTimeSerializer::class)

package no.nav.helsearbeidsgiver.lps

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.JsonObject
import no.nav.helsearbeidsgiver.utils.LocalDateSerializer
import no.nav.helsearbeidsgiver.utils.LocalDateTimeSerializer
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class Forespoersel(
    val forespoersel_id: String,
    val orgnr: String,
    val fnr: String,
    val status: Status,
    val sykmeldingsperioder: List<Periode>,
    val egenmeldingsperioder: List<Periode>,
    val arbeidsgiverperiode_paakrevd: Boolean,
    val inntekt_paakrevd: Boolean,
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

@Serializable
data class ForespoerselRequest(
    val fnr: String? = null,
    val forespoersel_id: String? = null,
    val status: Status? = null,
)

@Serializable
data class ForespoerselResponse(
    val antall: Int,
    val forespoersler: List<Forespoersel>,
)

@Serializable
data class Periode(
    val fom: LocalDate,
    val tom: LocalDate,
)

@Serializable
data class InntektsmeldingRequest(
    val fnr: String? = null,
    val innsendingId: String? = null,
    val navReferanseId: String? = null,
    val fraTid: LocalDateTime? = null,
    val tilTid: LocalDateTime? = null,
)

@Serializable
data class InntektsmeldingResponse(
    val antall: Int = 0,
    val inntektsmeldinger: List<JsonObject>,
)
