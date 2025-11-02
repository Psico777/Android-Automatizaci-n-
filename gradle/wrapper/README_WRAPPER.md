# Gradle Wrapper JAR File Note

The `gradle-wrapper.jar` file is required for the Gradle wrapper to function but is not included in this repository due to build environment restrictions.

## How to Get the Wrapper JAR

### Option 1: Generate it locally
Run this command in the project root directory:
```bash
gradle wrapper --gradle-version 8.0
```

This will download and set up the complete Gradle wrapper, including `gradle-wrapper.jar`.

### Option 2: Use Android Studio
1. Open the project in Android Studio
2. Android Studio will automatically download the Gradle wrapper if it's missing
3. The IDE will handle Gradle synchronization automatically

### Option 3: Manual Download
Download the gradle-wrapper.jar for version 8.0 from:
https://services.gradle.org/distributions/gradle-8.0-bin.zip

Extract it and copy `lib/gradle-wrapper.jar` to `gradle/wrapper/gradle-wrapper.jar`

## Why is it Missing?

The wrapper JAR was not included because:
1. Network restrictions in the build environment prevented automatic download
2. Binary files are often excluded from version control
3. The wrapper is easily regenerated in a standard development environment

## What to Do

Before building the project, ensure you have the gradle-wrapper.jar by using one of the methods above.
