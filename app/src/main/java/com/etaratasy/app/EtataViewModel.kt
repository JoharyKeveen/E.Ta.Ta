package com.etaratasy.app

import androidx.lifecycle.ViewModel
import com.etaratasy.app.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EtataUiState(
    val citoyen: Citoyen? = null,
    val connecte: Boolean = false,
    val demandes: List<Demande> = emptyList(),
    val rendezVous: List<RendezVous> = emptyList(),
    val partages: List<PartageAcces> = emptyList(),
    val notifications: List<Notification> = emptyList(),
    val sante: DossierSante = DossierSante(),
    val resultatControle: ResultatControle? = null
) {
    val nonLues: Int get() = notifications.count { !it.lue }
}

class EtataViewModel(
    private val repo: EtataRepository = EtataRepository()
) : ViewModel() {

    private val _ui = MutableStateFlow(EtataUiState())
    val ui: StateFlow<EtataUiState> = _ui.asStateFlow()

    /* ---------------- Authentification par numéro d'acte de naissance ---------------- */

    private var citoyenEnAttente: Citoyen? = null

    /** Retourne un message d'erreur, ou null si le numéro est reconnu. */
    fun soumettreNumeroActe(saisie: String): String? =
        when (val r = repo.verifierNumeroActe(saisie)) {
            is EtataRepository.ResultatAuth.Succes -> {
                citoyenEnAttente = r.citoyen
                null
            }
            is EtataRepository.ResultatAuth.Erreur -> r.message
        }

    /** Retourne un message d'erreur, ou null si la connexion réussit. */
    fun soumettreCode(code: String): String? {
        if (!repo.verifierCode(code)) return "Code à 6 chiffres invalide."
        val citoyen = citoyenEnAttente ?: return "Session expirée, recommencez."
        _ui.update {
            it.copy(
                citoyen = citoyen,
                connecte = true,
                notifications = repo.notificationsInitiales(citoyen),
                demandes = listOf(
                    Demande(
                        id = 1,
                        typeDocumentId = "fkt_residence",
                        nomDocument = "Certificat de résidence",
                        guichet = Guichet.FOKONTANY,
                        dateDepot = "10/09/2026",
                        statut = StatutDemande.PRETE,
                        reference = repo.genererReference(citoyen.numeroActe)
                    )
                )
            )
        }
        return null
    }

    fun deconnexion() {
        citoyenEnAttente = null
        _ui.value = EtataUiState()
    }

    /* ---------------- Demandes ---------------- */

    fun deposerDemande(type: TypeDocument) {
        val citoyen = _ui.value.citoyen ?: return
        val demande = Demande(
            id = System.currentTimeMillis(),
            typeDocumentId = type.id,
            nomDocument = type.nom,
            guichet = type.guichet,
            dateDepot = repo.dateDuJour(),
            statut = StatutDemande.DEPOSEE,
            reference = repo.genererReference(citoyen.numeroActe)
        )
        _ui.update { it.copy(demandes = listOf(demande) + it.demandes) }
    }

    /* ---------------- Rendez-vous ---------------- */

    fun prendreRendezVous(type: TypeDocument, date: String, heure: String) {
        val rdv = RendezVous(
            id = System.currentTimeMillis(),
            nomDocument = type.nom,
            guichet = type.guichet,
            date = date,
            heure = heure
        )
        _ui.update { it.copy(rendezVous = listOf(rdv) + it.rendezVous) }
    }

    fun annulerRendezVous(id: Long) {
        _ui.update { st -> st.copy(rendezVous = st.rendezVous.filterNot { it.id == id }) }
    }

    /* ---------------- Partage temporaire ---------------- */

    fun creerPartage(nomDocument: String, duree: DureePartage) {
        val partage = PartageAcces(
            id = System.currentTimeMillis(),
            nomDocument = nomDocument,
            duree = duree,
            code = repo.genererCodePartage()
        )
        _ui.update { it.copy(partages = listOf(partage) + it.partages) }
    }

    fun revoquerPartage(id: Long) {
        _ui.update { st ->
            st.copy(partages = st.partages.map { if (it.id == id) it.copy(actif = false) else it })
        }
    }

    /* ---------------- Santé ---------------- */

    fun majSante(transformation: (DossierSante) -> DossierSante) {
        _ui.update { it.copy(sante = transformation(it.sante)) }
    }

    /* ---------------- Notifications ---------------- */

    fun marquerLue(id: Long) {
        _ui.update { st ->
            st.copy(notifications = st.notifications.map { if (it.id == id) it.copy(lue = true) else it })
        }
    }

    /* ---------------- Contrôle forces de l'ordre ---------------- */

    fun rechercherControle(nom: String) {
        _ui.update { it.copy(resultatControle = repo.controlerParNom(nom)) }
    }

    fun effacerControle() {
        _ui.update { it.copy(resultatControle = null) }
    }
}
