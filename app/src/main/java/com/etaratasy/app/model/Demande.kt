package com.etaratasy.app.model

enum class StatutDemande(val label: String) {
    DEPOSEE("Déposée"),
    EN_TRAITEMENT("En traitement"),
    PRETE("Disponible numériquement"),
    RETIREE("Retirée"),
    REJETEE("Rejetée")
}

data class Demande(
    val id: Long,
    val typeDocumentId: String,
    val nomDocument: String,
    val guichet: Guichet,
    val dateDepot: String,
    val statut: StatutDemande,
    /** Référence imprimée sur le récépissé, dérivée de l'acte de naissance. */
    val reference: String
)
