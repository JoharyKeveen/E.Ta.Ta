package com.etaratasy.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etaratasy.app.data.Demande
import com.etaratasy.app.ui.components.EcranHeader
import com.etaratasy.app.ui.components.EtatVide
import com.etaratasy.app.ui.components.PastilleStatut
import com.etaratasy.app.ui.theme.EtataColors

@Composable
fun DemandesScreen(
    demandes: List<Demande>,
    nonLues: Int,
    onNotifications: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            EcranHeader(
                titre = "Mes demandes",
                sousTitre = if (demandes.isEmpty()) "Aucune demande en cours"
                else "${demandes.size} demande(s) enregistrée(s)",
                onCloche = onNotifications,
                nonLues = nonLues
            )
        }

        if (demandes.isEmpty()) {
            item {
                EtatVide(
                    "Vous n'avez encore déposé aucune demande.",
                    "Rendez-vous dans l'onglet Accueil pour en créer une."
                )
            }
        }

        items(demandes, key = { it.id }) { d ->
            Box(Modifier.padding(horizontal = 20.dp, vertical = 5.dp)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EtataColors.Surface,
                    border = BorderStroke(1.dp, EtataColors.Line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    d.nomDocument,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = EtataColors.Ink,
                                    lineHeight = 19.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "${d.guichet.label} · déposée le ${d.dateDepot}",
                                    fontSize = 12.sp, color = EtataColors.InkSoft
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            PastilleStatut(d.statut)
                        }
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider(color = EtataColors.Line)
                        Spacer(Modifier.height(10.dp))
                        Text("Référence du récépissé", fontSize = 10.sp, color = EtataColors.InkSoft)
                        Spacer(Modifier.height(3.dp))
                        Text(
                            d.reference,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            color = EtataColors.Rouge
                        )
                    }
                }
            }
        }
    }
}
