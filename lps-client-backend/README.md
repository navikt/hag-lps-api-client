# Klient for LPS api

Denne applikasjonen er en klient for å hente data fra LPS API. Den genererer et token fra Maskinporten og kaller på LPS API-endepunkter for å hente data.
Api-et kan brukes til å hente data om inntektsmeldinger og forespørsler.
- Url til dev er https://hag-lps-api-client.ekstern.dev.nav.no/
- Url til swagger er https://hag-lps-api-client.ekstern.dev.nav.no/swagger

Dette repoet er ikke lenger i bruk, og har blitt arkivert.

## Forutsetninger

- JDK 21 eller høyere
- Gradle
- Kotlin

## Oppsett

1. **Klon prosjektet:**

   ```sh
   git clone https://github.com/navikt/hag-lps-api-client.git
   cd hag-lps-api-client
   ```

2. **Bygg prosjektet:**

   ```sh
    gradle build
    ```
3. **Kjør prosjektet:**
   
   Som en Gradle-applikasjon:
   ```sh
    gradle run
    ```

Eller som en standalone-applikasjon:

   ```sh
    java -jar build/libs/hag-lps-client-all.jar
   ```

4. **Åpne Swagger UI:**  
   Naviger til http://localhost:8080/swagger i nettleseren din for å se Swagger UI og dokumentasjonen.
