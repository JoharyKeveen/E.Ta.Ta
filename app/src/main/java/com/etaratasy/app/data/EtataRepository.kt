package com.etaratasy.app.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

/**
 * Dépôt de données simulé.
 *
 * Dans la version réelle, chaque méthode devient un appel réseau vers le serveur de
 * l'administration (registre d'état civil, fokontany, arrondissement). L'interface
 * reste identique, ce qui permet de brancher Retrofit/Ktor sans toucher à l'UI.
 */
class EtataRepository {

    /** Registre d'état civil simulé, indexé par numéro d'acte de naissance. */
    private val registreEtatCivil: Map<String, Citoyen> = listOf(
        Citoyen(
            numeroActe = NumeroActeNaissance("0453", "2001", "101"),
            nom = "RASOA",
            prenoms = "Soa Hanitra",
            dateNaissance = "14/05/2001",
            fokontany = "Analamahitsy, Antananarivo I",
            telephone = "034 12 345 67"
        ),
        Citoyen(
            numeroActe = NumeroActeNaissance("1207", "1994", "104"),
            nom = "RAKOTO",
            prenoms = "Jean Michel",
            dateNaissance = "02/11/1994",
            fokontany = "Ankadifotsy, Antananarivo IV",
            telephone = "033 98 765 43"
        )
    ).associateBy { it.numeroActe.toString() }

    /* ------------------------- Authentification ------------------------- */

    sealed class ResultatAuth {
        data class Succes(val citoyen: Citoyen) : ResultatAuth()
        data class Erreur(val message: String) : ResultatAuth()
    }

    /**
     * Étape 1 : le citoyen saisit son numéro d'acte de naissance.
     * Le serveur vérifie qu'il existe dans le registre avant d'envoyer un code par SMS.
     */
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

    /** Étape 2 : vérification du code reçu par SMS (simulé : 6 chiffres quelconques). */
    fun verifierCode(code: String): Boolean = code.trim().length == 6 && code.all { it.isDigit() }

    /* ------------------------- Références ------------------------- */

    /**
     * Référence d'une demande, dérivée du numéro d'acte pour que l'agent du guichet
     * puisse relier immédiatement le dossier au registre d'état civil.
     * Ex. ETT-0453.2001.101-8241
     */
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

    /* ------------------------- Notifications système ------------------------- */

    /**
     * Notifications générées automatiquement par le système.
     * La majorité est calculée à partir de l'année de l'acte de naissance.
     */
    fun notificationsInitiales(citoyen: Citoyen): List<Notification> {
        val liste = mutableListOf<Notification>()
        val anneeCourante = Calendar.getInstance().get(Calendar.YEAR)
        val age = anneeCourante - citoyen.numeroActe.anneeNaissance

        if (age >= 18) {
            liste += Notification(
                id = 1,
                type = TypeNotification.MAJORITE,
                titre = "Vous avez atteint la majorité",
                corps = "Vous pouvez demander votre CIN. Prenez rendez-vous à votre " +
                    "Arrondissement depuis l'onglet Rendez-vous, muni de votre acte de naissance " +
                    "n° ${citoyen.numeroActe}.",
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

    /**
     * Recherche par nom complet, réservée aux agents assermentés.
     * Ne renvoie que la validité des pièces, jamais le contenu des documents.
     */
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
