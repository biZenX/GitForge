# Contributing to GitForge

Thank you for your interest in contributing to **GitForge**! We welcome community contributions to make this the premier native Android GitHub client and local development workspace.

## Development Setup

1. **Prerequisites:**
   - JDK 21 (Eclipse Temurin or OpenJDK recommended)
   - Android SDK Platform 36 (Android 16 / Build-tools 36.x)
   - Git 2.30+

2. **Clone & Build:**
   ```bash
   git clone https://github.com/biZenX/GitForge.git
   cd GitForge
   ./gradlew assembleDebug
   ```

3. **Running Tests:**
   ```bash
   ./gradlew test
   ```

## Contribution Workflow

1. Fork the repository and create your feature branch:
   ```bash
   git checkout -b feature/amazing-feature
   ```
2. Follow Kotlin idiomatic guidelines and Jetpack Compose best practices.
3. Ensure all tests pass:
   ```bash
   ./gradlew test
   ```
4. Verify release assembly and linting:
   ```bash
   ./gradlew assembleRelease
   ```
5. Commit your changes with descriptive commit messages following Conventional Commits (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`).
6. Open a Pull Request with a clear description of the problem and proposed solution.

## Code Architecture Guidelines

- **Unidirectional Data Flow (UDF):** ViewModels expose state via Kotlin `StateFlow`. Composables never perform I/O or query network/database directly.
- **Layered Decoupling:** Keep domain logic clean and independent from Android platform frameworks wherever practical.
- **Security First:** Never hardcode credentials, bypass path-traversal safeguards, or write outside permitted app-managed storage sandboxes.
