# QuoteVault QA package

## Test plan

Scope: shared logic in `commonTest`, plus a manual pass of the Android app.

Not in scope: iOS device run (no Mac required by the exam), paid APIs, accounts.

### Automated tests (`./gradlew :shared:allTests`)

| ID | Case | Expected |
| --- | --- | --- |
| A1 | Search by author, ignoring case | Matching quote only |
| A2 | Search by quote text | Matching quote only |
| A3 | Blank search | Full list |
| A4 | Toggle favorite twice | Added, then removed |
| A5 | GetQuotesUseCase | Returns repository quotes |
| A6 | Network fails after a successful load | Cached quotes returned |
| A7 | Favorites written to LocalStore | New repository instance still has them |
| A8 | First load with no cache and failed network | Exception, no crash in the repository |
| A9 | Dark mode preference | Starts false, becomes true after save |

### Manual Android cases

| ID | Case | Steps | Result | Status |
| --- | --- | --- | --- | --- |
| M1 | First launch online | Open app on emulator with internet | Browse shows quotes | |
| M2 | Search | Type part of an author name | List filters | |
| M3 | Detail navigation | Tap a card, then Back | Detail shows that quote, then Browse returns | |
| M4 | Favorite | Heart a quote, open Favorites | Quote is listed | |
| M5 | Unfavorite | Remove it from Detail or the list | Favorites count drops | |
| M6 | Settings | Toggle dark mode, leave Settings, kill app, reopen | Theme is still dark | |
| M7 | Offline after cache | Load once, enable airplane mode, Refresh | List stays usable | |
| M8 | Offline first launch (clear app data) | Airplane mode, fresh install | Error + Try again, no crash | |
| M9 | Deep link | `adb shell am start -a android.intent.action.VIEW -d quotevault://quote/1` | Detail for id 1 if that quote is loaded | |

Fill the Status column during Day 6–7 testing.

## Bug log

| ID | Found | Severity | Description | Fix | Recheck |
| --- | --- | --- | --- | --- | --- |
| B1 | Code review | Medium | Browse, Favorites, and Detail lived in one file; navigation was a manual list instead of routes | Split screens; NavHost with `quote/{quoteId}` | |
| B2 | Code review | Medium | Search query and theme lived in the App composable instead of state holders / settings repo | Query in ViewModel; theme in SettingsRepository | |
| B3 | Code review | Low | Android Ktor engine was CIO instead of the module’s OkHttp example | OkHttp on androidMain | |
| B4 | Code review | Low | Template Greeting/Platform files and a 1+2 test made the project harder to follow | Removed leftover template; tests cover search, cache, settings | |

Add new rows as QA finds issues during the demo week.
