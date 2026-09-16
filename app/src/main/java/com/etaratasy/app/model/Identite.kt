package com.etaratasy.app.model

/**
 * Identité citoyenne.
 *
 * Le numéro d'identification de l'application EST le numéro de l'acte de naissance :
 * c'est la seule pièce que tout Malagasy possède dès la naissance, bien avant la CIN.
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
