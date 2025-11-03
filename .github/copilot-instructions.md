# Copilot Instructions for MCProject

## Project Overview
"MC Project" is a MultiversX blockchain transaction viewer built as a single-module Android application with Kotlin, targeting Android API 24-36. The app displays blockchain data from MultiversX DevNet, including account information and transaction history. Package namespace: `com.vti.mcproject`.

**Architecture**: MVVM (Model-View-ViewModel) with Jetpack Compose UI and manual dependency injection.

## Architecture & Key Components

### Application Structure
- **Main Activity**: `MainActivity.kt` - Compose-based single activity:
  - Uses `ComponentActivity` with `setContent { }`
  - Displays `TransactionsScreen` as the main UI
  - Edge-to-edge layout with Material3 theming
  - No XML layouts, no fragments, no ViewBinding

- **UI Layer (Jetpack Compose)**:
  - **TransactionsScreen** (`ui/screens/TransactionsScreen.kt`) - Main screen displaying transactions
  - **DetailedTransactionScreen** (`ui/screens/DetailedTransactionScreen.kt`) - Detailed transaction view
  - **TransactionsListScreen** (`ui/screens/TransactionsListScreen.kt`) - Alternative transactions list screen
  - All UI is declarative using Composable functions
  - Material3 components (Scaffold, TopAppBar, LazyColumn, etc.)

- **ViewModel Layer**:
  - **TransactionsViewModel** (`ui/viewmodel/TransactionsViewModel.kt`) - Manages transaction and account state
  - **DetailedTransactionViewModel** (`ui/viewmodel/DetailedTransactionViewModel.kt`) - Manages detailed view state
  - Uses `StateFlow` for reactive state management
  - Manual dependency injection via `viewModel { }` factory lambda

- **Data Layer**:
  - **Repositories**:
    - `TransactionRepository` (`data/repository/TransactionRepository.kt`) - Provides mock transaction data
    - `AccountInfoRepository` (`data/repository/AccountInfoRepository.kt`) - Provides mock account data
  - **Models**:
    - `Transaction` (`data/model/Transaction.kt`) - Transaction data class
    - `AccountInfo` (`data/model/AccountInfo.kt`) - Account information data class
  - **Network**: `data/network/` folder (currently empty - prepared for future API integration)

- **Blockchain Integration**:
  - **MultiversXSdkService** (`blockchain/MultiversXSdkService.kt`) - Service layer for blockchain operations
    - Uses erdkotlin SDK (v0.4.0) for account operations
    - Direct HTTP calls to MultiversX API for transaction fetching
    - Connects to DevNet: `https://devnet-api.multiversx.com`
    - Contract address: `erd1qqqqqqqqqqqqqpgquvpnteagc5xsslc3yc9hf6um6n6jjgzdd8ss07v9ma`

- **Legacy Code (Commented Out)**:
  - `SecondFragment.kt` - Old fragment-based UI (commented out)
  - `TransactionAdapter.kt` - Old RecyclerView adapter (commented out)
  - XML layouts in `res/layout/` - Legacy layouts (still present but unused)

### Build Configuration
- **Gradle Kotlin DSL**: All build scripts use `.kts` format
- **Version Catalog**: Dependencies managed via `gradle/libs.versions.toml` with type-safe accessors
- **Build Features**:
  - Compose: `true`
  - ViewBinding: `true` (legacy support, not actively used)
  - MinifyEnabled (release): `false`
  - ProGuard rules: `proguard-rules.pro`
- **Java/Kotlin Compatibility**: Java 17 (source/target), Kotlin JVM target 17
- **SDK Targets**: minSdk 24, targetSdk/compileSdk 36
- **Kotlin Version**: 2.0.21

### Core Dependencies (via Version Catalog)
- **AndroidX**: Core KTX 1.17.0, AppCompat 1.7.1, ConstraintLayout 2.2.1
- **Material Design**: Material Components 1.13.0
- **Navigation**: Navigation Fragment KTX & UI KTX 2.9.5 (legacy), Navigation Compose 2.8.5
- **Lifecycle**: ViewModel KTX & LiveData KTX 2.8.7, ViewModel Compose, Runtime Compose
- **Coroutines**: Kotlinx Coroutines 1.9.0 (Android + Core)
- **Jetpack Compose**: 
  - Compose BOM 2024.10.01
  - Material3
  - UI, UI Graphics, UI Tooling
  - Activity Compose 1.9.3
- **Network**: 
  - OkHttp 4.12.0 (+ Logging Interceptor)
  - Retrofit 2.11.0 (+ Moshi Converter)
  - Moshi 1.15.1 (+ Kotlin support)
  - Gson 2.10.1
- **Blockchain**: 
  - erdkotlin 0.4.0 (local JAR in `app/libs/`)
  - BitcoinJ Core 0.16.3 (for Bech32 address encoding)
- **Testing**: JUnit 4.13.2, AndroidX Test (JUnit 1.3.0, Espresso 3.7.0)

## Developer Workflows

### Building & Running
- **Build**: `./gradlew build` (Windows: `gradlew.bat build`)
- **Clean Build**: `./gradlew clean build`
- **Install Debug APK**: `./gradlew installDebug`
- **Run/Debug**: Use Android Studio's run configurations or `./gradlew installDebug` + manual launch

### Testing
- **Unit Tests**: Located in `app/src/test/java/`
  - Run: `./gradlew test` or `./gradlew testDebugUnitTest`
- **Instrumented Tests**: Located in `app/src/androidTest/java/`
  - Run: `./gradlew connectedAndroidTest` (requires connected device/emulator)
- **Test Runner**: AndroidX Test with JUnit 4

### Dependency Management
- **Version Catalog**: All versions defined in `gradle/libs.versions.toml`
- **Access Pattern**: `libs.androidx.core.ktx`, `libs.plugins.kotlin.android`
- **Local JARs**: erdkotlin SDK stored in `app/libs/erdkotlin-0.4.0.jar`
- **Repository Management**: Centralized in `settings.gradle.kts` with `FAIL_ON_PROJECT_REPOS` mode

## Project-Specific Patterns & Conventions

### MultiversX Blockchain Integration
**Service Layer** (`MultiversXSdkService.kt`):
- Uses erdkotlin SDK for account operations (balance, nonce)
- Direct OkHttp calls for transactions (SDK endpoint is outdated)
- API Endpoint: `https://devnet-api.multiversx.com/accounts/{address}/transfers`
- Error handling: Returns empty lists on API errors for better UX
- Logging: HTTP interceptor logs all requests/responses

**Data Models**:
```kotlin
data class Transaction(
    val hash: String,
    val sender: String,
    val receiver: String,
    val value: String,      // In EGLD
    val timestamp: Long,
    val status: String,
    val fee: String,
    val data: String,
    val gasUsed: Long,
    val gasLimit: Long
)

data class AccountInfo(
    val address: String,
    val balance: String,    // In EGLD
    val nonce: Long
)
```

**API Integration**:
- Always use `withContext(Dispatchers.IO)` for network calls
- Wrap results in `Result<T>` for error handling
- Convert wei to EGLD using `BigInteger` division by 10^18
- Address validation via `Address.fromBech32()`

### Compose UI Pattern (CURRENT)
All UI components use Jetpack Compose. When creating new screens:
```kotlin
@Composable
fun MyScreen(
    viewModel: MyViewModel = viewModel {
        MyViewModel(
            repository = MyRepository()
        )
    }
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = { TopAppBar(...) }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            // Content
        }
    }
}
```

### Manual Dependency Injection Pattern
**Repository Instantiation**:
```kotlin
// In Composable screens
viewModel: TransactionsViewModel = viewModel {
    TransactionsViewModel(
        transactionRepository = TransactionRepository(),
        accountInfoRepository = AccountInfoRepository()
    )
}
```

**ViewModel Pattern**:
```kotlin
class MyViewModel(
    private val repository: MyRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MyUiState())
    val uiState: StateFlow<MyUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    fun loadData() {
        viewModelScope.launch {
            // Load data
        }
    }
}
```

### Legacy ViewBinding Pattern (Deprecated - Not in Use)
Old fragments used ViewBinding (now commented out). For reference:
```kotlin
// Fragment pattern (DEPRECATED - DO NOT USE)
private var _binding: FragmentXBinding? = null
private val binding get() = _binding!!

override fun onCreateView(...): View {
    _binding = FragmentXBinding.inflate(inflater, container, false)
    return binding.root
}

override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
}
```

### Code Organization
- **Package Structure**: 
  - `com.vti.mcproject` - Main activities and application class
  - `com.vti.mcproject.blockchain` - Blockchain integration layer
  - `com.vti.mcproject.data.model` - Data classes
  - `com.vti.mcproject.data.repository` - Repository implementations
  - `com.vti.mcproject.data.network` - Network layer (empty, prepared for future)
  - `com.vti.mcproject.ui.screens` - Compose screens
  - `com.vti.mcproject.ui.viewmodel` - ViewModels
  - `com.vti.mcproject.utils` - Utility classes (currently empty)
  - `com.vti.mcproject.di` - Dependency injection (currently empty)
- **File Naming**: PascalCase for Kotlin classes, match purpose (e.g., `TransactionsScreen`, `TransactionsViewModel`)
- **Kotlin Style**: Follow standard Kotlin conventions, JVM target 17
- **State Management**: Use `StateFlow` in ViewModels, collect as state in Composables

### Build & ProGuard
- Release builds: No minification/obfuscation (disabled)
- ProGuard rules in `proguard-rules.pro` (if needed for future releases)
- Backup rules: `@xml/backup_rules`, `@xml/data_extraction_rules`

## Integration Points
- **MultiversX DevNet API**: REST API at `https://devnet-api.multiversx.com`
  - Account endpoint: `/accounts/{address}`
  - Transfers endpoint: `/accounts/{address}/transfers?from=0&size=25`
- **erdkotlin SDK**: Local JAR for address validation and account operations
- **Network Security**: Internet permission enabled in `AndroidManifest.xml`
- **No local database**: All data currently uses mock repositories (prepared for future API integration)

## Examples
- To build the app: `./gradlew build` (Windows: `gradlew.bat build`)
- To run unit tests: `./gradlew test`
- To run instrumented tests: `./gradlew connectedAndroidTest`
- To add a new Compose screen: Create `@Composable` function in `ui/screens/`, add ViewModel in `ui/viewmodel/`
- To add blockchain service method: Add suspend function in `MultiversXSdkService`, wrap in `withContext(Dispatchers.IO)`, return `Result<T>`
- To display new data: Create ViewModel with `StateFlow`, collect state in Composable with `collectAsState()`

## Key Files & Directories
- `app/src/main/java/com/vti/mcproject/` — Main source code
  - `MainActivity.kt` — Compose-based main activity
  - `MCProjectApplication.kt` — Application class
  - `SecondFragment.kt` — Legacy fragment (commented out)
  - `TransactionAdapter.kt` — Legacy adapter (commented out)
- `app/src/main/java/com/vti/mcproject/blockchain/` — Blockchain integration
  - `MultiversXSdkService.kt` — Blockchain service layer
- `app/src/main/java/com/vti/mcproject/data/` — Data layer
  - `model/` — Data classes (Transaction, AccountInfo)
  - `repository/` — Repository implementations (mock data)
  - `network/` — Network layer (empty, prepared for future)
- `app/src/main/java/com/vti/mcproject/ui/` — UI layer
  - `screens/` — Compose screens (TransactionsScreen, DetailedTransactionScreen, TransactionsListScreen)
  - `viewmodel/` — ViewModels (TransactionsViewModel, DetailedTransactionViewModel)
- `app/src/main/java/com/vti/mcproject/utils/` — Utilities (empty)
- `app/src/main/java/com/vti/mcproject/di/` — Dependency injection (empty)
- `app/src/main/res/layout/` — Legacy XML layouts (unused)
- `app/src/main/res/navigation/nav_graph.xml` — Legacy navigation graph (unused)
- `app/src/main/res/values/strings.xml` — String resources
- `app/src/main/AndroidManifest.xml` — App manifest with internet permissions
- `app/libs/erdkotlin-0.4.0.jar` — MultiversX Kotlin SDK
- `app/build.gradle.kts` — App module build config
- `build.gradle.kts` — Root build config
- `gradle/libs.versions.toml` — Dependency versions

## Project Conventions

- **Naming**: PascalCase for Kotlin classes/fragments, camelCase for variables/functions, snake_case for XML resources
- **Commit Messages**: Use conventional format (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`)
- **Resource IDs**: Prefix with type (e.g., `button_submit`, `text_title`, `fragment_first`)

## ✅ Always Do

### Compose UI & MVVM
- **Always use Jetpack Compose** for new UI components (no XML layouts)
- **Always use ViewModels** for business logic and state management
- **Always use StateFlow** for observable state in ViewModels
- **Always collect state as State** in Composables using `collectAsState()`
- **Always scope coroutines to viewModelScope** in ViewModels
- **Always use manual dependency injection** with `viewModel { ViewModel(deps) }` factory
- **Always create repositories** to abstract data access from ViewModels
- **Always use Material3 components** (Scaffold, TopAppBar, LazyColumn, Card, etc.)
- **Always use `@Composable` functions** for UI components
- **Always provide preview functions** with `@Preview` annotation for Compose components

### Blockchain Integration
- **Always use `withContext(Dispatchers.IO)`** for network and blockchain operations
- **Always wrap blockchain calls in `Result<T>`** for proper error handling
- **Always log blockchain operations** using `Log.d/e(TAG, message)`
- **Always validate addresses** using `Address.fromBech32()` before API calls
- **Always convert wei to EGLD** using `BigInteger.TEN.pow(18)` division
- **Always handle API errors gracefully** - return empty lists or show user-friendly messages
- **Always add internet permission** when making network calls

### State Management
- **Always use StateFlow for ViewModel state** - `MutableStateFlow` internally, expose as `StateFlow`
- **Always initialize state in ViewModel init block** when appropriate
- **Always update state immutably** using `.update { }` or creating new instances
- **Always expose only immutable state** to UI (use `.asStateFlow()`)
- **Always handle loading/success/error states** appropriately in UI

### Code Quality
- **Always use coroutines for async operations** - `viewModelScope.launch`
- **Always use `val` over `var`** when possible - prefer immutability
- **Always use data classes** for models that hold data
- **Always add kdoc comments** for public classes and non-obvious functions
- **Always use `when` expressions** instead of long if-else chains
- **Always use sealed classes** for state management (e.g., `sealed class UiState<T>`)
- **Always follow Kotlin naming conventions** - PascalCase for classes, camelCase for functions/variables

### Dependencies & Build
- **Always add dependencies to `libs.versions.toml`** (never directly in `build.gradle.kts`)
- **Always reference dependencies via version catalog** using `libs.` prefix
- **Always place local JARs in `app/libs/`** directory
- **Always include transitive dependencies** for local JARs (e.g., OkHttp for erdkotlin)
- **Always run `./gradlew build`** after changing dependencies or build configuration
- **Always update both version and library** entries when upgrading dependencies
- **Always use Compose Compiler plugin** for Jetpack Compose projects
- **Always set `compose = true`** in buildFeatures when using Compose

### Testing
- **Always write unit tests** for business logic in `app/src/test/java/`
- **Always write UI tests** for critical user flows in `app/src/androidTest/java/`
- **Always run tests before committing** with `./gradlew test`
- **Always test network error scenarios** in blockchain integration
- **Always test ViewModel state changes** and coroutine behavior

### Resources
- **Always use string resources** from `res/values/strings.xml` (never hardcode user-facing text)
- **Always use dimension resources** for margins, padding, text sizes when applicable
- **Always support dark theme** with `values-night/` resources or Material3 dynamic theming
- **Always use vector drawables** (XML) instead of PNGs when possible
- **Always set content descriptions** for accessibility

## ❌ Never Do

### Compose UI & Architecture
- **Never use XML layouts for new UI** - use Jetpack Compose instead
- **Never use fragments for new screens** - use Composable functions
- **Never use ViewBinding in new code** - it's only kept for legacy support
- **Never use findViewById()** - project has migrated to Compose
- **Never use Kotlin synthetics** (`import kotlinx.android.synthetic.*`) - deprecated
- **Never access mutable state directly in Composables** - use StateFlow/State
- **Never perform heavy operations in Composable functions** - use ViewModels

### Blockchain Integration
- **Never make blockchain calls on the main thread** - always use `Dispatchers.IO`
- **Never hardcode API URLs** - use network configuration constants
- **Never expose private keys or sensitive data** in logs or code
- **Never ignore API errors** - always handle and log them
- **Never use deprecated API endpoints** - check MultiversX API documentation
- **Never block UI** while fetching blockchain data - show loading indicators

### State Management & ViewModels
- **Never expose MutableStateFlow to UI** - always use `.asStateFlow()`
- **Never update state from multiple threads without synchronization**
- **Never hold Activity/Fragment context in ViewModel** - causes memory leaks
- **Never perform UI operations in ViewModel** - ViewModels are UI-agnostic
- **Never forget to collect StateFlow in Composables** - state won't update

### Navigation & Dependency Injection
- **Never use FragmentManager** - project uses Compose Navigation or single screen
- **Never use Hilt/Dagger** - project uses manual dependency injection
- **Never create singletons for repositories** - instantiate in ViewModel factory
- **Never pass large objects via navigation arguments** - use ViewModel or repository

### Dependencies & Build
- **Never add dependencies directly to `build.gradle.kts`** - always use version catalog
- **Never hardcode version numbers** in build files
- **Never commit `local.properties`** - it contains machine-specific paths
- **Never modify generated code** in `app/build/` - it will be overwritten
- **Never forget transitive dependencies** for local JARs
- **Never disable Compose plugin** if using Jetpack Compose

### Code Quality
- **Never use raw types** - always specify generic types
- **Never use `var` when `val` is sufficient** - prefer immutability
- **Never suppress warnings without a comment** explaining why
- **Never use global state** unless absolutely necessary
- **Never block the main thread** - use background threads/coroutines for long operations
- **Never catch generic `Exception`** without logging - catch specific exceptions
- **Never use `!!` (not-null assertion)** without understanding why it's safe

### Resources
- **Never hardcode strings in code** - use `R.string.*` resources
- **Never hardcode colors in code or layouts** - define in `colors.xml` or use Material3 theme
- **Never use absolute sizes** - use `dp` for dimensions, `sp` for text
- **Never ignore accessibility** - always provide content descriptions and touch targets ≥48dp

### Android Lifecycle
- **Never hold Activity context longer than lifecycle** - causes memory leaks
- **Never reference Activity from static fields** or long-lived objects
- **Never perform heavy work in `onCreate()`** - use lazy initialization or background threads
- **Never forget to cancel coroutines** - use `viewModelScope` for automatic cancellation

## 🔍 Code Review Checklist

Before submitting code, verify:
- [ ] All new UI uses Jetpack Compose (no XML layouts)
- [ ] ViewModels manage business logic and expose StateFlow
- [ ] UI observes state via `collectAsState()`
- [ ] No hardcoded strings, colors, or dimensions
- [ ] Blockchain calls use `Dispatchers.IO` and `Result<T>`
- [ ] Network errors handled gracefully with user feedback
- [ ] Dependencies added to `libs.versions.toml`
- [ ] Tests pass (`./gradlew test`)
- [ ] No new lint warnings
- [ ] Follows project naming conventions (PascalCase for classes, camelCase for functions)
- [ ] Commit message follows conventional format
- [ ] Internet permission added if making network calls
- [ ] Coroutines properly scoped to `viewModelScope`
- [ ] Manual dependency injection used (no Hilt/Dagger)
- [ ] State updates are immutable
- [ ] No memory leaks (ViewModel properly scoped)
