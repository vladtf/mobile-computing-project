# Copilot Instructions for MCProject

## Project Overview
"MC Project" is a MultiversX blockchain transaction viewer built as a single-module Android application with Kotlin, targeting Android API 24-36. The app displays real-time blockchain data from MultiversX DevNet, including account information and transaction history. Package namespace: `com.vti.mcproject`.

## Architecture & Key Components

### Application Structure
- **Main Activity**: `MainActivity.kt` serves as the single-activity container with:
  - Material Design AppBar and Toolbar
  - Hidden FloatingActionButton (not currently used)
  - Navigation component host (`nav_host_fragment_content_main`)
  - AppBarConfiguration for navigation UI integration

- **Navigation Flow**: Single-screen app using Jetpack Navigation Component:
  - `SecondFragment` (MultiversX Transactions) - Start destination
  - Navigation graph defined in `res/navigation/nav_graph.xml`
  - No navigation actions currently (single screen)

- **Blockchain Integration**:
  - **MultiversXSdkService** (`blockchain/MultiversXSdkService.kt`) - Service layer for blockchain operations
    - Uses erdkotlin SDK (v0.4.0) for account operations
    - Direct HTTP calls to MultiversX API for transaction fetching
    - Connects to DevNet: `https://devnet-api.multiversx.com`
    - Contract address: `erd1qqqqqqqqqqqqqpgquvpnteagc5xsslc3yc9hf6um6n6jjgzdd8ss07v9ma`
  - **SecondFragment** - Main UI displaying account info and transaction list
  - **TransactionAdapter** - RecyclerView adapter for transaction items with DiffUtil

- **View Binding**: Enabled project-wide in `build.gradle.kts`
  - All fragments use ViewBinding pattern with nullable backing property (`_binding`)
  - Binding cleanup in `onDestroyView()` to prevent memory leaks
  - Example: `FragmentSecondBinding`, `ActivityMainBinding`, `ItemTransactionBinding`

### Build Configuration
- **Gradle Kotlin DSL**: All build scripts use `.kts` format
- **Version Catalog**: Dependencies managed via `gradle/libs.versions.toml` with type-safe accessors
- **Build Features**:
  - ViewBinding: `true`
  - MinifyEnabled (release): `false`
  - ProGuard rules: `proguard-rules.pro`
- **Java/Kotlin Compatibility**: Java 11 (source/target), Kotlin JVM target 11
- **SDK Targets**: minSdk 24, targetSdk/compileSdk 36

### Core Dependencies (via Version Catalog)
- **AndroidX**: Core KTX 1.17.0, AppCompat 1.7.1, ConstraintLayout 2.2.1
- **Material Design**: Material Components 1.13.0
- **Navigation**: Navigation Fragment KTX & UI KTX 2.9.5
- **Lifecycle**: ViewModel KTX & LiveData KTX 2.8.7
- **Coroutines**: Kotlinx Coroutines 1.9.0 (Android + Core)
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

### ViewBinding Pattern (CRITICAL)
All UI components use ViewBinding. When creating new activities/fragments:
```kotlin
// Fragment pattern
private var _binding: FragmentXBinding? = null
private val binding get() = _binding!!

override fun onCreateView(...): View {
    _binding = FragmentXBinding.inflate(inflater, container, false)
    return binding.root
}

override fun onDestroyView() {
    super.onDestroyView()
    _binding = null  // ALWAYS null out to prevent leaks
}
```

### Navigation Pattern
- Single-screen app - `SecondFragment` is the start destination
- Navigation Component configured but no active navigation actions
- MainActivity handles toolbar setup and configuration

### Code Organization
- **Package Structure**: 
  - `com.vti.mcproject` - Main activities and fragments
  - `com.vti.mcproject.blockchain` - Blockchain integration layer
- **File Naming**: Fragment classes match their layout files (e.g., `SecondFragment` → `fragment_second.xml`)
- **Kotlin Style**: Follow standard Kotlin conventions, JVM target 11
- **Adapters**: Use `ListAdapter` with `DiffUtil.ItemCallback` for RecyclerViews

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
- **No local database**: All data fetched from API in real-time

## Examples
- To build the app: `./gradlew build`
- To run unit tests: `./gradlew test`
- To run instrumented tests: `./gradlew connectedAndroidTest`
- To add blockchain service method: Add suspend function in `MultiversXSdkService`, wrap in `withContext(Dispatchers.IO)`, return `Result<T>`
- To display new data: Update `SecondFragment` with LiveData/coroutine flow

## Key Files & Directories
- `app/src/main/java/com/vti/mcproject/` — Main source code (MainActivity, SecondFragment, TransactionAdapter)
- `app/src/main/java/com/vti/mcproject/blockchain/` — Blockchain integration (MultiversXSdkService)
- `app/src/main/res/layout/` — UI layouts (activity_main, fragment_second, item_transaction)
- `app/src/main/res/navigation/nav_graph.xml` — Navigation graph (single fragment)
- `app/src/main/res/values/strings.xml` — String resources (blockchain and transaction strings)
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

### Blockchain Integration
- **Always use `withContext(Dispatchers.IO)`** for network and blockchain operations
- **Always wrap blockchain calls in `Result<T>`** for proper error handling
- **Always log blockchain operations** using `Log.d/e(TAG, message)`
- **Always validate addresses** using `Address.fromBech32()` before API calls
- **Always convert wei to EGLD** using `BigInteger.TEN.pow(18)` division
- **Always handle API errors gracefully** - return empty lists or show user-friendly messages
- **Always add internet permission** when making network calls

### ViewBinding & UI
- **Always null out ViewBinding in `onDestroyView()`** to prevent memory leaks
- **Always use ViewBinding** instead of `findViewById()` or synthetic views
- **Always use ConstraintLayout** for complex layouts (project standard)
- **Always use Material Design components** (e.g., `MaterialButton`, not `Button`)
- **Always set content descriptions** for accessibility on ImageViews and interactive elements

### Navigation
- **Always use Navigation Component** for fragment transactions
- **Always define fragments in `nav_graph.xml`** before using them
- **Always set start destination** in navigation graph
- **Always handle toolbar configuration** in MainActivity

### Dependencies & Build
- **Always add dependencies to `libs.versions.toml`** (never directly in `build.gradle.kts`)
- **Always reference dependencies via version catalog** using `libs.` prefix
- **Always place local JARs in `app/libs/`** directory
- **Always include transitive dependencies** for local JARs (e.g., OkHttp for erdkotlin)
- **Always run `./gradlew build`** after changing dependencies or build configuration
- **Always update both version and library** entries when upgrading dependencies

### Code Quality
- **Always use coroutines for async operations** - `viewLifecycleOwner.lifecycleScope.launch`
- **Always use `lateinit` for non-nullable properties** initialized in `onCreate()`/`onCreateView()`
- **Always use nullable types with safe calls** (`?.`) or explicit null checks
- **Always use `ListAdapter` with `DiffUtil`** for RecyclerViews
- **Always add kdoc comments** for public classes and non-obvious functions
- **Always use `when` expressions** instead of long if-else chains
- **Always use data classes** for models that hold data
- **Always use sealed classes** for state management (e.g., `BlockchainState<T>`)

### Testing
- **Always write unit tests** for business logic in `app/src/test/java/`
- **Always write UI tests** for critical user flows in `app/src/androidTest/java/`
- **Always run tests before committing** with `./gradlew test`
- **Always test network error scenarios** in blockchain integration

### Resources
- **Always use string resources** from `res/values/strings.xml` (never hardcode user-facing text)
- **Always use dimension resources** for margins, padding, text sizes
- **Always support dark theme** with `values-night/` resources
- **Always use vector drawables** (XML) instead of PNGs when possible
- **Always set content descriptions** for accessibility

## ❌ Never Do

### Blockchain Integration
- **Never make blockchain calls on the main thread** - always use `Dispatchers.IO`
- **Never hardcode API URLs** - use network configuration constants
- **Never expose private keys or sensitive data** in logs or code
- **Never ignore API errors** - always handle and log them
- **Never use deprecated API endpoints** - check MultiversX API documentation
- **Never block UI** while fetching blockchain data - show loading indicators

### ViewBinding & UI
- **Never use `findViewById()`** - ViewBinding is enabled project-wide
- **Never use Kotlin synthetics** (`import kotlinx.android.synthetic.*`) - deprecated
- **Never access views before `onCreateView()`** or after `onDestroyView()`
- **Never keep references to views** outside the view lifecycle (use binding pattern)
- **Never use `!!` (not-null assertion)** without understanding why it's safe - prefer safe calls or explicit null checks

### Navigation
- **Never use `FragmentManager.beginTransaction()`** - use Navigation Component instead
- **Never navigate with hardcoded fragment instances** - define in navigation graph
- **Never pass large objects via Bundle** - use ViewModel or fetch from repository

### Dependencies & Build
- **Never add dependencies directly to `build.gradle.kts`** - always use version catalog
- **Never hardcode version numbers** in build files
- **Never commit `local.properties`** - it contains machine-specific paths
- **Never modify generated code** in `app/build/` - it will be overwritten
- **Never forget transitive dependencies** for local JARs

### Code Quality
- **Never use raw types** - always specify generic types
- **Never use `var` when `val` is sufficient** - prefer immutability
- **Never suppress warnings without a comment** explaining why
- **Never use global state or singletons** unless absolutely necessary (like MultiversXSdkService)
- **Never block the main thread** - use background threads/coroutines for long operations
- **Never catch generic `Exception`** without logging - catch specific exceptions
- **Never use `!!` (not-null assertion)** without understanding why it's safe

### Resources
- **Never hardcode strings in code** - use `R.string.*` resources
- **Never hardcode colors in code or layouts** - define in `colors.xml`
- **Never use absolute sizes** - use `dp` for dimensions, `sp` for text
- **Never ignore accessibility** - always provide content descriptions and touch targets ≥48dp

### Android Lifecycle
- **Never hold Activity/Fragment context longer than lifecycle** - causes memory leaks
- **Never reference Activity from static fields** or long-lived objects
- **Never perform heavy work in `onCreate()`** - use lazy initialization or background threads
- **Never forget to cancel coroutines** - use `viewLifecycleOwner.lifecycleScope`

## 🔍 Code Review Checklist

Before submitting code, verify:
- [ ] ViewBinding nulled out in `onDestroyView()`
- [ ] No hardcoded strings, colors, or dimensions
- [ ] Blockchain calls use `Dispatchers.IO` and `Result<T>`
- [ ] Network errors handled gracefully with user feedback
- [ ] Dependencies added to `libs.versions.toml`
- [ ] Tests pass (`./gradlew test`)
- [ ] No new lint warnings
- [ ] Follows project naming conventions
- [ ] Commit message follows conventional format
- [ ] Internet permission added if making network calls
- [ ] Coroutines properly scoped to lifecycle
