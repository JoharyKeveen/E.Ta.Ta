package com.etaratasy.app.data

/**
 * Identité citoyenne.
 *
 * Le numéro d'identification de l'application EST le numéro de l'acte de naissance.
 * C'est la seule pièce que tout Malagasy possède dès la naissance, bien avant la CIN :
 * elle sert donc de racine à l'identité numérique.
 *
 * Format retenu : NNNN/AAAA-CCC
 *   NNNN = numéro d'ordre de l'acte dans le registre
 *   AAAA = année de l'acte
 *   CCC  = code de la commune / arrondissement d'enregistrement
 * Exemple : 0453/2001-101
 */
data class NumeroActeNaissance(
    val ordre: String,
    val annee: String,
    val codeCommune: String
) {
    override fun toString(): String = "$ordre/$annee-$codeCommune"

    companion object {
        private val REGEX = Regex("""^(\d{1,5})/(\d{4})-(\d{2,4})$""")

        /** Parse "0453/2001-101". Retourne null si le format est invalide. */
        fun parse(raw: String): NumeroActeNaissance? {
            val m = REGEX.find(raw.trim()) ?: return null
            val (ordre, annee, commune) = m.destructured
            val an = annee.toIntOrNull() ?: return null
            if (an < 1900 || an > 2100) return null
            return NumeroActeNaissance(ordre.padStart(4, '0'), annee, commune)
        }

        fun isValid(raw: String): Boolean = parse(raw) != null
    }

    /** Année de naissance déduite de l'acte, utilisée pour la notification de majorité. */
    val anneeNaissance: Int get() = annee.toInt()
}

data class Citoyen(
    val numeroActe: NumeroActeNaissance,
    val nom: String,
    val prenoms: String,
    val dateNaissance: String,
    val fokontany: String,
    val telephone: String,
    val compteActive: Boolean = true
) {
    val nomComplet: String get() = "$nom $prenoms"
    val initiales: String
        get() = "${nom.firstOrNull() ?: ' '}${prenoms.firstOrNull() ?: ' '}".trim().uppercase()
}

enum class Guichet(val label: String) {
    FOKONTANY("Fokontany"),
    ARRONDISSEMENT("Arrondissement"),
    CISCO("CISCO / Établissement")
}

/** Un type de document que le citoyen peut demander. */
data class TypeDocument(
    val id: String,
    val nom: String,
    val guichet: Guichet,
    /** true = la remise exige un passage physique, donc un rendez-vous. */
    val surRendezVous: Boolean = false
)

enum class StatutDemande(val label: String) {
    DEPOSEE("Déposée"),
    EN_TRAITEMENT("En traitement"),
    PRETE("Prête à retirer"),
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

data class RendezVous(
    val id: Long,
    val nomDocument: String,
    val guichet: Guichet,
    val date: String,
    val heure: String,
    val confirme: Boolean = true
)

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

data class DossierSante(
    val groupeSanguin: String = "",
    val allergies: String = "",
    val contactUrgence: String = ""
)

enum class TypeNotification { MAJORITE, PERMIS, RENDEZ_VOUS, MISE_A_JOUR }

data class Notification(
    val id: Long,
    val type: TypeNotification,
    val titre: String,
    val corps: String,
    val quand: String,
    val lue: Boolean = false
)

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
