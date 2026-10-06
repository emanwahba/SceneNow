# SceneNow

An Android app that shows this week's trending movies from [TMDB](https://www.themoviedb.org/).
You can filter the list by genre, sort it, and open a movie to see its details.

## Features

- **Trending list**: the top 100 trending movies of the week (5 TMDB pages of 20), with duplicates removed.
- **Genre filter**: filter chips built from TMDB's genre list.
- **Sorting**: by popularity, title or release date, ascending or descending.
- **Movie detail**: poster (tap for full size), tagline, overview, genres, rating, runtime, status, budget, revenue and an IMDb link.
- **Error handling**: loading and error states with retry on both screens.

## Setup

1. Get a TMDB **API Read Access Token** from your TMDB account settings.
2. Add it to `local.properties` (this file is git-ignored):

   ```properties
   TMDB_ACCESS_TOKEN=your_token_here
   ```

3. Open the project in Android Studio and run the `app` configuration.

Without a token, every request fails with 401 and the app shows its error state.

Other commands:

```bash
./gradlew :app:assembleDebug   # build the app
./gradlew testDebugUnitTest    # run all unit tests
./gradlew lintDebug            # run Android lint
```

## Architecture

The project follows **Clean Architecture** with **MVVM** in the UI layer, split into Gradle modules.
Dependencies point inward: the feature and data modules depend on `:core:domain`, and `:core:domain` depends on no other module. `:core:ui` holds shared Compose code and has no dependency on the other core modules.

```
                ┌──────────────────────────┐
                │           :app           │  wires everything together
                └──────┬──────────┬────────┘
                       │          │
          ┌────────────▼───┐  ┌───▼─────────────┐
          │ :feature:*     │  │ :core:data      │
          │ (screens + VMs)│  │ (repository)    │
          └──┬──────────┬──┘  └───┬─────────┬───┘
             │          │         │         │
       ┌─────▼────┐     │         │    ┌────▼──────────┐
       │ :core:ui │     │         │    │ :core:network │
       └──────────┘     │         │    │ (Retrofit)    │
                        │         │    └───────────────┘
                   ┌────▼─────────▼───┐
                   │   :core:domain   │  models, repository interface, use cases
                   └──────────────────┘
```

### Modules

| Module | Type | Responsibility |
|---|---|---|
| `:app` | Android application | `MainActivity`, Hilt application class, and `SceneNowNavHost`, the only place that knows about every feature. |
| `:core:domain` | Android library | Business logic with no UI or networking code: models (`Movie`, `MovieDetail`, `Genre`), the `MovieRepository` interface, the use cases, and the `DataResult` wrapper. |
| `:core:data` | Android library | `MovieRepositoryImpl`, DTO-to-domain mappers, error-to-message mapping, and the Hilt binding for the repository. |
| `:core:network` | Android library | Retrofit `TmdbApiService`, DTOs, and the OkHttp/Retrofit setup with the auth header. |
| `:core:ui` | Android library | Shared Compose pieces: theme, spacing, `LoadingView`, `ErrorView`, `MoviePoster`. |
| `:feature:movieslist` | Android library | The trending list screen, its ViewModel and navigation entry. |
| `:feature:moviedetail` | Android library | The detail screen, its ViewModel and navigation entry. |

### Key design decisions

- **Domain layer is UI- and framework-agnostic.** The repository is an interface in `:core:domain` and its implementation lives in `:core:data`, so the data source can change (for example, adding a Room cache for offline support) without touching the use cases or the screens.
- **`DataResult`** (`Success` / `Error`) is the single type the data layer returns. Failures are turned into user-friendly messages in the repository, so ViewModels never handle exceptions.
- **Use cases** hold the business rules. `GetTrendingMoviesUseCase` also owns the genre filtering and sorting logic, which keeps it unit-testable without Android.
- **Features don't know about each other.** Each feature module only exposes a `NavGraphBuilder` extension (for example `moviesListScreen(onMovieClick)`). `:app` connects them, so a new feature can be added by wiring one more line there.
- **Unidirectional data flow.** Each ViewModel exposes one `StateFlow<UiState>`. The screen renders that state and sends user actions back as ViewModel function calls.
- **Dependency injection with Hilt.** Interfaces are bound in `DataModule`, network objects are provided in `NetworkModule`, and ViewModels use `@HiltViewModel`.

### Data flow

```
Screen ──▶ ViewModel ──▶ UseCase ──▶ MovieRepository (interface)
                                            │
                                            ▼
                          MovieRepositoryImpl ──▶ TmdbApiService (Retrofit)
                                            │
                          DTOs ◀────────────┘  mapped to domain models
```

## Tools and libraries

| Area | Tool |
|---|---|
| Language | Kotlin 2.4 |
| UI | Jetpack Compose, Material 3 (Compose BOM 2026.09) |
| Navigation | Navigation Compose |
| Architecture | MVVM, Clean Architecture, `ViewModel` + `StateFlow` |
| Async | Kotlin Coroutines and Flow |
| Dependency injection | Hilt (with KSP) |
| Networking | Retrofit, OkHttp (with a logging interceptor in debug builds) |
| Serialization | kotlinx.serialization |
| Images | Coil |
| Testing | JUnit 4, kotlinx-coroutines-test, Turbine |
| Build | Gradle (Kotlin DSL) with a version catalog (`gradle/libs.versions.toml`), Android Gradle Plugin 9.4 |
| Static analysis | Android Lint |

SDK levels: min 24, compile and target 37.

## Testing

Unit tests cover the logic-heavy classes:

- **ViewModels**: `MoviesListViewModel` (loading, genre filter, sorting, retry, partial failures) and `MovieDetailViewModel` (loading, errors, retry, navigation arguments).
- **Use cases**: filtering and every sort field in both directions.
- **Repository**: page fetching, de-duplication and error-to-message mapping, using a fake `TmdbApiService`.
- **Mappers**: DTO-to-domain conversion.

Run them with `./gradlew testDebugUnitTest`

