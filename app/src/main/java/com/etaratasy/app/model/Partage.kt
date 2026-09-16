package com.etaratasy.app.model

enum class DureePartage(val label: String, val heures: Int) {
    H24("24 heures", 24),
    H72("72 heures", 72),
    J7("7 jours", 168)
}

data class PartageAcces(
    val id: Long,
    val nomDocument: String,
    val duree: DureePartage,
    val code: String,
    val actif: Boolean = true
)
