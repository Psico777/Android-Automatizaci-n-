# App Architecture and Flow Diagrams

## Component Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        User Interface                        │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌─────────────────┐          ┌─────────────────────────┐  │
│  │  MainActivity   │          │   Floating Overlay      │  │
│  │                 │          │   Controls              │  │
│  │  - Permission   │          │                         │  │
│  │    Management   │          │  ┌──────────────────┐  │  │
│  │  - Status       │          │  │ Record Button    │  │  │
│  │    Display      │          │  ├──────────────────┤  │  │
│  │                 │          │  │ Play Button      │  │  │
│  └─────────────────┘          │  ├──────────────────┤  │  │
│                                │  │ Clear Button     │  │  │
│                                │  ├──────────────────┤  │  │
│                                │  │ Status Text      │  │  │
│                                │  └──────────────────┘  │  │
│                                └─────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                  GestureRecorderService                      │
│                  (AccessibilityService)                      │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────────┐  ┌──────────────────┐  ┌───────────┐ │
│  │ Gesture          │  │ Gesture          │  │ Text      │ │
│  │ Recording        │  │ Playback         │  │ Copy/Paste│ │
│  │                  │  │                  │  │           │ │
│  │ - Capture touch  │  │ - Build paths    │  │ - Monitor │ │
│  │   coordinates    │  │ - Dispatch       │  │   text    │ │
│  │ - Store in list  │  │   gestures       │  │   select  │ │
│  │ - Track timing   │  │ - Replay timing  │  │ - Inject  │ │
│  └──────────────────┘  └──────────────────┘  └───────────┘ │
│                                                               │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                        Data Layer                            │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  GestureData (List)                                  │  │
│  │  ┌────────────────────────────────────────────────┐ │  │
│  │  │ GestureData(x, y, action, timestamp)           │ │  │
│  │  │ GestureData(x, y, action, timestamp)           │ │  │
│  │  │ GestureData(x, y, action, timestamp)           │ │  │
│  │  │ ...                                            │ │  │
│  │  └────────────────────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                               │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     Android System APIs                      │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  • WindowManager (Overlay)                                   │
│  • AccessibilityService API                                  │
│  • GestureDescription.dispatchGesture()                     │
│  • ClipboardManager                                          │
│  • AccessibilityNodeInfo (Text injection)                    │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

## User Flow Diagrams

### Setup Flow

```
┌─────────────┐
│   Install   │
│     App     │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│    Open     │
│     App     │
└──────┬──────┘
       │
       ▼
┌─────────────────────────┐
│  Tap "Habilitar         │
│  Servicio" Button       │
└──────┬──────────────────┘
       │
       ▼
┌─────────────────────────┐
│  Open Accessibility     │
│  Settings               │
└──────┬──────────────────┘
       │
       ▼
┌─────────────────────────┐
│  Enable "Gesture        │
│  Recorder" Service      │
└──────┬──────────────────┘
       │
       ▼
┌─────────────────────────┐
│  Grant Overlay          │
│  Permission             │
└──────┬──────────────────┘
       │
       ▼
┌─────────────────────────┐
│  Floating Controls      │
│  Appear on Screen       │
└─────────────────────────┘
```

### Gesture Recording Flow

```
┌────────────────┐
│ User Taps      │
│ "Iniciar       │
│  Grabación"    │
└────────┬───────┘
         │
         ▼
┌────────────────────────┐
│ isRecording = true     │
│ Clear gesture list     │
│ Start timestamp        │
└────────┬───────────────┘
         │
         ▼
┌────────────────────────┐     ┌──────────────────┐
│ User Performs Touch    │────▶│ Capture (x, y,   │
│ Gestures on Screen     │     │ action, time)    │
└────────────────────────┘     └────────┬─────────┘
         │                              │
         │                              ▼
         │                     ┌────────────────────┐
         │                     │ Store in           │
         │                     │ recordedGestures   │
         │                     └────────────────────┘
         ▼
┌────────────────────────┐
│ User Taps "Detener     │
│ Grabación"             │
└────────┬───────────────┘
         │
         ▼
┌────────────────────────┐
│ isRecording = false    │
│ Display gesture count  │
└────────────────────────┘
```

### Gesture Playback Flow

```
┌────────────────┐
│ User Taps      │
│ "Reproducir    │
│  Gestos"       │
└────────┬───────┘
         │
         ▼
┌────────────────────────┐
│ Group gestures into    │
│ strokes (DOWN to UP)   │
└────────┬───────────────┘
         │
         ▼
┌────────────────────────┐
│ For each stroke:       │
│                        │
│ 1. Create Path         │◀───┐
│ 2. Add all points      │    │
│ 3. Calculate duration  │    │
└────────┬───────────────┘    │
         │                     │
         ▼                     │
┌────────────────────────┐    │
│ Build                  │    │
│ GestureDescription     │    │
└────────┬───────────────┘    │
         │                     │
         ▼                     │
┌────────────────────────┐    │
│ dispatchGesture()      │    │
│ (System performs touch)│    │
└────────┬───────────────┘    │
         │                     │
         ▼                     │
┌────────────────────────┐    │
│ More strokes?          │────┘
│ Yes: Add delay & loop  │
│ No: Complete           │
└────────────────────────┘
```

### Copy/Paste Text Flow

```
┌────────────────────────┐
│ User selects text      │
│ in any app             │
└────────┬───────────────┘
         │
         ▼
┌────────────────────────┐
│ AccessibilityService   │
│ receives               │
│ TEXT_SELECTION_CHANGED │
└────────┬───────────────┘
         │
         ▼
┌────────────────────────┐
│ Extract selected text  │
│ from event source      │
└────────┬───────────────┘
         │
         ▼
┌────────────────────────┐
│ Copy to ClipboardManager│
└────────────────────────┘

For Pasting:

┌────────────────────────┐
│ pasteText() called     │
└────────┬───────────────┘
         │
         ▼
┌────────────────────────┐
│ Get rootInActiveWindow │
└────────┬───────────────┘
         │
         ▼
┌────────────────────────┐
│ Find editable node     │
│ (recursive search)     │
└────────┬───────────────┘
         │
         ▼
┌────────────────────────┐
│ Perform ACTION_SET_TEXT│
│ with text argument     │
└────────────────────────┘
```

## State Diagram

```
┌─────────────────┐
│   Initial       │
│   State         │
└────────┬────────┘
         │ App installed
         ▼
┌─────────────────┐
│  Permission     │
│  Setup Required │
└────────┬────────┘
         │ Permissions granted
         ▼
┌─────────────────┐      Start Recording
│   Ready/Idle    │◀────────────────────┐
└────────┬────────┘                     │
         │                               │
         │ Tap Record                    │
         ▼                               │
┌─────────────────┐      Stop Recording │
│   Recording     │──────────────────────┘
└────────┬────────┘
         │
         │ Gestures captured
         ▼
┌─────────────────┐
│  Ready with     │
│  Gestures       │
└────────┬────────┘
         │
         │ Tap Play
         ▼
┌─────────────────┐
│   Playing       │
│   Gestures      │
└────────┬────────┘
         │ Complete
         ▼
┌─────────────────┐
│  Ready with     │
│  Gestures       │
└─────────────────┘
         │
         │ Tap Clear
         ▼
┌─────────────────┐
│   Ready/Idle    │
└─────────────────┘
```

## Data Flow: Gesture Recording to Playback

```
Touch Event
    │
    ▼
┌───────────────────────────┐
│ Record Phase              │
│                           │
│ GestureData(              │
│   x: 100.0,               │
│   y: 200.0,               │
│   action: ACTION_DOWN,    │
│   timestamp: 0            │
│ )                         │
│                           │
│ GestureData(              │
│   x: 150.0,               │
│   y: 250.0,               │
│   action: ACTION_MOVE,    │
│   timestamp: 50           │
│ )                         │
│                           │
│ GestureData(              │
│   x: 200.0,               │
│   y: 300.0,               │
│   action: ACTION_UP,      │
│   timestamp: 100          │
│ )                         │
└───────────┬───────────────┘
            │
            ▼
┌───────────────────────────┐
│ Group into Stroke         │
│                           │
│ Stroke 1: [               │
│   GestureData(...),       │
│   GestureData(...),       │
│   GestureData(...)        │
│ ]                         │
└───────────┬───────────────┘
            │
            ▼
┌───────────────────────────┐
│ Convert to Path           │
│                           │
│ path.moveTo(100, 200)     │
│ path.lineTo(150, 250)     │
│ path.lineTo(200, 300)     │
│                           │
│ duration = 100ms          │
└───────────┬───────────────┘
            │
            ▼
┌───────────────────────────┐
│ Create StrokeDescription  │
│                           │
│ StrokeDescription(        │
│   path,                   │
│   startTime: 0,           │
│   duration: 100,          │
│   willContinue: false     │
│ )                         │
└───────────┬───────────────┘
            │
            ▼
┌───────────────────────────┐
│ Dispatch Gesture          │
│                           │
│ dispatchGesture(          │
│   gestureDescription,     │
│   callback,               │
│   handler                 │
│ )                         │
└───────────┬───────────────┘
            │
            ▼
     System Touch Event
```

This architecture enables clean separation of concerns and makes the app maintainable and extensible.
