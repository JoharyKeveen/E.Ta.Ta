package com.etaratasy.app.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.etaratasy.app.model.*
import com.etaratasy.app.repository.EtataRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel unique de l'application (architecture MVVM).
 * Utilise AndroidViewModel pour accéder au Context et aux SharedPreferences.
 */
class EtataViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repo = EtataRepository()
    private val prefs = application.getSharedPreferences("etata_prefs", Context.MODE_PRIVATE)

    private val _ui = MutableStateFlow(EtataUiState(
        securiteActivee = prefs.getBoolean("securite_activee", false),
        sessionSauvegardee = prefs.getBoolean("session_sauvegardee", false)
    ))
    val ui: StateFlow<EtataUiState> = _ui.asStateFlow()

    private var citoyenEnAttente: Citoyen? = null

    init {
        // Si une session est sauvegardée, on pré-charge le citoyen
        if (_ui.value.sessionSauvegardee) {
            val dernierActe = prefs.getString("dernier_acte", null)
            if (dernierActe != null) {
                val res = repo.verifierNumeroActe(dernierActe)
                if (res is EtataRepository.ResultatAuth.Succes) {
                    citoyenEnAttente = res.citoyen
                }
            }
        }
    }

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

        viewModelScope.launch {
            _ui.update { it.copy(chargement = true) }
            delay(1500)

            // Documents de démonstration : 
            // 1. Un certificat déjà prêt
            val typeRes = Catalogue.parId("fkt_residence")!!
            val docRes = repo.genererDocumentNumerique(typeRes, citoyen)
            val demandeRes = Demande(
                id = docRes.id,
                typeDocumentId = typeRes.id,
                nomDocument = typeRes.nom,
                guichet = typeRes.guichet,
                dateDepot = "10/09/2026",
                statut = StatutDemande.PRETE,
                reference = docRes.reference
            )

            // 2. CIN et Permis déjà possédés (pièces officielles permanentes)
            val typeCin = Catalogue.parId("arr_cin")!!
            val typePermis = Catalogue.parId("arr_permis")!!
            val docCin = repo.genererDocumentNumerique(typeCin, citoyen, permanent = true)
            val docPermis = repo.genererDocumentNumerique(typePermis, citoyen, permanent = true)

            _ui.update {
                it.copy(
                    citoyen = citoyen,
                    connecte = true,
                    chargement = false,
                    sessionSauvegardee = true,
                    notifications = repo.notificationsInitiales(citoyen),
                    demandes = listOf(demandeRes),
                    documentsNumeriques = listOf(docRes),
                    piecesOfficielles = listOf(docCin, docPermis)
                )
            }
            prefs.edit().putString("dernier_acte", citoyen.numeroActe.toString())
                .putBoolean("session_sauvegardee", true).apply()
        }
        return null
    }

    fun deconnexion() {
        citoyenEnAttente = null
        _ui.value = EtataUiState(
            securiteActivee = _ui.value.securiteActivee
        )
        prefs.edit().putBoolean("session_sauvegardee", false).apply()
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
        viewModelScope.launch {
            _ui.update { it.copy(chargement = true) }
            delay(1200)
            
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
                    documentsNumeriques = listOf(doc) + it.documentsNumeriques,
                    chargement = false
                )
            }
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

    /* ---------------- Sécurité ---------------- */

    fun toggleSecurite(active: Boolean) {
        _ui.update { it.copy(securiteActivee = active) }
        prefs.edit().putBoolean("securite_activee", active).apply()
    }

    fun validerAuthentificationBiometrique() {
        viewModelScope.launch {
            _ui.update { it.copy(chargement = true) }
            delay(1000)
            
            // Si on bypass le login, on charge un citoyen par défaut pour la démo
            if (!_ui.value.connecte) {
                val citoyenDemo = repo.verifierNumeroActe("0453/2001-101").let {
                    if (it is EtataRepository.ResultatAuth.Succes) it.citoyen else null
                }
                if (citoyenDemo != null) {
                    val typeCin = Catalogue.parId("arr_cin")!!
                    val typePermis = Catalogue.parId("arr_permis")!!
                    val docCin = repo.genererDocumentNumerique(typeCin, citoyenDemo, permanent = true)
                    val docPermis = repo.genererDocumentNumerique(typePermis, citoyenDemo, permanent = true)

                    _ui.update { 
                        it.copy(
                            citoyen = citoyenDemo, 
                            connecte = true,
                            piecesOfficielles = listOf(docCin, docPermis)
                        ) 
                    }
                }
            }
            
            _ui.update { it.copy(authentifieBiometrique = true, chargement = false) }
        }
    }
}
