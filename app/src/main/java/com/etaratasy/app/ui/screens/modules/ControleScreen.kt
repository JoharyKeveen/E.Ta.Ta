package com.etaratasy.app.ui.screens.modules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etaratasy.app.data.ResultatControle
import com.etaratasy.app.ui.components.EcranHeader
import com.etaratasy.app.ui.theme.EtataColors

/**
 * Module de contrôle des forces de l'ordre.
 * L'agent saisit le nom complet et n'obtient QUE la validité des pièces,
 * jamais le contenu des documents ni les données de santé.
 */
@Composable
fun ControleScreen(
    resultat: ResultatControle?,
    onRetour: () -> Unit,
    onRechercher: (String) -> Unit
) {
    var requete by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 60.dp)
    ) {
        EcranHeader(
            titre = "Contrôle des pièces",
            sousTitre = "Réservé aux agents assermentés en service",
            onRetour = onRetour
        )

        Column(Modifier.padding(horizontal = 20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = requete,
                    onValueChange = { requete = it },
                    placeholder = { Text("Nom complet de la personne", color = EtataColors.InkSoft, fontSize = 14.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EtataColors.Ink,
                        unfocusedBorderColor = EtataColors.Line,
                        focusedContainerColor = EtataColors.Surface,
                        unfocusedContainerColor = EtataColors.Surface
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = EtataColors.Ink,
                    modifier = Modifier.size(52.dp)
                ) {
                    IconButton(onClick = { onRechercher(requete) }) {
                        Icon(Icons.Default.Search, "Rechercher", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            if (resultat != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = EtataColors.Surface,
                    border = BorderStroke(1.dp, EtataColors.Line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Text(
                            resultat.nomComplet,
                            fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = EtataColors.Ink
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Acte de naissance n° ${resultat.numeroActe}",
                            fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = EtataColors.InkSoft
                        )

                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = EtataColors.Line)
                        Spacer(Modifier.height(14.dp))

                        LignePiece("Carte d'identité (CIN)", resultat.cinValide, resultat.cinDetail)
                        Spacer(Modifier.height(14.dp))
                        LignePiece("Permis de conduire", resultat.permisValide, resultat.permisDetail)

                        if (resultat.alerte != null) {
                            Spacer(Modifier.height(16.dp))
                            Surface(shape = RoundedCornerShape(10.dp), color = EtataColors.AmbreSoft) {
                                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.WarningAmber, null,
                                        tint = EtataColors.Ambre, modifier = Modifier.size(17.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(resultat.alerte, fontSize = 12.sp, color = EtataColors.Ambre, lineHeight = 17.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
                Text(
                    "Chaque consultation est horodatée et associée au matricule de l'agent.",
                    fontSize = 11.sp, color = EtataColors.InkSoft, lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun LignePiece(intitule: String, valide: Boolean, detail: String) {
    Row(verticalAlignment = Alignment.Top) {
        Column(Modifier.weight(1f)) {
            Text(intitule, fontSize = 13.sp, color = EtataColors.Ink)
            Spacer(Modifier.height(2.dp))
            Text(detail, fontSize = 12.sp, color = EtataColors.InkSoft, lineHeight = 16.sp)
        }
        Spacer(Modifier.width(10.dp))
        Icon(
            if (valide) Icons.Default.Check else Icons.Default.Close,
            null,
            tint = if (valide) EtataColors.Vert else EtataColors.Rouge,
            modifier = Modifier.size(18.dp)
        )
    }
}
