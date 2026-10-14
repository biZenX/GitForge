# GitForge Releases

## Release v1.0.0 (Initial Production Release)

**Release Date:** October 14, 2026  
**Artifact:** `releases/GitForge-v1.0.0.apk`  
**Size:** ~17 MB  
**SHA-256 Checksum:** `5d84f3139f207c8d49d0624afddaf2694b4918feca8c6f301b9f2553d9757422`

---

### Highlights

- **Native Architecture & Tech Stack:**
  - Built with Kotlin & Jetpack Compose using modern Android Material 3 Expressive guidelines.
  - Layered modular architecture: `:app`, `:core:model`, `:core:network`, `:core:database`, `:core:security`, `:core:designsystem`, `:feature:editor`, `:feature:git`, `:feature:terminal`.
  - Pure native Eclipse JGit engine (`6.10.0.202406032230-r`) with desugaring support.
  - Dependency Injection with Dagger Hilt 2.58.
  - Offline Room database caching with reactive Flow streams.
  - WorkManager 2.10 background workers with Foreground Service progress notifications.

- **GitHub Comprehensive Features:**
  - **Authentication:** Android Keystore AES-256 GCM encrypted token storage with CSRF-protected OAuth callback state handling.
  - **Repositories:** Full browsing of public & authorized private repositories, branches, commit trees, commit history, and README markdown rendering.
  - **Contents API:** Remote file viewing, editing, creating, and deleting with custom commit messages.
  - **Archive Downloads:** Direct background streaming ZIP archive downloading with cancellation and live percentage progress.
  - **Productivity Hub:** Unified screens for Issues, Pull Requests (with diff viewer & merge actions), GitHub Actions workflow runs, Notifications, and Releases.

- **Local Development Workspace & Studio:**
  - Sandboxed local workspaces in app-managed storage with strict path-traversal protection.
  - Syntax-highlighting code editor for Kotlin, Java, Python, JavaScript, and Markdown with line numbering, search, undo/redo, and unified diff view.
  - Local Git engine supporting clone, commit, branch switching, stage/unstage, and status checking.
  - Sandboxed terminal shell with built-in commands (`ls`, `cat`, `mkdir`, `rm`, `pwd`, `echo`, `env`, `help`, `clear`) and process management.

---

### Verification & Quality Assurance

- **Unit & Integration Tests:** 345 automated tasks passing across all modules (`./gradlew test`).
- **Lint Vital:** Zero lint errors on release target with proper foreground service type declarations.
- **Target SDK:** 36 (Android 16 preview / current platform), Min SDK: 26 (Android 8.0 Oreo).
