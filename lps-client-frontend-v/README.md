# LPS Client Frontend

## Contents

- [Introduction](#introduction)
- [Installation](#installation)
- [Start development server](#start-development-server)
- [Building](#building)

## Introduction

Dette er en frontend-applikasjon for å hente data fra LPS API. Den genererer et token fra Maskinporten og kaller på LPS API-endepunkter for å hente data.

## Installation

2. Install dependencies:
    ```bash
    npm install
    ```

## Start development server

To start the development server:
```bash
npm start
```

## Building

Denne applikasjonen er bygget med [Create React App](https://create-react-app.dev/). For å bygge applikasjonen kjører du:
```bash
npm run build
```
applikasjonen vil bygges og legges i

```code
lps-client-backend/src/main/resources/lps-client-front
```
