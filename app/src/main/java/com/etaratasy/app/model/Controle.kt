package com.etaratasy.app.model

/** Résultat renvoyé au module de contrôle des forces de l'ordre. */
data class ResultatControle(
    val nomComplet: String,
    val numeroActe: String,
    val cinValide: Boolean,
    val cinDetail: String,
    val permisValide: Boolean,
    val permisDetail: String,
    val alerte: String?
)
