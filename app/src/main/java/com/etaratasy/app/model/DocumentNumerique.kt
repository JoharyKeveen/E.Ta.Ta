package com.etaratasy.app.model

/**
 * Représentation numérique d'un document délivré, consultable directement dans
 * l'application — c'est ce qui matérialise la suppression de la file d'attente.
 *
 * `permanent = true` uniquement pour la CIN et le permis de conduire : une fois
 * retirés au guichet, ils restent affichés en permanence dans le portefeuille et
 * ne proposent aucune action de suppression côté interface.
 */
data class DocumentNumerique(
    val id: Long,
    val typeDocumentId: String,
    val nom: String,
    val guichet: Guichet,
    val dateEmission: String,
    val reference: String,
    val permanent: Boolean,
    val pdfUrl: String? = null
)
