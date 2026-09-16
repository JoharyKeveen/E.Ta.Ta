package com.etaratasy.app.viewmodel

import androidx.lifecycle.ViewModel
import com.etaratasy.app.model.*
import com.etaratasy.app.repository.EtataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel unique de l'application (architecture MVVM) :
 *  - Model      → com.etaratasy.app.model / com.etaratasy.app.repository
 *  - ViewModel  → cette classe : expose un état immuable (StateFlow) et des actions
 *  - View       → com.etaratasy.app.ui.screens, composables sans état propre,
 *                 qui lisent `ui` et appellent les fonctions publiques ci-dessous.
 */
class EtataViewModel(
    private val repo: EtataRepository = EtataRepository()
) : ViewModel() {

    private val _ui = MutableStateFlow(EtataUiState())
    val ui: StateFlow<EtataUiState> = _ui.asStateFlow()

    private var citoyenEnAttente: Citoyen? = null

    /* ---------------- Authentification par numéro d'acte de naissance ---------------- */

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

        // Un premier document de démonstration, déjà dans le portefeuille.
        val typeDemo = Catalogue.parId("fkt_residence")!!
        val docDemo = repo.genererDocumentNumerique(typeDemo, citoyen)
        val demandeDemo = Demande(
            id = docDemo.id,
            typeDocumentId = typeDemo.id,
            nomDocument = typeDemo.nom,
            guichet = typeDemo.guichet,
            dateDepot = "10/09/2026",
            statut = StatutDemande.PRETE,
            reference = docDemo.reference
        )

        _ui.update {
            it.copy(
                citoyen = citoyen,
                connecte = true,
                notifications = repo.notificationsInitiales(citoyen),
                demandes = listOf(demandeDemo),
                documentsNumeriques = listOf(docDemo)
            )
        }
        return null
    }

    fun deconnexion() {
        citoyenEnAttente = null
        _ui.value = EtataUiState()
    }

    /* ---------------- Demandes sans rendez-vous ---------------- */

    /**
     * Dépôt d'une demande pour un document du Fokontany ou le diplôme (BACC).
     * Ces documents ne nécessitent aucune présence physique : le document numérique
     * est généré immédiatement et rendu visible dans le portefeuille — c'est ce qui
     * supprime la file d'attente pour cette catégorie de pièces.
     */
    fun deposerDemande(type: TypeDocument) {
        val citoyen = _ui.value.citoyen ?: return
        val doc = repo.genererDocumentNumerique(type, citoyen)
        val demande = Demande(
            id = doc.id,
            typeDocumentId = type.id,
            nomDocument = type.nom,
            guichet = type.guichet,
            dateDepot = repo.dateDuJour(),
            statut = StatutDemande.PRETE,
            reference = doc.reference
        )
        _ui.update {
            it.copy(
                demandes = listOf(demande) + it.demandes,
                documentsNumeriques = listOf(doc) + it.documentsNumeriques
            )
        }
    }

    /* ---------------- Rendez-vous (documents spéciaux : CIN, permis, actes) ---------------- */

    fun prendreRendezVous(type: TypeDocument, date: String, heure: String) {
        val rdv = RendezVous(
            id = System.currentTimeMillis(),
            typeDocumentId = type.id,
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

    /**
     * Retrait effectif au guichet, déclenché par le citoyen une fois le document
     * physiquement obtenu. Pour la CIN et le permis de conduire uniquement, cela crée
     * une pièce officielle permanente dans le portefeuille : elle ne propose alors
     * aucune action de suppression.
     */
    fun marquerRetrait(rdvId: Long) {
        val citoyen = _ui.value.citoyen ?: return
        val rdv = _ui.value.rendezVous.firstOrNull { it.id == rdvId } ?: return
        val type = Catalogue.parId(rdv.typeDocumentId)

        _ui.update { st ->
            val restants = st.rendezVous.filterNot { it.id == rdvId }
            if (type != null && type.pieceOfficiellePermanente) {
                val piece = repo.genererDocumentNumerique(type, citoyen, permanent = true)
                st.copy(rendezVous = restants, piecesOfficielles = st.piecesOfficielles + piece)
            } else {
                st.copy(rendezVous = restants)
            }
        }
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
