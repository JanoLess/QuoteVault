# Where Modules 6 to 9 show up in QuoteVault

Use this during the 5-minute code walkthrough. Point at the file, then explain the idea in your own words.

## Module 6 — source sets, layers, repository, DI

- Source sets: `shared/src/commonMain`, `androidMain`, `iosMain`, `jvmMain`, plus `commonTest`
- Hosts: `androidApp` is the Android entry; `desktopApp` launches the same `App()` composable in a JVM window
- Gradle engines: OkHttp on Android, Darwin on iOS, CIO on desktop (`shared/build.gradle.kts` and `HttpClientFactory.*.kt`)
- Layers: `domain/` (rules) never talks to Ktor or SharedPreferences. `data/` talks to the network and local store. `presentation/` holds UI state.
- Repository: `QuoteRepository` in domain, `QuoteRepositoryImpl` in data
- Constructor injection: `AppGraph` builds `HttpClient` → API → repository → use cases → ViewModel. The ViewModel does not create its own dependencies.

## Module 7 — sealed state, Flow, ViewModel

- `QuotesUiState` is a sealed interface: Loading, Success, Error
- `BrowseQuotesViewModel` exposes `StateFlow` for the screen and `SharedFlow` for one-off snackbar messages
- The UI never mutates those flows. It calls ViewModel functions, then recomposes from the new state.

## Module 8 — Compose UI and navigation

- Screens collect `uiState` with `collectAsState()` in `QuoteVaultNavHost`
- Destinations live in `AppDestination`
- `NavHost` maps routes to screens
- Tapping a quote navigates to `quote/{quoteId}` and the detail screen reads that id from the back stack
- Deep link: `quotevault://quote/{quoteId}` (also declared in `AndroidManifest.xml`)
- System / Back button uses the navigation back stack (`popBackStack`)

## Module 9 — networking and persistence

- Ktor + kotlinx.serialization: `QuoteApi` converts JSON into `Quote` objects
- HTTP status and exceptions are checked so a failed call becomes an error message, not a crash
- Cache-first: `QuoteRepositoryImpl.getQuotes()` returns saved quotes if they exist, otherwise fetches and writes them to `LocalStore`
- Settings vs larger data: dark mode is a small key/value preference. The quote list and favorites are JSON blobs in the same store because the dataset is small. A Room/SQLDelight database would be the next step if quotes needed queries, relations, or large history.
