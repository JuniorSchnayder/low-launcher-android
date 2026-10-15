package com.lowlauncher.app.util

import java.text.Normalizer

/**
 * Remove acentos e caracteres diacríticos para busca insensível a acentuação.
 * Ex: "Câmera" -> "camera", "Configurações" -> "configuracoes", "Música" -> "musica".
 */
fun String.unaccent(): String {
    val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
    return Regex("\\p{InCombiningDiacriticalMarks}+").replace(normalized, "")
}

/**
 * Normaliza uma string para comparação de busca (sem acentos e em minúsculas).
 */
fun String.normalizeForSearch(): String {
    return this.unaccent().lowercase().trim()
}
