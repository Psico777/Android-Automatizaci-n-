# Guía Rápida - Gesture Recorder App

## 📱 Instalar la app

### Descargar APK
El APK compilado está disponible en:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Instalar con ADB
```bash
# Conecta tu dispositivo Android por USB (con Depuración USB habilitada)
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Instalar manualmente
1. Descarga el archivo `app-debug.apk`
2. Transfiere a tu dispositivo Android
3. Abre el explorador de archivos y toca el APK para instalar

## ⚙️ Configuración (primera vez)

### Paso 1: Habilitar servicio de accesibilidad
1. Abre la app **"Gesture Recorder"**
2. Presiona el botón **"Habilitar Servicio"**
3. Ve a: **Ajustes → Accesibilidad**
4. Busca **"Gesture Recorder"** y actívalo (ON)
5. Confirma los diálogos que aparezcan

### Paso 2: Permitir mostrar sobre otras apps
1. Cuando se solicite en la app, presiona **"Permitir"**
2. Si no aparece automáticamente:
   - Ve a: **Ajustes → Apps → Gesture Recorder**
   - Toca **"Mostrar sobre otras apps"**
   - Actívalo (ON)

## 🎮 Usar la app

### Grabar gestos
1. Los botones flotantes aparecerán en la pantalla
2. Presiona **"Iniciar Grabación"** (botón rojo)
3. Realiza los toques/gestos que quieras grabar
4. Presiona **"Detener Grabación"** cuando termines
5. Verás un contador del número de gestos grabados

### Reproducir gestos
1. Presiona **"Reproducir Gestos"** (botón azul)
2. La app ejecutará automáticamente la secuencia grabada
3. Los gestos se repetirán en el mismo orden y con el mismo timing

### Limpiar gestos
- Presiona **"Limpiar Gestos"** (botón gris) para borrar la grabación actual
- Luego puedes grabar una nueva secuencia

### Copiar/pegar texto
- Selecciona texto en cualquier app y se copiará automáticamente
- El texto estará disponible en el portapapeles para pegar en otros lugares

## 🎯 Casos de uso típicos

### Automatizar tareas repetitivas
1. Graba los gestos de una tarea que repites frecuentemente
2. Presiona "Reproducir Gestos" para ejecutarla de nuevo

### Llenar formularios
1. Graba los toques y selecciones para completar un formulario
2. Luego reproduce los gestos para llenar el formulario en nuevas entradas

### Navegar en apps
1. Graba una secuencia de clicks para navegar dentro de una app
2. Reproduce para hacer la misma navegación rápidamente

## ⚠️ Limitaciones y notas

- La app funciona en **Android 7.0 (API 24) o superior**
- Los gestos se graban solo mientras está activo el servicio de accesibilidad
- Los gestos se almacenan en memoria (se borran al cerrar la app)
- La precisión de las coordenadas depende del dispositivo

## 🔧 Troubleshooting

### La app se cierra al abrir
- Verifica que estés usando Android 7.0 o superior
- Desinstala y vuelve a instalar el APK

### Los botones flotantes no aparecen
- Asegúrate de que el servicio de accesibilidad está habilitado
- Verifica que el permiso "Mostrar sobre otras apps" está concedido
- Reinicia la app

### No se graban los gestos
- Verifica en Ajustes → Accesibilidad que "Gesture Recorder" está ON
- El servicio debe estar activo para que funcione

### No se reproducen los gestos
- Asegúrate de haber grabado al menos un gesto
- Verifica que el dispositivo esté usando Android 7.0+
- Intenta con una secuencia más simple primero

## 📞 Más información

- Revisa `BUILD_GUIDE.md` para instrucciones detalladas de compilación
- Consulta `TECHNICAL_DOCS.md` para detalles técnicos de la implementación
- Lee `LIMITATIONS.md` para conocer las limitaciones actuales y mejoras propuestas

## 🎁 Mejoras futuras

Próximas funcionalidades planeadas:
- ✅ Guardar/cargar secuencias de gestos
- ✅ Editor de gestos grabados
- ✅ Reproducción en bucle
- ✅ Exportar/importar secuencias
- ✅ Mejora en la captura precisa de coordenadas

---

**¡Disfruta usando Gesture Recorder!** 🚀
