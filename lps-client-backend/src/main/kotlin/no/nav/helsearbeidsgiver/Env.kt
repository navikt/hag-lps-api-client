package no.nav.helsearbeidsgiver

import io.github.cdimascio.dotenv.dotenv

private val dotenv = dotenv()
val maskinportenClientId: String = dotenv["MASKINPORTEN_CLIENT_ID"]
val maskinportenClientJwk: String = dotenv["MASKINPORTEN_CLIENT_JWK"]
val maskinportenClientIssuer: String = dotenv["MASKINPORTEN_ISSUER"]
val maskinportenPortenScopes: String = dotenv["MASKINPORTEN_SCOPES"]
val maskinportenTokenEndpoint: String = dotenv["MASKINPORTEN_TOKEN_ENDPOINT"]
val maskinportenIntegrasjonsId: String = dotenv["MASKINPORTEN_INTEGRATION_ID"]
val maskinportenKid: String = dotenv["MASKINPORTEN_KID"]
val maskinportenPrivateKey: String = dotenv["MASKINPORTEN_PKEY"]
val systemId: String = dotenv["SYSTEM_ID"]
val altinnSystemUserRequestUrl: String = dotenv["ALTINN_SYSTEM_USER_REQUEST_URL"]
