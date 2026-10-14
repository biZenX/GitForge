# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |

## Reporting a Vulnerability

GitForge takes security and user privacy seriously:

- All OAuth credentials and Personal Access Tokens are encrypted on-device using the Android Keystore (`security-crypto` AES-256 GCM).
- Local workspaces and terminal execution operate strictly inside sandboxed app-managed directories (`context.filesDir/workspaces/...`).
- Path traversal attempts (`../`) are systematically rejected with `SecurityException`.

If you discover a potential security vulnerability or credential leak vector:

1. **Do NOT open a public GitHub issue.**
2. Send an email to `abdelrhmanBit@proton.me` with details, reproduction steps, and potential remediation.
3. We will acknowledge receipt within 48 hours and work with you to patch and coordinate responsible disclosure.
