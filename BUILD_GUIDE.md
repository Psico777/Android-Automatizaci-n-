# Build and Installation Guide

## Prerequisites

1. **Android Studio** - Download from https://developer.android.com/studio
2. **JDK 8 or higher** - Usually bundled with Android Studio
3. **Android SDK** - Install through Android Studio SDK Manager
   - Minimum SDK: API 24 (Android 7.0)
   - Target SDK: API 34 (Android 14)

## Building the Project

### Method 1: Using Android Studio

1. Open Android Studio
2. Click "Open an Existing Project"
3. Navigate to the project directory and select it
4. Wait for Gradle sync to complete
5. Build the project:
   - Menu: Build → Make Project
   - Or press: Ctrl+F9 (Windows/Linux) or Cmd+F9 (Mac)

### Method 2: Using Command Line

```bash
# On Linux/Mac
./gradlew assembleDebug

# On Windows
gradlew.bat assembleDebug
```

The APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

## Installing on Device

### Using Android Studio

1. Connect your Android device via USB (enable USB debugging in Developer Options)
2. Click the "Run" button (green triangle) or press Shift+F10
3. Select your device from the list

### Using ADB

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## First Time Setup

After installing the app, you need to grant permissions:

### 1. Enable Accessibility Service

1. Open the app
2. Tap "Habilitar Servicio" button
3. In the Accessibility Settings, find "Gesture Recorder"
4. Toggle it ON
5. Confirm the permission dialog

### 2. Grant Overlay Permission

1. When prompted, tap "Allow" to grant overlay permission
2. If not prompted automatically:
   - Go to Settings → Apps → Gesture Recorder
   - Tap "Display over other apps"
   - Toggle it ON

## Testing the App

### Testing Gesture Recording

1. After enabling all permissions, floating buttons will appear on screen
2. Drag the buttons to a convenient location
3. Tap "Iniciar Grabación" to start recording
4. Perform some touch gestures on the screen
5. Tap "Detener Grabación" to stop
6. Tap "Reproducir Gestos" to replay your recorded gestures

### Testing Copy/Paste Functionality

1. Open any app with text (e.g., browser, notes)
2. Select some text
3. The AccessibilityService will automatically capture it
4. Open another app with a text field
5. The captured text is available in your clipboard

## Troubleshooting

### App Crashes on Launch

- Check that you're using Android 7.0 (API 24) or higher
- Verify that all permissions are granted

### Overlay Buttons Not Appearing

- Ensure Accessibility Service is enabled
- Check that overlay permission is granted
- Try restarting the app

### Gestures Not Recording

- Verify the Accessibility Service is running
- Make sure you pressed "Iniciar Grabación" button
- Check system settings for any accessibility restrictions

### Gestures Not Playing Back

- Ensure Android version is 7.0 or higher (gesture dispatch requires API 24+)
- Check that gestures were successfully recorded
- Try recording simpler gestures first (single taps)

## Building for Release

1. Generate a signing key:
```bash
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-alias
```

2. Update `app/build.gradle.kts` with signing config:
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("path/to/my-release-key.jks")
            storePassword = "your-password"
            keyAlias = "my-alias"
            keyPassword = "your-password"
        }
    }
    
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            // ... other config
        }
    }
}
```

3. Build release APK:
```bash
./gradlew assembleRelease
```

## Security Notes

- Never commit your signing keys to version control
- Store passwords securely (consider using environment variables)
- The app requires sensitive permissions - use responsibly
- Test thoroughly on different Android versions

## System Requirements for Development

- **OS**: Windows 10/11, macOS 10.14+, or Linux
- **RAM**: Minimum 8 GB (16 GB recommended)
- **Disk Space**: At least 10 GB free space
- **Screen Resolution**: 1280 x 800 minimum
