package com.etaratasy.app.repository

import com.etaratasy.app.model.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

/**
 * Dépôt de données simulé (couche "Model" au sens large de MVVM).
 *
 * Dans une version réelle, chaque méthode devient un appel réseau vers le serveur
 * de l'administration. Les signatures ne changent pas quand on branche Retrofit/Ktor :
 * le ViewModel et l'UI n'ont rien à savoir de cette implémentation.
 */
class EtataRepository {

    /** Registre d'état civil simulé, indexé par numéro d'acte de naissance. */
    private val registreEtatCivil: Map<String, Citoyen> = listOf(
        Citoyen(
            numeroActe = NumeroActeNaissance("0453", "2001", "101"),
            nom = "RASOA",
            prenoms = "Soa Hanitra",
            dateNaissance = "14/05/2001",
            lieuNaissance = "Maternité Befelatanana, Antananarivo",
            numeroCin = "101 202 000 453",
            adresse = "Lot II V 45 Analamahitsy",
            arrondissement = "Antananarivo V",
            profession = "Étudiante",
            pere = "RASOA Jean Baptiste",
            mere = "RAVELO Marie Louise",
            fokontany = "Analamahitsy",
            telephone = "034 12 345 67"
        ),
        Citoyen(
            numeroActe = NumeroActeNaissance("1207", "1994", "104"),
            nom = "RAKOTO",
            prenoms = "Jean Michel",
            dateNaissance = "02/11/1994",
            lieuNaissance = "Ambohimahasoa",
            numeroCin = "104 194 001 207",
            adresse = "Logt 432 Cité Ankadifotsy",
            arrondissement = "Antananarivo I",
            profession = "Comptable",
            pere = "RAKOTO Pierre",
            mere = "RANDRIA Suzanne",
            fokontany = "Ankadifotsy",
            telephone = "033 98 765 43"
        )
    ).associateBy { it.numeroActe.toString() }

    /* ------------------------- Authentification ------------------------- */

    sealed class ResultatAuth {
        data class Succes(val citoyen: Citoyen) : ResultatAuth()
        data class Erreur(val message: String) : ResultatAuth()
    }

    fun verifierNumeroActe(saisie: String): ResultatAuth {
        val numero = NumeroActeNaissance.parse(saisie)
            ?: return ResultatAuth.Erreur("Format attendu : 0453/2001-101")
        val citoyen = registreEtatCivil[numero.toString()]
            ?: return ResultatAuth.Erreur("Aucun acte de naissance ne correspond à ce numéro.")
        if (!citoyen.compteActive) {
            return ResultatAuth.Erreur("Compte non activé. Présentez-vous à votre Fokontany.")
        }
        return ResultatAuth.Succes(citoyen)
    }

    fun verifierCode(code: String): Boolean = code.trim().length == 6 && code.all { it.isDigit() }

    /* ------------------------- Références & documents ------------------------- */

    fun genererReference(numero: NumeroActeNaissance): String {
        val suffixe = Random.nextInt(1000, 9999)
        return "ETT-${numero.ordre}.${numero.annee}.${numero.codeCommune}-$suffixe"
    }

    fun genererCodePartage(): String {
        val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..6).map { alphabet.random() }.joinToString("")
    }

    fun dateDuJour(): String =
        SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(Calendar.getInstance().time)

    /** Simule l'URL ou le chemin d'un fichier PDF pour le document. */
    fun getMockPdfPath(typeDocumentId: String): String =
        "pdfs/${typeDocumentId}_template.pdf"

    /**
     * Génère la version numérique d'un document, immédiatement consultable dans
     * l'application. `permanent = true` pour la CIN et le permis retirés au guichet.
     */
    fun genererDocumentNumerique(type: TypeDocument, citoyen: Citoyen, permanent: Boolean = false): DocumentNumerique {
        val metas = mutableMapOf<String, String>()
        
        when (type.id) {
            "arr_cin" -> {
                metas["dateDelivrance"] = "12/06/2019"
                metas["lieuDelivrance"] = citoyen.arrondissement
            }
            "fkt_residence" -> {
                metas["infoMaison"] = "Maison individuelle en dur, portail vert"
                metas["ville"] = "Antananarivo"
                metas["pays"] = "Madagascar"
            }
            "arr_permis" -> {
                metas["categorie"] = "B"
                metas["validite"] = "30/06/2028"
            }
        }

        return DocumentNumerique(
            id = System.nanoTime(),
            typeDocumentId = type.id,
            nom = type.nom,
            guichet = type.guichet,
            dateEmission = dateDuJour(),
            reference = genererReference(citoyen.numeroActe),
            permanent = permanent,
            pdfUrl = getMockPdfPath(type.id),
            metadonnees = metas
        )
    }

    /* ------------------------- Notifications système ------------------------- */

    fun notificationsInitiales(citoyen: Citoyen): List<Notification> {
        val liste = mutableListOf<Notification>()
        val anneeCourante = Calendar.getInstance().get(Calendar.YEAR)
        val age = anneeCourante - citoyen.numeroActe.anneeNaissance

        if (age >= 18) {
            liste += Notification(
                id = 1,
                type = TypeNotification.MAJORITE,
                titre = "Vous avez atteint la majorité",
                corps = "Vous pouvez demander votre CIN. Prenez rendez-vous à votre Arrondissement " +
                    "depuis l'onglet Rendez-vous, muni de votre acte de naissance n° ${citoyen.numeroActe}.",
                quand = "il y a 2 jours"
            )
        }
        liste += Notification(
            id = 2,
            type = TypeNotification.PERMIS,
            titre = "Résultat de l'examen du permis",
            corps = "Épreuve théorique réussie. Présentez-vous au bureau administratif pour " +
                "préparer la suite de la procédure.",
            quand = "il y a 5 jours"
        )
        liste += Notification(
            id = 3,
            type = TypeNotification.RENDEZ_VOUS,
            titre = "Rappel de rendez-vous",
            corps = "Acte de notoriété — demain à 09h00, Arrondissement Antananarivo I.",
            quand = "il y a 6 jours",
            lue = true
        )
        liste += Notification(
            id = 4,
            type = TypeNotification.MISE_A_JOUR,
            titre = "Mise à jour de vos informations",
            corps = "Un agent du Fokontany a mis à jour votre adresse de résidence.",
            quand = "il y a 1 semaine",
            lue = true
        )
        return liste
    }

    /* ------------------------- Module forces de l'ordre ------------------------- */

    fun controlerParNom(nomComplet: String): ResultatControle? {
        if (nomComplet.isBlank()) return null
        val citoyen = registreEtatCivil.values.firstOrNull {
            it.nomComplet.equals(nomComplet.trim(), ignoreCase = true) ||
                it.nom.equals(nomComplet.trim(), ignoreCase = true)
        }
        return if (citoyen != null) {
            ResultatControle(
                nomComplet = citoyen.nomComplet,
                numeroActe = citoyen.numeroActe.toString(),
                cinValide = true,
                cinDetail = "Délivrée le 12/03/2019",
                permisValide = true,
                permisDetail = "Catégorie B, valable jusqu'au 30/06/2028",
                alerte = null
            )
        } else {
            ResultatControle(
                nomComplet = nomComplet.trim(),
                numeroActe = "—",
                cinValide = false,
                cinDetail = "Aucune pièce numérisée trouvée",
                permisValide = false,
                permisDetail = "Aucun permis enregistré",
                alerte = "Personne non trouvée dans le registre numérique."
            )
        }
    }
}
