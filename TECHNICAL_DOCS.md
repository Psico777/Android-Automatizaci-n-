# Technical Implementation Documentation

## Architecture Overview

The app is built using Android's AccessibilityService framework to intercept and dispatch touch events, along with WindowManager for displaying floating controls.

### Core Components

1. **MainActivity.kt** - Entry point and permission management
2. **GestureRecorderService.kt** - AccessibilityService implementation
3. **GestureData.kt** - Data model for storing gesture information
4. **Layouts** - XML layouts for UI components

## Implementation Details

### 1. AccessibilityService (GestureRecorderService)

The service extends `AccessibilityService` and provides the core functionality:

#### Key Features:

**Overlay Controls Creation**
```kotlin
private fun createOverlayControls() {
    windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    overlayView = inflater.inflate(R.layout.overlay_controls, null)
    
    // Use TYPE_APPLICATION_OVERLAY for Android 8.0+
    val params = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        },
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
    )
    
    windowManager?.addView(overlayView, params)
}
```

**Gesture Recording**
- Gestures are stored as `GestureData` objects containing:
  - X coordinate
  - Y coordinate  
  - Action type (ACTION_DOWN, ACTION_MOVE, ACTION_UP)
  - Timestamp (relative to recording start)

**Gesture Playback**
```kotlin
private fun performGestureStroke(stroke: List<GestureData>) {
    val path = Path()
    val firstGesture = stroke.first()
    path.moveTo(firstGesture.x, firstGesture.y)
    
    for (i in 1 until stroke.size) {
        val gesture = stroke[i]
        path.lineTo(gesture.x, gesture.y)
    }
    
    val duration = (stroke.last().timestamp - stroke.first().timestamp).coerceAtLeast(10)
    
    val gestureBuilder = GestureDescription.Builder()
    val strokeDescription = GestureDescription.StrokeDescription(
        path, 0, duration, false
    )
    
    gestureBuilder.addStroke(strokeDescription)
    dispatchGesture(gestureBuilder.build(), null, null)
}
```

**Text Copy/Paste**
- Text selection events are captured via `TYPE_VIEW_TEXT_SELECTION_CHANGED`
- Selected text is automatically copied to clipboard
- Paste functionality finds editable nodes and uses `ACTION_SET_TEXT`

### 2. Permission Management (MainActivity)

The app requires two critical permissions:

#### Accessibility Permission
```kotlin
private fun isAccessibilityServiceEnabled(): Boolean {
    val expectedComponentName = "$packageName/${GestureRecorderService::class.java.name}"
    val enabledServices = Settings.Secure.getString(
        contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    )
    return enabledServices?.contains(expectedComponentName) == true
}
```

#### Overlay Permission
```kotlin
private fun checkOverlayPermission() {
    if (!Settings.canDrawOverlays(this)) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivity(intent)
    }
}
```

### 3. Data Model (GestureData)

Simple data class to store gesture information:
```kotlin
data class GestureData(
    val x: Float,
    val y: Float,
    val action: Int,
    val timestamp: Long
)
```

## AndroidManifest Configuration

### Required Permissions

```xml
<!-- System alert window for overlay -->
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />

<!-- Write settings (commented out as not strictly required) -->
<uses-permission android:name="android.permission.WRITE_SETTINGS" />
```

### Service Declaration

```xml
<service
    android:name=".GestureRecorderService"
    android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE"
    android:exported="true">
    <intent-filter>
        <action android:name="android.accessibilityservice.AccessibilityService" />
    </intent-filter>
    <meta-data
        android:name="android.accessibilityservice"
        android:resource="@xml/accessibility_service_config" />
</service>
```

## Accessibility Service Configuration

Located at `res/xml/accessibility_service_config.xml`:

```xml
<accessibility-service
    android:accessibilityEventTypes="typeAllMask"
    android:accessibilityFeedbackType="feedbackGeneric"
    android:accessibilityFlags="flagDefault|flagReportViewIds|flagRetrieveInteractiveWindows"
    android:canPerformGestures="true"
    android:canRetrieveWindowContent="true"
    android:description="@string/accessibility_service_description"
    android:notificationTimeout="100"
    android:packageNames="" />
```

Key attributes:
- `canPerformGestures="true"` - Enables gesture dispatch (API 24+)
- `canRetrieveWindowContent="true"` - Access to window content for text operations
- `typeAllMask` - Receive all accessibility events
- Empty `packageNames` - Monitor all apps

## UI Components

### Main Activity Layout

Features:
- Service status indicator
- Button to open accessibility settings
- Instructions for users

### Overlay Controls Layout

Floating controls with:
- Record/Stop button - Toggles gesture recording
- Play button - Replays recorded gestures
- Clear button - Clears recorded gestures
- Status text - Shows recording state and gesture count

The overlay is draggable using touch listeners on the root view.

## Gesture Recording Algorithm

1. **Start Recording**:
   - Clear previous gestures
   - Set `isRecording = true`
   - Record start timestamp

2. **Capture Gestures**:
   - Note: Standard AccessibilityService events don't provide exact touch coordinates
   - This implementation provides the framework for gesture recording
   - In production, you might need additional touch interception methods

3. **Stop Recording**:
   - Set `isRecording = false`
   - Display count of recorded gestures

4. **Playback**:
   - Group gestures into strokes (DOWN to UP sequences)
   - Create Path objects for each stroke
   - Dispatch gestures using `dispatchGesture()`
   - Add delays between strokes

## Limitations and Notes

### Current Limitations:

1. **Touch Coordinate Capture**: 
   - AccessibilityService events don't directly provide touch coordinates
   - The framework is in place, but actual coordinate capture may require additional implementation
   - Consider using accessibility node positions or additional overlays for precise tracking

2. **Android Version Requirements**:
   - Gesture dispatch requires API 24 (Android 7.0) or higher
   - Overlay permissions require user interaction on API 23+

3. **Performance**:
   - Large numbers of gestures may cause memory issues
   - Long sequences might have timing drift

### Possible Enhancements:

1. **Save/Load Gestures**: Persist gestures to storage
2. **Named Sequences**: Allow naming and managing multiple gesture sequences
3. **Gesture Editor**: UI to modify recorded gestures
4. **Loop Playback**: Repeat gestures multiple times
5. **Screen Recording**: Capture and replay along with visual feedback
6. **Precise Touch Tracking**: Implement additional touch interception for exact coordinates

## Security Considerations

This app requires powerful permissions that could be misused:

- **AccessibilityService**: Can read screen content and inject touches
- **SYSTEM_ALERT_WINDOW**: Can draw over other apps

Responsible use guidelines:
- Only use for legitimate automation purposes
- Inform users of data collection (if any)
- Don't capture sensitive information
- Follow Google Play policies if publishing

## Testing Recommendations

1. **Manual Testing**:
   - Test on multiple Android versions (7.0, 8.0, 9.0, 10+)
   - Test on different screen sizes and resolutions
   - Verify permissions flow on first install

2. **Automated Testing**:
   - Unit tests for GestureData logic
   - UI tests for MainActivity
   - Integration tests for service lifecycle

3. **Edge Cases**:
   - Service disabled during recording
   - App killed during playback
   - Screen rotation during recording
   - Multiple rapid taps
   - Long-press gestures

## Performance Optimization

- Limit maximum number of gestures stored
- Use efficient data structures for gesture lookup
- Minimize overlay redraw operations
- Release resources promptly in onDestroy()

## Future Development

Potential features for future versions:
- AI-powered gesture recognition
- Cloud sync for gesture sequences
- Macro programming language
- Integration with Tasker/automation apps
- Screen recording with gesture overlay
