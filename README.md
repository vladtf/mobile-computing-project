# MC Project

An Android application built with Kotlin demonstrating fragment-based navigation using Jetpack components.

## Overview

Single-module Android app showcasing:
- Single Activity with fragment navigation
- ViewBinding for type-safe view access
- Material Design components
- Jetpack Navigation Component

## Tech Stack

- Kotlin 2.0.21
- Android SDK 24-36
- Jetpack Navigation 2.9.5
- Material Design 1.13.0
- ViewBinding

## Getting Started

### Prerequisites
- JDK 11+
- Android Studio Ladybug or newer
- Android device/emulator (API 24+)

### Build & Run

```bash
# Build
gradlew.bat build        # Windows
./gradlew build          # Linux/Mac

# Install
gradlew.bat installDebug
./gradlew installDebug
```

## Testing

```bash
# Unit tests
./gradlew test

# Instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest
```

## Project Structure

```
app/src/main/
├── java/com/vti/mcproject/
│   ├── MainActivity.kt
│   ├── FirstFragment.kt
│   └── SecondFragment.kt
├── res/
│   ├── layout/
│   ├── navigation/nav_graph.xml
│   └── ...
└── AndroidManifest.xml
```

## Key Features

TODO

## Architecture

TODO
