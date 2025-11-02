
# Documentación técnica de implementación

## Visión general de la arquitectura

La aplicación está construida usando el framework `AccessibilityService` de Android para interceptar y despachar eventos táctiles, junto con `WindowManager` para mostrar controles flotantes.

### Componentes principales

1. **MainActivity.kt** - Punto de entrada y gestión de permisos
2. **GestureRecorderService.kt** - Implementación del `AccessibilityService`
3. **GestureData.kt** - Modelo de datos para almacenar información de gestos
4. **Layouts** - Archivos XML con las interfaces de usuario

## Detalles de implementación

### 1. AccessibilityService (GestureRecorderService)

El servicio extiende `AccessibilityService` y proporciona la funcionalidad principal:

#### Funcionalidades clave:

**Creación de controles overlay**
```kotlin
private fun createOverlayControls() {
    windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    overlayView = inflater.inflate(R.layout.overlay_controls, null)
    
    // Usar TYPE_APPLICATION_OVERLAY para Android 8.0+
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

**Grabación de gestos**
- Los gestos se almacenan como objetos `GestureData` que contienen:
  - Coordenada X
  - Coordenada Y  
  - Tipo de acción (ACTION_DOWN, ACTION_MOVE, ACTION_UP)
  - Timestamp (relativo al inicio de la grabación)

**Reproducción de gestos**
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

**Copiar/Pegar texto**
- Los eventos de selección de texto se capturan mediante `TYPE_VIEW_TEXT_SELECTION_CHANGED`
- El texto seleccionado se copia automáticamente al portapapeles
- La función de pegado busca nodos editables y utiliza `ACTION_SET_TEXT`

### 2. Gestión de permisos (MainActivity)

La app requiere dos permisos críticos:

#### Permiso de accesibilidad
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

#### Permiso de overlay
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

### 3. Modelo de datos (GestureData)

Clase de datos sencilla para almacenar la información de cada gesto:
```kotlin
data class GestureData(
    val x: Float,
    val y: Float,
    val action: Int,
    val timestamp: Long
)
```

## Configuración en AndroidManifest

### Permisos requeridos

```xml
<!-- Permiso para mostrar ventanas sobre otras apps -->
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />

<!-- (Opcional) WRITE_SETTINGS si se necesitase -->
<uses-permission android:name="android.permission.WRITE_SETTINGS" />
```

### Declaración del servicio

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

## Configuración del AccessibilityService

Archivo: `res/xml/accessibility_service_config.xml`:

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

Atributos clave:
- `canPerformGestures="true"` - Habilita el dispatch de gestos (API 24+)
- `canRetrieveWindowContent="true"` - Permite acceso al contenido de ventanas para operaciones de texto
- `typeAllMask` - Recibe todos los eventos de accesibilidad
- `packageNames` vacío - Monitoriza todas las aplicaciones

## Componentes de UI

### Layout de MainActivity

Características:
- Indicador del estado del servicio
- Botón para abrir ajustes de accesibilidad
- Instrucciones para el usuario

### Layout de controles overlay

Controles flotantes con:
- Botón Grabar/Parar - Alterna la grabación
- Botón Reproducir - Reproduce gestos grabados
- Botón Limpiar - Borra gestos grabados
- Texto de estado - Muestra estado y contador de gestos

El overlay es arrastrable mediante listeners táctiles.

## Algoritmo de grabación

1. **Iniciar grabación**:
   - Limpiar gestos previos
   - `isRecording = true`
   - Registrar timestamp de inicio

2. **Capturar gestos**:
   - Nota: Los eventos estándar de `AccessibilityService` no siempre ofrecen coordenadas exactas
   - Esta implementación ofrece la estructura para grabar gestos
   - En producción puede necesitarse interceptación táctil adicional

3. **Detener grabación**:
   - `isRecording = false`
   - Mostrar número de gestos grabados

4. **Reproducción**:
   - Agrupar gestos en trazos (DOWN → UP)
   - Crear objetos `Path` por trazo
   - Despachar con `dispatchGesture()`
   - Añadir retrasos entre trazos si es necesario

## Limitaciones y notas

### Limitaciones actuales:

1. **Captura de coordenadas táctiles**: 
   - Los eventos de `AccessibilityService` no siempre proporcionan coordenadas exactas
   - La estructura está implementada, pero la captura precisa puede requerir un overlay táctil adicional

2. **Requisitos de versión Android**:
   - `dispatchGesture()` requiere API 24 (Android 7.0) o superior
   - Permisos de overlay requieren interacción del usuario en API 23+

3. **Rendimiento**:
   - Secuencias largas de gestos pueden consumir memoria
   - Pueden producirse desviaciones temporales en ejecuciones muy largas

### Posibles mejoras:

1. **Guardar/Cargar gestos**: Persistir gestos en almacenamiento
2. **Secuencias con nombre**: Administrar múltiples secuencias guardadas
3. **Editor de gestos**: UI para modificar gestos grabados
4. **Reproducción en bucle**: Repetir gestos N veces
5. **Grabación de pantalla**: Capturar pantalla junto con gestos para depuración
6. **Captura táctil precisa**: Implementar overlay para captura exacta

## Consideraciones de seguridad

La app requiere permisos potentes que podrían ser mal utilizados:

- **AccessibilityService**: Puede leer contenido de pantalla e inyectar toques
- **SYSTEM_ALERT_WINDOW**: Puede dibujar sobre otras apps

Buenas prácticas:
- Usar únicamente para automatización legítima
- Informar al usuario sobre cualquier recogida de datos
- Evitar capturar información sensible
- Cumplir las políticas de Google Play al publicar

## Recomendaciones de pruebas

1. **Pruebas manuales**:
   - Probar en varias versiones de Android (7.0, 8.0, 9.0, 10+)
   - Probar en distintos tamaños y resoluciones de pantalla
   - Verificar el flujo de permisos en una instalación limpia

2. **Pruebas automatizadas**:
   - Tests unitarios para la lógica de `GestureData`
   - Tests UI para `MainActivity`
   - Tests de integración para el ciclo de vida del servicio

3. **Casos límite**:
   - Servicio deshabilitado durante la grabación
   - App terminada durante la reproducción
   - Rotación de pantalla durante la grabación
   - Pulsaciones rápidas consecutivas
   - Gestos de larga duración

## Optimización de rendimiento

- Limitar número máximo de gestos almacenados
- Usar estructuras eficientes para búsqueda/recuperación
- Minimizar repintados del overlay
- Liberar recursos en `onDestroy()`

## Desarrollo futuro

Características potenciales:
- Reconocimiento de gestos con IA
- Sincronización en la nube para secuencias
- Lenguaje de macros para automatizaciones
- Integración con Tasker u otras apps de automatización
- Grabación de pantalla con overlay de gestos
