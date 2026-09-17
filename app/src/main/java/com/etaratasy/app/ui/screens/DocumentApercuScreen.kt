package com.etaratasy.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etaratasy.app.FileUtils
import com.etaratasy.app.model.Citoyen
import com.etaratasy.app.model.DocumentNumerique
import com.etaratasy.app.ui.components.BoutonSecondaire
import com.etaratasy.app.ui.components.EcranHeader
import com.etaratasy.app.ui.theme.EtataColors

/** Rendu façon "certificat" du document numérique, consultable et vérifiable dans l'application. */
@Composable
fun DocumentApercuScreen(
    document: DocumentNumerique,
    citoyen: Citoyen,
    onRetour: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 60.dp)
    ) {
        EcranHeader(titre = "Document numérique", onRetour = onRetour)

        Column(Modifier.padding(horizontal = 20.dp)) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = EtataColors.Surface,
                border = BorderStroke(1.dp, EtataColors.Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(22.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "RÉPUBLIQUE DE MADAGASCAR",
                            fontSize = 10.sp, letterSpacing = 1.sp, color = EtataColors.InkSoft,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Default.CheckCircle, null, tint = EtataColors.Vert, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(document.guichet.label, fontSize = 10.sp, color = EtataColors.InkSoft)

                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(color = EtataColors.Line)
                    Spacer(Modifier.height(20.dp))

                    Text(
                        document.nom, fontSize = 21.sp, fontWeight = FontWeight.SemiBold,
                        color = EtataColors.Ink, lineHeight = 27.sp
                    )

                    Spacer(Modifier.height(20.dp))
                    
                    // --- Section Identité ---
                    Text("IDENTITÉ DU TITULAIRE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EtataColors.InkSoft)
                    Spacer(Modifier.height(10.dp))
                    LigneChamp("Nom complet", citoyen.nomComplet)
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Box(Modifier.weight(1f)) { LigneChamp("Date de naissance", citoyen.dateNaissance) }
                        Box(Modifier.weight(1f)) { LigneChamp("Lieu de naissance", citoyen.lieuNaissance) }
                    }
                    
                    if (document.typeDocumentId == "arr_cin") {
                        Spacer(Modifier.height(12.dp))
                        LigneChamp("Numéro CIN", citoyen.numeroCin, mono = true)
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    LigneChamp("Profession", citoyen.profession)

                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(color = EtataColors.Line)
                    Spacer(Modifier.height(16.dp))

                    // --- Section Filiation / Domicile ---
                    Text("FILIATION ET DOMICILE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EtataColors.InkSoft)
                    Spacer(Modifier.height(10.dp))
                    LigneChamp("Père", citoyen.pere)
                    Spacer(Modifier.height(12.dp))
                    LigneChamp("Mère", citoyen.mere)
                    Spacer(Modifier.height(12.dp))
                    LigneChamp("Adresse", citoyen.adresse)
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Box(Modifier.weight(1f)) { LigneChamp("Fokontany", citoyen.fokontany) }
                        Box(Modifier.weight(1f)) { LigneChamp("Arrondissement", citoyen.arrondissement) }
                    }

                    // --- Métadonnées spécifiques ---
                    if (document.metadonnees.isNotEmpty()) {
                        Spacer(Modifier.height(20.dp))
                        HorizontalDivider(color = EtataColors.Line)
                        Spacer(Modifier.height(16.dp))
                        Text("DÉTAILS DU DOCUMENT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EtataColors.InkSoft)
                        Spacer(Modifier.height(10.dp))
                        
                        document.metadonnees.forEach { (cle, valeur) ->
                            val label = when(cle) {
                                "dateDelivrance" -> "Date de délivrance"
                                "lieuDelivrance" -> "Lieu de délivrance"
                                "infoMaison" -> "Information sur le domicile"
                                "ville" -> "Ville"
                                "pays" -> "Pays"
                                "categorie" -> "Catégorie"
                                "validite" -> "Valable jusqu'au"
                                else -> cle
                            }
                            LigneChamp(label, valeur)
                            Spacer(Modifier.height(8.dp))
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(color = EtataColors.Line)
                    Spacer(Modifier.height(16.dp))

                    Text("RÉFÉRENCES ADMINISTRATIVES", fontSize = 10.sp, color = EtataColors.InkSoft)
                    Spacer(Modifier.height(8.dp))
                    LigneChamp("Acte de naissance", citoyen.numeroActe.toString(), mono = true)
                    Spacer(Modifier.height(12.dp))
                    Text("Référence de vérification", fontSize = 10.sp, color = EtataColors.InkSoft)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        document.reference, fontSize = 16.sp, fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp, color = EtataColors.Rouge
                    )

                    if (document.permanent) {
                        Spacer(Modifier.height(16.dp))
                        Surface(shape = RoundedCornerShape(10.dp), color = EtataColors.VertSoft) {
                            Text(
                                "Pièce officielle permanente — conservée dans votre portefeuille et non supprimable.",
                                fontSize = 12.sp, color = EtataColors.Vert, lineHeight = 17.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            Text(
                "Ce document numérique fait foi au même titre que sa version papier. " +
                    "Il peut être vérifié par un agent à partir de la référence ci-dessus.",
                fontSize = 11.sp, color = EtataColors.InkSoft, lineHeight = 16.sp
            )

            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth()) {
                Box(Modifier.weight(1f)) {
                    BoutonSecondaire(
                        "Télécharger",
                        icone = Icons.Default.Download
                    ) {
                        val path = document.pdfUrl ?: "pdfs/fkt_residence_template.pdf"
                        val success = FileUtils.telechargerPdfDepuisAssets(
                            context,
                            path,
                            "${document.nom.replace(" ", "_")}.pdf"
                        )
                        if (success) {
                            android.widget.Toast.makeText(context, "Document enregistré dans vos téléchargements", android.widget.Toast.LENGTH_LONG).show()
                        } else {
                            android.widget.Toast.makeText(context, "Erreur lors du téléchargement", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                Spacer(Modifier.width(12.dp))
                Box(Modifier.weight(1f)) {
                    BoutonSecondaire(
                        "Imprimer",
                        icone = Icons.Default.Print
                    ) {
                        // Simulation de l'impression (plus complexe à mocker réellement)
                        android.widget.Toast.makeText(context, "Envoi vers l'imprimante...", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

@Composable
private fun LigneChamp(intitule: String, valeur: String, mono: Boolean = false) {
    Column {
        Text(intitule, fontSize = 11.sp, color = EtataColors.InkSoft)
        Spacer(Modifier.height(2.dp))
        Text(
            valeur, fontSize = 14.sp, color = EtataColors.Ink,
            fontFamily = if (mono) FontFamily.Monospace else FontFamily.Default
        )
    }
}
