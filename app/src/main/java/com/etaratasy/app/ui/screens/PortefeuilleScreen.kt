package com.etaratasy.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etaratasy.app.model.DocumentNumerique
import com.etaratasy.app.ui.components.EcranHeader
import com.etaratasy.app.ui.components.EtatVide
import com.etaratasy.app.ui.components.LabelSection
import com.etaratasy.app.ui.theme.EtataColors

/**
 * Portefeuille numérique.
 *
 * - "Pièces officielles" : CIN et permis, une fois retirés au guichet. Permanentes,
 *   aucune action de suppression n'est proposée à l'écran.
 * - "Documents disponibles" : certificats Fokontany et diplôme, générés et visibles
 *   ici dès le dépôt de la demande, sans rendez-vous ni déplacement.
 */
@Composable
fun PortefeuilleScreen(
    piecesOfficielles: List<DocumentNumerique>,
    documentsNumeriques: List<DocumentNumerique>,
    nonLues: Int,
    onNotifications: () -> Unit,
    onOuvrirDocument: (DocumentNumerique) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            EcranHeader(
                titre = "Portefeuille",
                sousTitre = "Vos documents numériques, toujours avec vous",
                onCloche = onNotifications,
                nonLues = nonLues
            )
        }

        item { LabelSection("Pièces officielles", Modifier.padding(horizontal = 20.dp)) }
        if (piecesOfficielles.isEmpty()) {
            item {
                Box(Modifier.padding(horizontal = 20.dp)) {
                    EtatVide(
                        "Aucune pièce officielle pour le moment.",
                        "La CIN et le permis de conduire apparaîtront ici, de façon permanente, dès leur retrait au guichet."
                    )
                }
            }
        }
        items(piecesOfficielles, key = { it.id }) { p ->
            Box(Modifier.padding(horizontal = 20.dp, vertical = 5.dp)) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = EtataColors.Ink,
                    modifier = Modifier.fillMaxWidth().clickable { onOuvrirDocument(p) }
                ) {
                    Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Badge, null, tint = Color.White, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(p.nom, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.White)
                            Spacer(Modifier.height(3.dp))
                            Text(
                                "Délivrée le ${p.dateEmission} · permanente",
                                fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                        Icon(Icons.Default.Lock, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(15.dp))
                    }
                }
            }
        }

        item { Spacer(Modifier.height(22.dp)) }
        item { LabelSection("Documents disponibles", Modifier.padding(horizontal = 20.dp)) }
        if (documentsNumeriques.isEmpty()) {
            item {
                Box(Modifier.padding(horizontal = 20.dp)) {
                    EtatVide(
                        "Aucun document généré pour le moment.",
                        "Demandez un certificat depuis l'onglet Accueil : il apparaît ici instantanément, sans file d'attente."
                    )
                }
            }
        }
        items(documentsNumeriques, key = { it.id }) { d ->
            Box(Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EtataColors.Surface,
                    border = BorderStroke(1.dp, EtataColors.Line),
                    modifier = Modifier.fillMaxWidth().clickable { onOuvrirDocument(d) }
                ) {
                    Row(
                        Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(d.nom, fontSize = 14.sp, color = EtataColors.Ink)
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "${d.guichet.label} · ${d.dateEmission}",
                                fontSize = 11.sp, color = EtataColors.InkSoft
                            )
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight, null,
                            tint = EtataColors.InkSoft, modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
