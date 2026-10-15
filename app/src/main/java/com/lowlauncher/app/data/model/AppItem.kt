package com.lowlauncher.app.data.model

data class AppItem(
    val packageName: String,
    val activityName: String,
    val label: String,
    val isFavorite: Boolean = false,
    val hasFrictionDelay: Boolean = false
) {
    val isWhatsApp: Boolean
        get() = packageName == "com.whatsapp" ||
                packageName == "com.whatsapp.w4b" ||
                packageName.contains("whatsapp", ignoreCase = true)
}
