# Copilot Instructions for MCProject

## Project Overview
"MC Project" is a single-module Android application built with Kotlin, targeting Android API 24-36. The app uses Jetpack Navigation with View Binding for a fragment-based UI architecture. Package namespace: `com.vti.mcproject`.

## Architecture & Key Components

### Application Structure
- **Main Activity**: `MainActivity.kt` serves as the single-activity container with:
  - Material Design AppBar and Toolbar
  - FloatingActionButton (FAB) with Snackbar integration
  - Navigation component host (`nav_host_fragment_content_main`)
  - AppBarConfiguration for navigation UI integration

- **Navigation Flow**: Fragment-based navigation using Jetpack Navigation Component:
  - `FirstFragment` ↔ `SecondFragment` (bidirectional navigation)
  - Navigation graph defined in `res/navigation/nav_graph.xml`
  - Uses safe navigation actions (`action_FirstFragment_to_SecondFragment`, etc.)

- **View Binding**: Enabled project-wide in `build.gradle.kts`
  - All fragments use ViewBinding pattern with nullable backing property (`_binding`)
  - Binding cleanup in `onDestroyView()` to prevent memory leaks
  - Example: `FragmentFirstBinding`, `ActivityMainBinding`

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
- **AndroidX**: Core KTX, AppCompat, ConstraintLayout
- **Material Design**: Material Components (`com.google.android.material`)
- **Navigation**: Navigation Fragment KTX & UI KTX (v2.9.5)
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
- **Repository Management**: Centralized in `settings.gradle.kts` with `FAIL_ON_PROJECT_REPOS` mode

## Project-Specific Patterns & Conventions

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
- Use Navigation Component with Safe Args
- Navigate via: `findNavController().navigate(R.id.action_SourceFragment_to_DestFragment)`
- Define actions in `res/navigation/nav_graph.xml`
- MainActivity handles up navigation via `AppBarConfiguration`

### Code Organization
- **Package Structure**: Single-level `com.vti.mcproject` (no sub-packages currently)
- **File Naming**: Fragment classes match their layout files (e.g., `FirstFragment` → `fragment_first.xml`)
- **Kotlin Style**: Follow standard Kotlin conventions, JVM target 11

### Build & ProGuard
- Release builds: No minification/obfuscation (disabled)
- ProGuard rules in `proguard-rules.pro` (if needed for future releases)
- Backup rules: `@xml/backup_rules`, `@xml/data_extraction_rules`

## Integration Points
- **External Dependencies**: Managed via Gradle and `libs.versions.toml`.
- **No detected API integrations or service boundaries**: Update this section if you add network, database, or other integrations.

## Examples
- To build the app: `./gradlew build`
- To run unit tests: `./gradlew test`
- To run instrumented tests: `./gradlew connectedAndroidTest`
- To add a new fragment: Create Fragment class + layout XML + add to `nav_graph.xml` with actions

## Key Files & Directories
- `app/src/main/java/com/vti/mcproject/` — Main source code (MainActivity, Fragments)
- `app/src/main/res/layout/` — UI layouts (activities, fragments)
- `app/src/main/res/navigation/nav_graph.xml` — Navigation graph
- `app/src/main/res/menu/menu_main.xml` — App menu
- `app/src/main/AndroidManifest.xml` — App manifest
- `app/build.gradle.kts` — App module build config
- `build.gradle.kts` — Root build config
- `gradle/libs.versions.toml` — Dependency versions

## Project Conventions

- **Naming**: PascalCase for Kotlin classes/fragments, camelCase for variables/functions, snake_case for XML resources
- **Commit Messages**: Use conventional format (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`)
- **Resource IDs**: Prefix with type (e.g., `button_submit`, `text_title`, `fragment_first`)

## ✅ Always Do

### ViewBinding & UI
- **Always null out ViewBinding in `onDestroyView()`** to prevent memory leaks
- **Always use ViewBinding** instead of `findViewById()` or synthetic views
- **Always use ConstraintLayout** for complex layouts (project standard)
- **Always use Material Design components** (e.g., `MaterialButton`, not `Button`)
- **Always set content descriptions** for accessibility on ImageViews and interactive elements

### Navigation
- **Always use Navigation Component** for fragment transactions (never use FragmentManager directly)
- **Always define navigation actions in `nav_graph.xml`** before using them
- **Always use Safe Args** when passing data between fragments
- **Always handle up navigation** through `AppBarConfiguration` in MainActivity

### Dependencies & Build
- **Always add dependencies to `libs.versions.toml`** (never directly in `build.gradle.kts`)
- **Always reference dependencies via version catalog** using `libs.` prefix
- **Always run `./gradlew build`** after changing dependencies or build configuration
- **Always update both version and library** entries when upgrading dependencies

### Code Quality
- **Always use `lateinit` for non-nullable properties** initialized in `onCreate()`/`onCreateView()`
- **Always use nullable types with safe calls** (`?.`) or explicit null checks
- **Always use Kotlin coroutines** for asynchronous operations (when needed)
- **Always add kdoc comments** for public classes and non-obvious functions
- **Always use `when` expressions** instead of long if-else chains
- **Always use data classes** for models that hold data

### Testing
- **Always write unit tests** for business logic in `app/src/test/java/`
- **Always write UI tests** for critical user flows in `app/src/androidTest/java/`
- **Always run tests before committing** with `./gradlew test`

### Resources
- **Always use string resources** from `res/values/strings.xml` (never hardcode user-facing text)
- **Always use dimension resources** for margins, padding, text sizes
- **Always support dark theme** with `values-night/` resources
- **Always use vector drawables** (XML) instead of PNGs when possible

## ❌ Never Do

### ViewBinding & UI
- **Never use `findViewById()`** - ViewBinding is enabled project-wide
- **Never use Kotlin synthetics** (`import kotlinx.android.synthetic.*`) - deprecated
- **Never access views before `onCreateView()`** or after `onDestroyView()`
- **Never keep references to views** outside the view lifecycle (use binding pattern)
- **Never use `!!` (not-null assertion)** without understanding why it's safe - prefer safe calls or explicit null checks

### Navigation
- **Never use `FragmentManager.beginTransaction()`** - use Navigation Component instead
- **Never navigate with hardcoded fragment instances** - use navigation actions
- **Never pass large objects via Bundle** - use ViewModel or pass IDs and fetch data
- **Never mix navigation patterns** - stick to Navigation Component throughout

### Dependencies & Build
- **Never add dependencies directly to `build.gradle.kts`** - always use version catalog
- **Never hardcode version numbers** in build files
- **Never commit `local.properties`** - it contains machine-specific paths
- **Never modify generated code** in `app/build/` - it will be overwritten

### Code Quality
- **Never use raw types** - always specify generic types
- **Never use `var` when `val` is sufficient** - prefer immutability
- **Never suppress warnings without a comment** explaining why
- **Never use global state or singletons** unless absolutely necessary
- **Never block the main thread** - use background threads/coroutines for long operations
- **Never catch generic `Exception`** - catch specific exceptions

### Resources
- **Never hardcode strings in code** - use `R.string.*` resources
- **Never hardcode colors in code or layouts** - define in `colors.xml`
- **Never use absolute sizes** - use `dp` for dimensions, `sp` for text
- **Never ignore accessibility** - always provide content descriptions and touch targets ≥48dp

### Android Lifecycle
- **Never hold Activity/Fragment context longer than lifecycle** - causes memory leaks
- **Never reference Activity from static fields** or long-lived objects
- **Never perform heavy work in `onCreate()`** - use lazy initialization or background threads

## 🔍 Code Review Checklist

Before submitting code, verify:
- [ ] ViewBinding nulled out in `onDestroyView()`
- [ ] No hardcoded strings, colors, or dimensions
- [ ] Navigation uses defined actions from `nav_graph.xml`
- [ ] Dependencies added to `libs.versions.toml`
- [ ] Tests pass (`./gradlew test`)
- [ ] No new lint warnings
- [ ] Follows project naming conventions
- [ ] Commit message follows conventional format
