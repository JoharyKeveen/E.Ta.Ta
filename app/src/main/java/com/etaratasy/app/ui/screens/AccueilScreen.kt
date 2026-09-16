package com.etaratasy.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etaratasy.app.model.Catalogue
import com.etaratasy.app.model.Citoyen
import com.etaratasy.app.model.TypeDocument
import com.etaratasy.app.ui.components.*
import com.etaratasy.app.ui.theme.EtataColors

@Composable
fun AccueilScreen(
    citoyen: Citoyen,
    nonLues: Int,
    onNotifications: () -> Unit,
    onDemander: (TypeDocument) -> Unit,
    onDocumentsSpeciaux: () -> Unit,
    onModule: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            EcranHeader(
                titre = "Manao ahoana, ${citoyen.prenoms.substringBefore(' ')}",
                sousTitre = "Que souhaitez-vous demander aujourd'hui ?",
                onCloche = onNotifications,
                nonLues = nonLues
            )
        }

        // Carte d'identité numérique fondée sur l'acte de naissance
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = EtataColors.Ink,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text("Identifiant national", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                    Spacer(Modifier.height(6.dp))
                    Text(
                        citoyen.numeroActe.toString(),
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Numéro d'acte de naissance",
                        fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(14.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                    Spacer(Modifier.height(12.dp))
                    Text(citoyen.nomComplet, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Né(e) le ${citoyen.dateNaissance} · ${citoyen.fokontany}",
                        fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f), lineHeight = 15.sp
                    )
                }
            }
        }

        item { Spacer(Modifier.height(26.dp)) }

        item {
            LabelSection("Certificats du Fokontany", Modifier.padding(horizontal = 20.dp))
        }
        items(Catalogue.certificatsFokontany, key = { it.id }) { doc ->
            Box(Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                LigneDocument(doc.nom, "Généré numériquement, sans déplacement") { onDemander(doc) }
            }
        }

        item { Spacer(Modifier.height(22.dp)) }

        item {
            Column(Modifier.padding(horizontal = 20.dp)) {
                LabelSection("Autre demande")
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EtataColors.VertSoft,
                    modifier = Modifier.fillMaxWidth()
                        .clickable { onDemander(Catalogue.autresDocuments.first()) }
                ) {
                    Row(
                        Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.School, null, tint = EtataColors.Vert, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Demande de diplôme (BACC)",
                            fontSize = 14.sp, fontWeight = FontWeight.Medium, color = EtataColors.Vert
                        )
                    }
                }
            }
        }

        item { Spacer(Modifier.height(22.dp)) }

        item {
            Column(Modifier.padding(horizontal = 20.dp)) {
                LabelSection("Documents spéciaux (sur rendez-vous)")
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EtataColors.Surface,
                    border = BorderStroke(1.dp, EtataColors.Line),
                    modifier = Modifier.fillMaxWidth().clickable(onClick = onDocumentsSpeciaux)
                ) {
                    Row(
                        Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarMonth, null, tint = EtataColors.Rouge, modifier = Modifier.size(19.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "CIN, permis, actes d'Arrondissement…",
                                fontSize = 14.sp, color = EtataColors.Ink
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "CIN et permis restent ensuite dans votre portefeuille",
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

        item { Spacer(Modifier.height(22.dp)) }

        item {
            Column(Modifier.padding(horizontal = 20.dp)) {
                LabelSection("Modules")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CarteModule("Partage", Icons.Default.Share, Modifier.weight(1f)) { onModule("partage") }
                    CarteModule("Santé", Icons.Default.MonitorHeart, Modifier.weight(1f)) { onModule("sante") }
                    CarteModule("Contrôle", Icons.Default.Shield, Modifier.weight(1f)) { onModule("controle") }
                }
            }
        }
    }
}

@Composable
private fun CarteModule(
    titre: String,
    icone: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = EtataColors.Surface,
        border = BorderStroke(1.dp, EtataColors.Line),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            Modifier.padding(vertical = 16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icone, null, tint = EtataColors.Ink, modifier = Modifier.size(19.dp))
            Spacer(Modifier.height(8.dp))
            Text(titre, fontSize = 11.sp, color = EtataColors.Ink)
        }
    }
}
