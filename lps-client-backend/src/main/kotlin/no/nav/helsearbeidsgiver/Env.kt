package no.nav.helsearbeidsgiver

import io.github.cdimascio.dotenv.dotenv

private val dotenv =
    dotenv {
        ignoreIfMalformed = true
        ignoreIfMissing = true
    }

val maskinportenClientIssuer: String = System.getenv("MASKINPORTEN_ISSUER") ?: dotenv["MASKINPORTEN_ISSUER"]
val maskinportenTokenEndpoint: String = System.getenv("MASKINPORTEN_TOKEN_ENDPOINT") ?: dotenv["MASKINPORTEN_TOKEN_ENDPOINT"]
val maskinportenIntegrasjonsId: String = System.getenv("MASKINPORTEN_INTEGRATION_ID") ?: dotenv["MASKINPORTEN_INTEGRATION_ID"]
val maskinportenKid: String = System.getenv("MASKINPORTEN_KID") ?: dotenv["MASKINPORTEN_KID"]
val maskinportenPrivateKey: String = System.getenv("MASKINPORTEN_PKEY") ?: dotenv["MASKINPORTEN_PKEY"]
val systemId: String = System.getenv("SYSTEM_ID") ?: dotenv["SYSTEM_ID"]
val altinnSystemUserRequestUrl: String = System.getenv("ALTINN_SYSTEM_USER_REQUEST_URL") ?: dotenv["ALTINN_SYSTEM_USER_REQUEST_URL"]
