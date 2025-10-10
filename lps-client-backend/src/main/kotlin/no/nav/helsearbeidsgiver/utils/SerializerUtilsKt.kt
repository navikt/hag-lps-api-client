package no.nav.helsearbeidsgiver.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.json.Json

fun <T> KSerializer<T>.list(): KSerializer<List<T>> = ListSerializer(this)

fun <T> KSerializer<T>.set(): KSerializer<Set<T>> = SetSerializer(this)

val jsonConfig =
    Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }
