# Implementation Plan: Fix R8 Serialization Crash

The crash log indicates that the app fails to serialize/deserialize data classes in release builds. This is caused by R8 obfuscation renaming or removing fields in the domain models used by Firebase Realtime Database.

## User Review Required

> [!IMPORTANT]
> This change modifies `proguard-rules.pro`. These rules will ensure that data classes in the `uz.kmax.fizikatest.domain.models` package are preserved during the build process, preventing serialization errors in release versions.

## Proposed Changes

### [App Configuration]

#### [MODIFY] [proguard-rules.pro](file:///C:/Users/User/AndroidStudioProjects/FizikaTest/app/proguard-rules.pro)

Add rules to:
1. Keep all data models in the `uz.kmax.fizikatest.domain.models` package.
2. Preserve members of classes annotated with `@Keep`.
3. Keep the names of fields in these models so Firebase can map them to database keys.

## Verification Plan

### Manual Verification
1. Build the app in `release` mode (e.g., `./gradlew assembleRelease`).
2. Run the release build on a device or emulator.
3. Navigate to the screens that fetch data from Firebase (e.g., Splash screen, Test list, Content list).
4. Verify that the app no longer crashes and data is displayed correctly.
