package com.etaratasy.app.viewmodel

import com.etaratasy.app.model.*

data class EtataUiState(
    val citoyen: Citoyen? = null,
    val connecte: Boolean = false,
    val demandes: List<Demande> = emptyList(),
    val rendezVous: List<RendezVous> = emptyList(),
    /** Documents disponibles numériquement : certificats Fokontany + diplôme, sans RDV. */
    val documentsNumeriques: List<DocumentNumerique> = emptyList(),
    /** CIN et permis retirés : permanents, jamais supprimables. */
    val piecesOfficielles: List<DocumentNumerique> = emptyList(),
    val partages: List<PartageAcces> = emptyList(),
    val notifications: List<Notification> = emptyList(),
    val sante: DossierSante = DossierSante(),
    val resultatControle: ResultatControle? = null
) {
    val nonLues: Int get() = notifications.count { !it.lue }
}
