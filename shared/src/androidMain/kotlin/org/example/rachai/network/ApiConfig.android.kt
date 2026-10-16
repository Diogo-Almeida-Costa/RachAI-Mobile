package org.example.rachai.network

import android.os.Build

private fun isAndroidEmulator(): Boolean {
    val fingerprint = Build.FINGERPRINT
    val model = Build.MODEL
    val hardware = Build.HARDWARE
    return fingerprint.startsWith("generic") ||
            fingerprint.startsWith("unknown") ||
            model.contains("google_sdk") ||
            model.contains("Emulator") ||
            model.contains("Android SDK built for x86") ||
            hardware.contains("goldfish") ||
            hardware.contains("ranchu")
}

/**
 * No emulador Android, 10.0.2.2 é o alias padrão que mapeia para o localhost da máquina de desenvolvimento.
 * Em dispositivos físicos conectados na mesma rede Wi-Fi, utiliza o IP local do computador.
 */
actual val apiBaseUrl: String
    get() {
        return if (isAndroidEmulator()) {
            "http://10.0.2.2:8080"
        } else {
            "http://192.168.1.81:8080"
        }
    }
