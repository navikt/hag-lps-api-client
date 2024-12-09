package no.nav.helsearbeidsgiver.altinn

import kotlinx.serialization.Serializable

// Se Altinn sin dokumentasjon for request og responsobjekter https://docs.altinn.studio/nb/api/authentication/spec/#/RequestSystemUser/post_systemuser_request_vendor

@Serializable
data class CreateRequestSystemUser(
    val externalRef: String? = null,
    val systemId: String,
    val partyOrgNo: String,
    val rights: List<Right>,
    val redirectUrl: String?,
)

@Serializable
data class Right(
    val action: String?,
    val resource: List<AttributePair>,
)

@Serializable
data class AttributePair(
    val id: String,
    val value: String,
)

@Serializable
data class RequestSystemResponse(
    val id: String,
    val externalRef: String?,
    val systemId: String,
    val partyOrgNo: String,
    val rights: List<Right>,
    val status: String,
    val redirectUrl: String?,
    val confirmUrl: String? = null,
)

@Serializable
data class RegistrerRespons(
    val confirmUrl: String?,
)
