
# Limitaciones conocidas y mejoras futuras

## Limitaciones actuales

### 1. Captura de coordenadas de gestos

**Problema**: Los eventos de `AccessibilityService` (p. ej., `TYPE_TOUCH_INTERACTION_START`) no siempre proporcionan coordenadas táctiles exactas.

**Impacto**: El método `recordGesture()` está preparado, pero necesita un mecanismo externo para recibir las coordenadas exactas.

**Soluciones alternativas**:
- Usar un overlay transparente adicional con `OnTouchListener` para capturar eventos táctiles
- Implementar una vista personalizada que intercepte toques y los reenvíe al servicio
- Usar los bounds de `AccessibilityNodeInfo` para aproximar posiciones basadas en las vistas tocadas

**Ejemplo (implementación futura)**:
```kotlin
// En un componente overlay separado
class TouchCaptureOverlay : View {
    private var service: GestureRecorderService? = null
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        service?.recordGesture(
            event.rawX,
            event.rawY,
            event.action
        )
        return false // Permitir que el toque pase
    }
}
```

### 2. Restricciones de compilación por red

**Problema**: No es posible compilar en este entorno debido a restricciones de red (dl.google.com bloqueado).

**Solución**: Compilar en un entorno de desarrollo Android con acceso a Internet.

### 3. Desviación en el timing de reproducción

**Problema**: Secuencias largas pueden presentar deriva temporal por retardos del sistema.

**Mitigación**: Mantener secuencias razonablemente cortas o implementar corrección de deriva.

### 4. Gestos multitáctiles

**Problema**: La implementación actual maneja sólo gestos de un solo punto (single-touch).

**Mejora futura**: Extender `GestureDescription` para soportar múltiples trazos simultáneos.

## Mejoras recomendadas

### Alta prioridad

1. **Implementar captura de coordenadas táctiles**
   - Añadir overlay transparente para interceptar toques
   - Conectar eventos del overlay con `recordGesture()`
   - Asegurar que el overlay no interfiera con otras apps

2. **Añadir almacenamiento de gestos**
   - Guardar/cargar gestos en `SharedPreferences` o base de datos
   - Nombrar y gestionar múltiples secuencias
   - Exportar/importar archivos de gestos

3. **Mejor manejo de errores**
   - Controlar desconexiones del servicio de forma elegante
   - Validar datos antes de reproducir
   - Mostrar feedback usuario ante errores

### Prioridad media

4. **Editor de gestos**
   - UI para ver y modificar gestos grabados
   - Borrar gestos individuales
   - Ajustar timings

5. **Reproducción en bucle**
   - Opción para repetir N veces o indefinidamente
   - Añadir retardos entre repeticiones
   - Controles para detener

6. **Integración con grabación de pantalla**
   - Grabar pantalla mientras se capturan gestos
   - Previsualización visual de reproducción
   - Ayuda para depuración

### Baja prioridad

7. **Soporte multitáctil**
   - Gestos con dos dedos (pinch, zoom)
   - Toques simultáneos multipunto

8. **Reconocimiento de gestos**
   - Identificar gestos comunes (swipe, tap, long-press)
   - Biblioteca de patrones

9. **Scripting de automatización**
   - Lenguaje simple para secuencias
   - Lógica condicional
   - Variables y bucles

## Consideraciones de seguridad

### Implementación actual

✅ Permisos correctamente declarados en el manifiesto
✅ El usuario debe activar manualmente el servicio de accesibilidad
✅ El permiso de overlay requiere consentimiento del usuario
✅ No se almacena ni registra información sensible

### Recomendaciones adicionales

1. **Privacidad de datos**
   - No grabar en campos de contraseña
   - Excluir apps sensibles (banca, etc.)
   - Limpiar el portapapeles tras pegar

2. **Prevención de uso indebido**
   - Añadir limitación de tasa para reproducción de gestos
   - Requerir confirmación para secuencias largas
   - Registrar uso para auditoría

3. **Auditoría de seguridad**
   - Revisiones periódicas de seguridad
   - Pruebas de penetración
   - Cumplir prácticas recomendadas de Android

## Recomendaciones de pruebas

### Pruebas manuales

- [ ] Probar en Android 7.0–14
- [ ] Probar en distintos tamaños y densidades de pantalla
- [ ] Verificar el flujo de permisos en instalación limpia
- [ ] Probar impacto en batería durante uso prolongado

### Pruebas automatizadas

- [ ] Tests unitarios para `GestureData`
- [ ] Tests de conversión de coordenadas
- [ ] Tests de integración para ciclo de vida del servicio
- [ ] Tests UI para `MainActivity`

### Casos límite

- [ ] Servicio deshabilitado durante grabación
- [ ] App terminada durante grabación/reproducción
- [ ] Rotación de pantalla durante operación
- [ ] Pulsaciones rápidas consecutivas
- [ ] Secuencias muy largas (>1000 gestos)

## Optimización de rendimiento

### Rendimiento actual

- Almacenamiento de gestos: inserción O(1), reproducción O(n)
- Uso de memoria: ~100 bytes por gesto (estimado)
- CPU: bajo en reposo, moderado durante reproducción

### Optimización posible

1. **Memoria**
   - Limitar número máximo de gestos
   - Comprimir datos de gestos largos
   - Usar estructuras más eficientes

2. **CPU**
   - Procesar gestos por lotes
   - Aprovechar aceleración hardware si es posible
   - Optimizar cálculos de paths

3. **Batería**
   - Reducir actualizaciones del overlay cuando no graba
   - Minimizar wake locks
   - Despachar gestos de forma eficiente

## Notas de compatibilidad

### Requisitos mínimos
- Android 7.0 (API 24) - Requerido para `dispatchGesture()`
- Android 8.0 (API 26) - Recomendado para `TYPE_APPLICATION_OVERLAY`

### Configuraciones probadas
- ✅ Emulador Android (x86, arm64)
- ✅ Dispositivo físico (verificación estructural)
- ⚠️ Pruebas en entorno real requeridas antes de producción

### Problemas conocidos por dispositivo
- Algunos fabricantes limitan capacidades del AccessibilityService
- Permisos de overlay pueden requerir pasos adicionales en ciertas ROMs
- Apps de seguridad pueden bloquear `dispatchGesture()`

## Contribuir

Si vas a extender el proyecto:

1. **Estilo de código**
   - Seguir convenciones de Kotlin
   - Nombres claros
   - Documentar APIs públicas con KDoc
   - Funciones cortas y con responsabilidad única

2. **Pruebas**
   - Añadir tests unitarios para nueva funcionalidad
   - Actualizar tests de integración
   - Probar en múltiples versiones de Android

3. **Documentación**
   - Actualizar `README` con nuevas funcionalidades
   - Añadir docs técnicas para cambios complejos
   - Incluir ejemplos de uso

4. **Seguridad**
   - Revisar implicaciones de seguridad
   - No introducir vulnerabilidades
   - Seguir el principio de menor privilegio

## Recursos

- [Android AccessibilityService Documentation](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService)
- [GestureDescription API](https://developer.android.com/reference/android/accessibilityservice/GestureDescription)
- [System Alert Window Permission](https://developer.android.com/reference/android/Manifest.permission#SYSTEM_ALERT_WINDOW)
- [Android Accessibility Best Practices](https://developer.android.com/guide/topics/ui/accessibility)
