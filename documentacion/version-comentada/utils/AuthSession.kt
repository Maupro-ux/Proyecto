/**
 * VERSIÓN COMENTADA DE REFERENCIA. La lógica coincide con la versión limpia compilable.
 * Las explicaciones documentan responsabilidad, ciclo de vida, fórmulas y acceso a Room.
 */
package com.proyecto.gyrocampro.utils

/**
 * Estado de autenticación en memoria del proceso. Solo LoginActivity puede activar
 * markAuthenticated desde el callback de éxito; logout revoca la bandera.
 * Si el proceso muere, el valor vuelve a false: la sesión no se restaura sin nueva huella.
 */
object AuthSession {
    // Volatile asegura visibilidad del valor si el estado se consulta desde otro hilo.
    @Volatile
    var isAuthenticated: Boolean = false
        private set

    fun markAuthenticated() {
        isAuthenticated = true
    }

    fun clear() {
        isAuthenticated = false
    }
}
