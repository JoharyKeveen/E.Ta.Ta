package com.etaratasy.app.data

/** Catalogue statique des documents, repris du cahier des charges E.Ta.Ta. */
object Catalogue {

    /** Certificats délivrés par le Fokontany, demandables entièrement en ligne. */
    val certificatsFokontany = listOf(
        TypeDocument("fkt_celibat", "Certificat de célibat", Guichet.FOKONTANY),
        TypeDocument("fkt_residence", "Certificat de résidence", Guichet.FOKONTANY),
        TypeDocument("fkt_bonne_conduite", "Certificat de bonne conduite", Guichet.FOKONTANY),
        TypeDocument("fkt_prise_en_charge", "Certificat de prise en charge et de garde", Guichet.FOKONTANY),
        TypeDocument("fkt_non_remariage", "Certificat de non-remariage", Guichet.FOKONTANY),
        TypeDocument("fkt_divorce", "Certificat de divorce", Guichet.FOKONTANY),
        TypeDocument("fkt_propriete", "Certificat relatif à la propriété", Guichet.FOKONTANY),
        TypeDocument("fkt_lieu_exploitation", "Certificat d'existence du lieu d'exploitation", Guichet.FOKONTANY),
        TypeDocument("fkt_occupation", "Certificat d'occupation", Guichet.FOKONTANY),
        TypeDocument("fkt_vie_collective", "Certificat de vie collective", Guichet.FOKONTANY)
    )

    /** Autres demandes hors Fokontany. */
    val autresDocuments = listOf(
        TypeDocument("dip_bacc", "Diplôme du baccalauréat", Guichet.CISCO)
    )

    /** Documents exigeant une présence physique : la demande crée un rendez-vous. */
    val documentsSpeciaux = listOf(
        TypeDocument("fkt_vie_individuelle", "Certificat de vie individuelle", Guichet.FOKONTANY, surRendezVous = true),
        TypeDocument("arr_partage", "Acte de partage", Guichet.ARRONDISSEMENT, surRendezVous = true),
        TypeDocument("arr_notoriete", "Acte de notoriété", Guichet.ARRONDISSEMENT, surRendezVous = true),
        TypeDocument("arr_mariage_civil", "Certificat de mariage civil", Guichet.ARRONDISSEMENT, surRendezVous = true),
        TypeDocument("arr_cin", "Carte d'identité nationale (CIN)", Guichet.ARRONDISSEMENT, surRendezVous = true),
        TypeDocument("arr_permis", "Permis de conduire", Guichet.ARRONDISSEMENT, surRendezVous = true)
    )

    val tous: List<TypeDocument> = certificatsFokontany + autresDocuments + documentsSpeciaux

    val creneaux = listOf("08h00", "09h00", "10h30", "13h00", "14h30", "15h30")

    val groupesSanguins = listOf("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-")
}
