package com.etaratasy.app.model

data class RendezVous(
    val id: Long,
    val typeDocumentId: String,
    val nomDocument: String,
    val guichet: Guichet,
    val date: String,
    val heure: String
)
