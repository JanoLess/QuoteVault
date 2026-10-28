# QuoteVault

QuoteVault is a Kotlin Multiplatform app for browsing inspirational quotes, saving favorites, and keeping a small local cache so the list still works after the first successful load.

The Android app is the graded target. The `shared` module also keeps an iOS source set and a desktop entry so the same business logic can run on more than one platform.

## What you can do

- Browse quotes from [DummyJSON](https://dummyjson.com/quotes)
- Search by quote text or author
- Open a quote detail screen (the quote id is passed in the navigation route)
- Save and remove favorites
- Turn dark mode on or off (saved on the device)
- Pull to refresh; if the network fails, cached quotes are shown instead of crashing

## Team roles

Fill in your names before submission.

| Role | Name | Owns |
| --- | --- | --- |
| BA / Developer | | Requirements, screen flow, Compose screens |
| Dedicated Developer | | Shared architecture, networking, persistence, project setup |
| QA | | Test plan, bug log, `shared` automated tests |

## How the code is organized

```
shared/src/commonMain/kotlin/com/example/quotevault/
  di/                 constructor injection (AppGraph)
  domain/             Quote model, repository interfaces, use cases
  data/               Ktor API, LocalStore, repository implementations
  presentation/       ViewModel, screens, navigation, theme
androidApp/           Android host
desktopApp/           Desktop host
shared/src/iosMain/   iOS expect/actual implementations
```

Module mapping for defense is in [docs/MODULE_MAP.md](docs/MODULE_MAP.md).
BA requirements are in [docs/REQUIREMENTS.md](docs/REQUIREMENTS.md).
QA package is in [docs/QA.md](docs/QA.md).

## Setup

1. JDK 17 or newer
2. Android Studio / IntelliJ with the Kotlin Multiplatform plugin
3. Android SDK with compile SDK 37 and a device or emulator on API 24+

## Run Android

```
./gradlew :androidApp:assembleDebug
```

Install the APK from `androidApp/build/outputs/apk/debug/androidApp-debug.apk`, or use the Android Studio run configuration for `:androidApp`.

Internet is required for the first quote load. After that, cached quotes stay on the device.

## Run desktop

The same shared screens, ViewModel, networking, favorites, and settings run in a Compose Desktop window (`desktopApp`). Quotes and dark mode are stored in `~/.quotevault` on your computer.

```
./gradlew :desktopApp:run
```

On Windows PowerShell:

```
.\gradlew.bat :desktopApp:run
```

In the IDE, run the `main` function in `desktopApp/src/main/kotlin/com/example/quotevault/Main.kt`. Wide windows use a side navigation rail and a quote grid; a narrower window keeps the phone-style bottom bar.

## Run tests

```
./gradlew :shared:cleanAllTests :shared:allTests
```

On Windows PowerShell:

```
.\gradlew.bat :shared:cleanAllTests :shared:allTests
```

## Deep link

A quote detail screen can be opened with:

```
quotevault://quote/{quoteId}
```

Example: `quotevault://quote/1`
