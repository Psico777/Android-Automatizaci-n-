````markdown
# Gesture Recorder - Automatización Android

Aplicación Android en Kotlin que utiliza AccessibilityService para automatizar gestos y acciones en la pantalla.

## 🎯 Características

1. **Grabación de Gestos**: Graba las coordenadas y tipo de toque del usuario en la pantalla
2. **Reproducción de Gestos**: Repite la secuencia de gestos grabada
3. **Controles Flotantes**: Botones flotantes (overlay) para iniciar/detener la grabación
4. **Copiar/Pegar Texto**: Copia y pega texto entre aplicaciones usando el servicio de accesibilidad

## 📋 Requisitos

- Android 7.0 (API 24) o superior
- Permiso de accesibilidad
- Permiso de overlay (ventanas flotantes)

## 📥 Instalación

1. Compilar el proyecto con Gradle:
```bash
./gradlew assembleDebug
```

2. Instalar la aplicación en un dispositivo Android

3. Habilitar el servicio de accesibilidad:
   - Abrir la app
   - Presionar "Habilitar Servicio"
   - En la configuración de accesibilidad, activar "Gesture Recorder"

4. Conceder permiso de overlay cuando se solicite

## 🎮 Uso

### Botones Flotantes

Después de habilitar el servicio, aparecerán botones flotantes en la pantalla:

- **Iniciar Grabación**: Comienza a grabar los gestos en la pantalla
- **Detener Grabación**: Detiene la grabación
- **Reproducir Gestos**: Reproduce la secuencia de gestos grabada
- **Limpiar Gestos**: Elimina todos los gestos grabados

Los botones pueden ser movidos arrastrándolos a cualquier posición de la pantalla.

### Copiar/Pegar Texto

El servicio automáticamente captura texto seleccionado y lo copia al portapapeles. La funcionalidad de pegado está disponible mediante el AccessibilityService.

## 📁 Estructura del Proyecto

```
app/
├── src/main/
│   ├── java/com/psico/gesturerecorder/
│   │   ├── MainActivity.kt              # Actividad principal
│   │   ├── GestureRecorderService.kt    # Servicio de accesibilidad
│   │   └── GestureData.kt               # Clase de datos para gestos
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_main.xml        # Layout de la actividad principal
│   │   │   └── overlay_controls.xml     # Layout de los controles flotantes
│   │   ├── values/
│   │   │   └── strings.xml              # Recursos de texto
│   │   └── xml/
│   │       └── accessibility_service_config.xml  # Configuración del servicio
│   └── AndroidManifest.xml              # Manifiesto con permisos y servicios
```

## 🔐 Permisos

La aplicación requiere los siguientes permisos:

- `SYSTEM_ALERT_WINDOW`: Para mostrar botones flotantes sobre otras aplicaciones
- `BIND_ACCESSIBILITY_SERVICE`: Para acceder a las funciones de accesibilidad

## 💡 Notas Técnicas

- La reproducción de gestos requiere Android 7.0 (API 24) o superior
- Los gestos se graban con coordenadas absolutas en la pantalla
- El servicio de accesibilidad debe estar activo para todas las funcionalidades
- La aplicación no requiere root

## 📄 Licencia

Este proyecto es de código abierto.

````
