package com.psico.gesturerecorder

/**
 * Clase de datos para almacenar información de un gesto
 */
data class GestureData(
    val x: Float,
    val y: Float,
    val action: Int, // ACTION_DOWN, ACTION_MOVE, ACTION_UP
    val timestamp: Long
)
