
# Nota sobre el JAR del Gradle Wrapper

El archivo `gradle-wrapper.jar` es necesario para que el Gradle wrapper funcione, pero no se incluye en este repositorio debido a restricciones del entorno de construcción.

## Cómo obtener el wrapper JAR

### Opción 1: Generarlo localmente
Ejecuta este comando en la raíz del proyecto:
```bash
gradle wrapper --gradle-version 8.0
```

Esto descargará y configurará el Gradle wrapper, incluyendo `gradle-wrapper.jar`.

### Opción 2: Usar Android Studio
1. Abre el proyecto en Android Studio
2. Android Studio descargará automáticamente el Gradle wrapper si falta
3. El IDE gestionará la sincronización de Gradle

### Opción 3: Descarga manual
Descarga `gradle-8.0-bin.zip` desde:
https://services.gradle.org/distributions/gradle-8.0-bin.zip

Extrae el ZIP y copia `lib/gradle-wrapper.jar` a `gradle/wrapper/gradle-wrapper.jar`

## ¿Por qué falta?

El JAR del wrapper no se incluyó porque:
1. Restricciones de red en el entorno impidieron la descarga automática
2. Normalmente no se versionan archivos binarios grandes
3. El wrapper se puede regenerar en un entorno de desarrollo estándar

## Qué debes hacer

Antes de compilar el proyecto, asegúrate de tener `gradle-wrapper.jar` usando una de las opciones anteriores.
