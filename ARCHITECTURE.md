
# Arquitectura de la aplicación y diagramas de flujo

## Arquitectura de componentes

```
┌─────────────────────────────────────────────────────────────┐
│                        Interfaz de Usuario                  │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌─────────────────┐          ┌─────────────────────────┐  │
│  │  MainActivity   │          │   Overlay flotante      │  │
│  │                 │          │   (Controles)           │  │
│  │  - Gestión de   │          │                         │  │
│  │    permisos     │          │  ┌──────────────────┐  │  │
│  │  - Indicador    │          │  │ Botón Grabar     │  │  │
│  │    de estado    │          │  ├──────────────────┤  │  │
│  │                 │          │  │ Botón Reproducir │  │  │
│  └─────────────────┘          │  ├──────────────────┤  │  │
│                                │  │ Botón Limpiar    │  │  │
│                                │  ├──────────────────┤  │  │
│                                │  │ Texto de estado  │  │  │
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
│  │ Grabación de     │  │ Reproducción de  │  │ Copiar/    │ │
│  │ gestos           │  │ gestos           │  │ pegar texto│ │
│  │                  │  │                  │  │           │ │
│  │ - Captura de     │  │ - Construcción   │  │ - Monit.  │ │
│  │   coordenadas    │  │   de paths       │  │   selección│ │
│  │ - Almacena lista │  │ - Dispatch de    │  │ - Inyección│ │
│  │ - Registro timing│  │   gestos         │  │   en campos│ │
│  └──────────────────┘  └──────────────────┘  └───────────┘ │
│                                                               │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                        Capa de datos                         │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  GestureData (Lista)                                 │  │
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
│                     APIs del sistema Android                 │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  • WindowManager (Overlay)                                   │
│  • AccessibilityService API                                  │
│  • GestureDescription.dispatchGesture()                     │
│  • ClipboardManager                                          │
│  • AccessibilityNodeInfo (Inyección de texto)               │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

## Diagramas de flujo de usuario

### Flujo de configuración

```
┌─────────────┐
│   Instalar  │
│     App     │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│    Abrir    │
│     App     │
└──────┬──────┘
       │
       ▼
┌─────────────────────────┐
│  Pulsar "Habilitar      │
│  Servicio"              │
└──────┬──────────────────┘
       │
       ▼
┌─────────────────────────┐
│  Abrir Ajustes de       │
│  Accesibilidad          │
└──────┬──────────────────┘
       │
       ▼
┌─────────────────────────┐
│  Activar "Gesture       │
│  Recorder" Service      │
└──────┬──────────────────┘
       │
       ▼
┌─────────────────────────┐
│  Conceder permiso de    │
│  overlay                │
└──────┬──────────────────┘
       │
       ▼
┌─────────────────────────┐
│  Aparecen controles     │
│  flotantes en pantalla  │
└─────────────────────────┘
```

### Flujo de grabación de gestos

```
┌────────────────┐
│ Usuario pulsa  │
│ "Iniciar       │
│  Grabación"    │
└────────┬───────┘
         │
         ▼
┌────────────────────────┐
│ isRecording = true     │
│ Limpiar lista de gestos│
│ Iniciar timestamp      │
└────────┬───────────────┘
         │
         ▼
┌────────────────────────┐     ┌──────────────────┐
│ Usuario realiza toques │────▶│ Capturar (x, y,  │
│ en pantalla            │     │ action, timestamp)│
└────────────────────────┘     └────────┬─────────┘
         │                              │
         │                              ▼
         │                     ┌────────────────────┐
         │                     │ Guardar en          │
         │                     │ recordedGestures    │
         │                     └────────────────────┘
         ▼
┌────────────────────────┐
│ Usuario pulsa "Detener │
│ Grabación"             │
└────────┬───────────────┘
         │
         ▼
┌────────────────────────┐
│ isRecording = false    │
│ Mostrar contador       │
└────────────────────────┘
```

### Flujo de reproducción de gestos

```
┌────────────────┐
│ Usuario pulsa  │
│ "Reproducir    │
│  Gestos"       │
└────────┬───────┘
         │
         ▼
┌────────────────────────┐
│ Agrupar gestos en      │
│ trazos (DOWN → UP)     │
└────────┬───────────────┘
         │
         ▼
┌────────────────────────┐
│ Por cada trazo:        │
│ 1. Crear Path          │
│ 2. Añadir puntos       │
│ 3. Calcular duración   │
└────────┬───────────────┘
         │
         ▼
┌────────────────────────┐
│ Construir              │
│ GestureDescription     │
└────────┬───────────────┘
         │
         ▼
┌────────────────────────┐
│ dispatchGesture()      │
└────────────────────────┘
```

### Flujo copiar/pegar

```
┌────────────────────────┐
│ Usuario selecciona     │
│ texto en cualquier app │
└────────┬───────────────┘
         │
         ▼
┌────────────────────────┐
│ Servicio recibe evento  │
│ TEXT_SELECTION_CHANGED  │
└────────┬───────────────┘
         │
         ▼
┌────────────────────────┐
│ Extraer texto seleccionado│
└────────┬───────────────┘
         │
         ▼
┌────────────────────────┐
│ Copiar al portapapeles │
└────────────────────────┘
```

## Diagrama de estados

```
┌─────────────────┐
│   Inicial       │
│   (Sin permisos)│
└────────┬────────┘
         │ App instalada
         ▼
┌─────────────────┐
│  Config. permisos│
└────────┬────────┘
         │ Permisos concedidos
         ▼
┌─────────────────┐      Iniciar grabación
│   Listo / Idle  │◀────────────────────┐
└────────┬────────┘                     │
         │                               │
         │ Pulsar Grabar                │
         ▼                               │
┌─────────────────┐      Parar grabación │
│   Grabando      │──────────────────────┘
└────────┬────────┘
         │
         │ Gestos capturados
         ▼
┌─────────────────┐
│ Listo con gestos│
└────────┬────────┘
         │
         │ Pulsar Reproducir
         ▼
┌─────────────────┐
│ Reproduciendo   │
└────────┬────────┘
         │ Completado
         ▼
┌─────────────────┐
│ Listo con gestos│
└─────────────────┘
```

## Flujo de datos: grabación → reproducción

```
Evento táctil
    │
    ▼
┌───────────────────────────┐
│ Fase de grabación         │
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
│ Agrupar en trazos         │
└───────────┬───────────────┘
            │
            ▼
┌───────────────────────────┐
│ Convertir a Path          │
└───────────┬───────────────┘
            │
            ▼
┌───────────────────────────┐
│ Crear StrokeDescription   │
└───────────┬───────────────┘
            │
            ▼
┌───────────────────────────┐
│ Dispatch Gesture          │
└───────────────────────────┘
```

Esta arquitectura facilita la separación de responsabilidades y hace la aplicación mantenible y extensible.
