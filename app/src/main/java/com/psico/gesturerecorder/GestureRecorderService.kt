package com.psico.gesturerecorder

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Path
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class GestureRecorderService : AccessibilityService() {

    private var overlayView: View? = null
    private var windowManager: WindowManager? = null
    private var isRecording = false
    private val recordedGestures = mutableListOf<GestureData>()
    private var recordingStartTime: Long = 0

    // Referencias a los botones del overlay
    private var recordButton: Button? = null
    private var playButton: Button? = null
    private var clearButton: Button? = null
    private var statusTextView: TextView? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        
        // Verificar permiso de overlay
        if (Settings.canDrawOverlays(this)) {
            createOverlayControls()
        } else {
            Toast.makeText(
                this,
                getString(R.string.overlay_permission_required),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun createOverlayControls() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        overlayView = inflater.inflate(R.layout.overlay_controls, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 100
        params.y = 100

        overlayView?.let { view ->
            recordButton = view.findViewById(R.id.recordButton)
            playButton = view.findViewById(R.id.playButton)
            clearButton = view.findViewById(R.id.clearButton)
            statusTextView = view.findViewById(R.id.statusTextView)

            // Hacer el overlay arrastrable
            var initialX = 0
            var initialY = 0
            var initialTouchX = 0f
            var initialTouchY = 0f

            view.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager?.updateViewLayout(view, params)
                        true
                    }
                    else -> false
                }
            }

            recordButton?.setOnClickListener {
                toggleRecording()
            }

            playButton?.setOnClickListener {
                replayGestures()
            }

            clearButton?.setOnClickListener {
                clearGestures()
            }

            windowManager?.addView(view, params)
            updateUI()
        }
    }

    private fun toggleRecording() {
        isRecording = !isRecording
        
        if (isRecording) {
            recordedGestures.clear()
            recordingStartTime = System.currentTimeMillis()
            recordButton?.text = getString(R.string.stop_recording)
            Toast.makeText(this, getString(R.string.recording), Toast.LENGTH_SHORT).show()
        } else {
            recordButton?.text = getString(R.string.start_recording)
            Toast.makeText(
                this,
                getString(R.string.gestures_recorded, recordedGestures.size),
                Toast.LENGTH_SHORT
            ).show()
        }
        
        updateUI()
    }

    private fun replayGestures() {
        if (recordedGestures.isEmpty()) {
            Toast.makeText(this, "No hay gestos grabados", Toast.LENGTH_SHORT).show()
            return
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Toast.makeText(
                this,
                "Reproducción de gestos requiere Android 7.0+",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        Toast.makeText(this, "Reproduciendo gestos...", Toast.LENGTH_SHORT).show()

        // Agrupar gestos en trazos (desde ACTION_DOWN hasta ACTION_UP)
        val strokes = mutableListOf<MutableList<GestureData>>()
        var currentStroke = mutableListOf<GestureData>()

        for (gesture in recordedGestures) {
            currentStroke.add(gesture)
            if (gesture.action == MotionEvent.ACTION_UP) {
                if (currentStroke.isNotEmpty()) {
                    strokes.add(currentStroke)
                    currentStroke = mutableListOf()
                }
            }
        }

        // Reproducir cada trazo
        for ((index, stroke) in strokes.withIndex()) {
            android.os.Handler(mainLooper).postDelayed({
                performGestureStroke(stroke)
            }, index * 100L) // Pequeño delay entre trazos
        }
    }

    @Suppress("DEPRECATION")
    private fun performGestureStroke(stroke: List<GestureData>) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return
        if (stroke.isEmpty()) return

        val path = Path()
        val firstGesture = stroke.first()
        path.moveTo(firstGesture.x, firstGesture.y)

        for (i in 1 until stroke.size) {
            val gesture = stroke[i]
            path.lineTo(gesture.x, gesture.y)
        }

        // Calcular duración basada en timestamps
        val duration = if (stroke.size > 1) {
            (stroke.last().timestamp - stroke.first().timestamp).coerceAtLeast(10)
        } else {
            100L
        }

        val gestureBuilder = GestureDescription.Builder()
        val strokeDescription = GestureDescription.StrokeDescription(
            path,
            0,
            duration,
            false
        )
        
        gestureBuilder.addStroke(strokeDescription)
        
        val gesture = gestureBuilder.build()
        dispatchGesture(gesture, null, null)
    }

    private fun clearGestures() {
        recordedGestures.clear()
        Toast.makeText(this, "Gestos eliminados", Toast.LENGTH_SHORT).show()
        updateUI()
    }

    private fun updateUI() {
        statusTextView?.text = if (isRecording) {
            getString(R.string.recording)
        } else {
            getString(R.string.gestures_recorded, recordedGestures.size)
        }
        
        playButton?.isEnabled = recordedGestures.isNotEmpty() && !isRecording
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            // Capturar eventos de toque si estamos grabando
            if (isRecording && event.eventType == AccessibilityEvent.TYPE_TOUCH_INTERACTION_START) {
                // NOTA: Los eventos de accesibilidad no proporcionan coordenadas exactas.
                // Para obtener coordenadas precisas, se requeriría:
                // 1. Un overlay transparente que capture eventos táctiles, O
                // 2. Usar AccessibilityNodeInfo para obtener bounds de vistas tocadas, O
                // 3. Implementar un servicio adicional con permisos especiales
                // El método recordGesture() está disponible para ser llamado desde 
                // implementaciones externas que sí tengan acceso a las coordenadas.
            }

            // Funcionalidad de copiar/pegar texto
            if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
                captureSelectedText(event)
            }
        }
    }

    private fun captureSelectedText(event: AccessibilityEvent) {
        val source = event.source ?: return
        
        // Obtener el texto seleccionado
        val text = source.text?.toString() ?: return
        
        if (text.isNotEmpty()) {
            // Copiar al portapapeles
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Captured Text", text)
            clipboard.setPrimaryClip(clip)
        }
    }

    /**
     * Función pública para pegar texto en un campo editable.
     * Puede ser llamada desde otras partes de la app o mediante reflection/IPC.
     * 
     * @param text El texto a pegar en el campo activo
     * 
     * Ejemplo de uso desde otra actividad:
     * val serviceIntent = Intent(this, GestureRecorderService::class.java)
     * serviceIntent.putExtra("action", "paste_text")
     * serviceIntent.putExtra("text", "texto a pegar")
     */
    fun pasteText(text: String) {
        val rootNode = rootInActiveWindow ?: return
        val editableNode = findEditableNode(rootNode)
        
        editableNode?.let { node ->
            val arguments = android.os.Bundle()
            arguments.putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                text
            )
            node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
            node.recycle()
        }
        
        rootNode.recycle()
    }

    private fun findEditableNode(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        if (node.isEditable) {
            return node
        }
        
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val result = findEditableNode(child)
            if (result != null) {
                return result
            }
            child.recycle()
        }
        
        return null
    }

    override fun onInterrupt() {
        // Manejar interrupciones
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayView?.let {
            windowManager?.removeView(it)
        }
    }

    /**
     * Método público para capturar gestos desde fuera del servicio.
     * 
     * Este método está diseñado para ser llamado por componentes externos que
     * puedan capturar coordenadas exactas de toques (por ejemplo, un overlay
     * transparente personalizado con OnTouchListener).
     * 
     * @param x Coordenada X del toque en píxeles
     * @param y Coordenada Y del toque en píxeles  
     * @param action Tipo de acción (MotionEvent.ACTION_DOWN, ACTION_MOVE, ACTION_UP)
     * 
     * Ejemplo de uso desde un overlay:
     * overlayView.setOnTouchListener { _, event ->
     *     if (service != null) {
     *         service.recordGesture(event.rawX, event.rawY, event.action)
     *     }
     *     false
     * }
     */
    fun recordGesture(x: Float, y: Float, action: Int) {
        if (isRecording) {
            val timestamp = System.currentTimeMillis() - recordingStartTime
            recordedGestures.add(GestureData(x, y, action, timestamp))
        }
    }
}
