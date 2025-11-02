
# Guía de compilación e instalación

## Requisitos previos

1. **Android Studio** - Descárgalo desde https://developer.android.com/studio
2. **JDK 8 o superior** - Normalmente incluido con Android Studio
3. **Android SDK** - Instálalo desde el SDK Manager de Android Studio
   - SDK mínimo: API 24 (Android 7.0)
   - SDK objetivo: API 34 (Android 14)

## Compilar el proyecto

### Método 1: Usando Android Studio

1. Abre Android Studio
2. Selecciona "Open an Existing Project"
3. Navega hasta la carpeta del proyecto y selecciónala
4. Espera a que Gradle sincronice
5. Para compilar:
   - Menú: Build → Make Project
   - O presiona: Ctrl+F9 (Windows/Linux) o Cmd+F9 (Mac)

### Método 2: Línea de comandos

```bash
# En Linux/Mac
./gradlew assembleDebug

# En Windows
gradlew.bat assembleDebug
```

El APK generado estará en:
```
app/build/outputs/apk/debug/app-debug.apk
```

## Instalar en un dispositivo

### Usando Android Studio

1. Conecta el dispositivo por USB (habilita Depuración USB en Opciones de desarrollador)
2. Haz clic en el botón "Run" (triángulo verde) o presiona Shift+F10
3. Selecciona tu dispositivo

### Usando ADB

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Primera configuración

Tras instalar la app, debes conceder permisos:

### 1. Habilitar el servicio de accesibilidad

1. Abre la app
2. Pulsa el botón "Habilitar Servicio"
3. En Ajustes de accesibilidad, busca "Gesture Recorder"
4. Actívalo (ON)
5. Confirma el diálogo de permisos

### 2. Conceder permiso de overlay

1. Cuando se solicite, pulsa "Allow" para conceder el permiso de overlay
2. Si no aparece la solicitud automáticamente:
   - Ve a Ajustes → Apps → Gesture Recorder
   - Selecciona "Display over other apps"
   - Actívalo (ON)

## Probar la aplicación

### Probar la grabación de gestos

1. Tras conceder permisos, aparecerán botones flotantes en pantalla
2. Arrastra los botones donde te resulten cómodos
3. Pulsa "Iniciar Grabación" para comenzar
4. Realiza gestos táctiles en la pantalla
5. Pulsa "Detener Grabación" para finalizar
6. Pulsa "Reproducir Gestos" para reproducir lo grabado

### Probar copiar/pegar

1. Abre cualquier app con texto (p. ej., navegador, notas)
2. Selecciona texto
3. El servicio capturará el texto automáticamente
4. Abre otra app con un campo de texto
5. El texto capturado estará disponible en el portapapeles

## Resolución de problemas

### La app se cierra al iniciar

- Comprueba que usas Android 7.0 (API 24) o superior
- Verifica que se hayan concedido todos los permisos

### Los botones flotantes no aparecen

- Asegura que el servicio de accesibilidad esté habilitado
- Comprueba que el permiso de overlay esté concedido
- Reinicia la app si es necesario

### No se graban gestos

- Verifica que el servicio de accesibilidad esté en ejecución
- Asegúrate de haber pulsado "Iniciar Grabación"
- Revisa restricciones de accesibilidad en ajustes del sistema

### No se reproducen gestos

- Asegura que la versión de Android sea 7.0 o superior (dispatchGesture requiere API 24+)
- Verifica que se haya grabado correctamente la secuencia
- Intenta grabar gestos sencillos (taps simples) primero

## Compilar para release

1. Genera una clave de firma:
```bash
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-alias
```

2. Actualiza `app/build.gradle.kts` con la configuración de firma:
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
            // ... otras configuraciones
        }
    }
}
```

3. Genera el APK de release:
```bash
./gradlew assembleRelease
```

## Notas de seguridad

- Nunca subas tus claves de firma al control de versiones
- Almacena contraseñas de forma segura (p. ej., variables de entorno)
- La app requiere permisos sensibles: úsalos responsablemente
- Prueba en varias versiones de Android antes de publicar

## Requisitos del sistema para desarrollo

- **SO**: Windows 10/11, macOS 10.14+ o Linux
- **RAM**: Mínimo 8 GB (recomendado 16 GB)
- **Espacio en disco**: Al menos 10 GB libres
- **Resolución de pantalla**: 1280 x 800 mínimo
