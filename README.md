<p align="center">
  <h1 align="center">GitForge</h1>
  <p align="center"><strong>Native Android GitHub Client & Local Development Workspace</strong></p>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2026--36)-brightgreen?logo=android" alt="Platform" />
  <img src="https://img.shields.io/badge/Kotlin-2.2.20-7F52FF?logo=kotlin" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-BOM%202026.03-4285F4?logo=jetpackcompose" alt="Compose" />
  <img src="https://img.shields.io/badge/Material%203-Expressive-purple" alt="Material 3" />
  <img src="https://img.shields.io/badge/Git%20Engine-Eclipse%20JGit-orange?logo=git" alt="JGit" />
  <img src="https://img.shields.io/badge/License-GPLv3-blue.svg" alt="License: GPL v3" />
</p>

---

## 🌟 Overview

**GitForge** is an open-source, production-oriented native Android application that combines comprehensive GitHub management with an authentic, sandboxed local development workspace. 

Unlike web wrappers or static dashboards, GitForge runs entirely natively using **Jetpack Compose**, **Material 3 Expressive**, **Dagger Hilt**, a pure Java/Kotlin **JGit** engine, **Room** offline persistence, and **WorkManager** background workers.

---

## ✨ Features

### 🐙 Comprehensive GitHub Client
- **Secure Authentication:** Android Keystore AES-256 GCM token encryption with CSRF-protected OAuth browser flow.
- **Repository Management:** Explore public and authorized private repositories, branches, tags, full commit history, and README markdown rendering.
- **Contents API:** Remote file viewing, creation, direct editing, and file deletion with custom commit messages.
- **Repository ZIP Downloader:** Background streaming download worker with foreground service notifications, live progress percentages, and cancellation controls.
- **Productivity Suite:**
  - **Issues:** View, comment, filter states, and inspect issue metadata.
  - **Pull Requests:** Inspect PR states, review unified diffs, and merge directly.
  - **GitHub Actions:** Track workflow runs, job statuses, and execution timing.
  - **Notifications & Releases:** Real-time notification lists and release asset browsing.

### 💻 Code Studio & Local Workspace
- **Multi-Workspace Storage:** App-managed local directory sandboxing with strict canonical path-traversal prevention.
- **Syntax-Highlighting Editor:** Native token-based highlighting for **Kotlin**, **Java**, **Python**, **JavaScript**, **JSON**, and **Markdown**.
- **Developer Controls:** Line numbers, responsive dual-axis scrolling, find in file, unsaved change tracking, undo/discard, and side-by-side unified diff previews.
- **Storage Access Framework (SAF):** Import and export files seamlessly to and from external device storage.

### 🌿 Embedded Git Engine
- Powered by **Eclipse JGit** (`6.10.0` with JDK 21 desugaring).
- Local Git operations: **Clone**, **Commit**, **Branch creation & switching**, **Status inspection**, **Stage/Unstage**, **Diff generation**, **Fetch**, and **Pull**.

### ⌨️ Sandboxed Terminal
- **Dual-Mode Execution:** Native process runner combined with a deterministic suite of built-in commands (`ls`, `cat`, `touch`, `mkdir`, `rm`, `pwd`, `echo`, `env`, `help`, `clear`).
- **Security First:** Confined to workspace boundaries; prevents unauthorized access outside app-managed sandboxes.
- **Full Process Control:** Real-time standard output and standard error separation, exit code inspection, and instant execution cancellation.

---

## 🏗️ Architecture

GitForge follows strict **Clean Architecture** principles and **Unidirectional Data Flow (UDF)**:

```
├── app/                  # Main Application, Navigation Graph, ViewModels, WorkManager, Hilt DI
├── core/
│   ├── model/            # Pure Kotlin Domain & Entity Models
│   ├── designsystem/     # Material 3 Expressive Theme, Dynamic Color, Reusable Components
│   ├── network/          # Retrofit, OkHttp, Kotlinx Serialization, GitHub API Service
│   ├── database/         # Room Database, Type Converters, DAOs (Offline Cache)
│   └── security/         # Android Keystore AES-256 GCM Encryption, OAuth CSRF Protection
└── feature/
    ├── editor/           # Code Studio, Syntax Highlighting Engine, Diff Viewer
    ├── git/              # JGit Engine, Local Workspace Storage Manager
    └── terminal/         # Sandboxed Terminal Command Engine & Terminal Composable UI
```

---

## 🚀 Building & Running

### Prerequisites
- **JDK:** 21 (Temurin or OpenJDK recommended)
- **Android SDK:** API Platform 36, Build Tools 36.x
- **Gradle:** 8.13.2 (Wrapper included)

### Commands

```bash
# Clone the repository
git clone https://github.com/biZenX/GitForge.git
cd GitForge

# Run all unit tests across all modules
./gradlew test

# Assemble Debug APK
./gradlew assembleDebug

# Assemble Release APK (Optimized & Verified via Lint Vital)
./gradlew assembleRelease
```

The compiled release APK will be located at:
```
app/build/outputs/apk/release/app-release.apk
```

---

## 🔒 Security & Privacy

- **No Hardcoded Secrets:** Client secrets, API tokens, and credentials are never stored in source code, logs, or APK assets.
- **Cryptographic Storage:** Access tokens are stored using `EncryptedSharedPreferences` backed by the hardware Android Keystore.
- **Strict Sandbox Boundaries:** File operations and terminal executions validate canonical paths to prevent directory traversal exploits.

---

## 📜 License

This project is licensed under the **GNU General Public License v3.0 (GPLv3)**.  
See the [LICENSE](LICENSE) file for complete terms and copyleft guarantees.
