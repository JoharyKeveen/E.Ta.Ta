package com.etaratasy.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etaratasy.app.model.Citoyen
import com.etaratasy.app.model.DocumentNumerique
import com.etaratasy.app.ui.components.EcranHeader
import com.etaratasy.app.ui.theme.EtataColors

/** Rendu façon "certificat" du document numérique, consultable et vérifiable dans l'application. */
@Composable
fun DocumentApercuScreen(
    document: DocumentNumerique,
    citoyen: Citoyen,
    onRetour: () -> Unit
) {
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
                    LigneChamp("Nom complet", citoyen.nomComplet)
                    Spacer(Modifier.height(12.dp))
                    LigneChamp("Numéro d'acte de naissance", citoyen.numeroActe.toString(), mono = true)
                    Spacer(Modifier.height(12.dp))
                    LigneChamp("Fokontany", citoyen.fokontany)
                    Spacer(Modifier.height(12.dp))
                    LigneChamp("Date d'émission", document.dateEmission)

                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(color = EtataColors.Line)
                    Spacer(Modifier.height(16.dp))

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
