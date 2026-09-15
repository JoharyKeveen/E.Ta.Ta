package com.etaratasy.app.ui.screens.modules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etaratasy.app.data.Catalogue
import com.etaratasy.app.data.DureePartage
import com.etaratasy.app.data.PartageAcces
import com.etaratasy.app.ui.components.*
import com.etaratasy.app.ui.theme.EtataColors

/**
 * Module de demande d'accès temporaire aux documents.
 * Le citoyen génère un code à durée limitée qu'un tiers (banque, employeur, notaire)
 * peut saisir pour consulter le document sans que celui-ci circule en copie papier.
 */
@Composable
fun PartageScreen(
    partages: List<PartageAcces>,
    onRetour: () -> Unit,
    onCreer: (String, DureePartage) -> Unit,
    onRevoquer: (Long) -> Unit
) {
    var document by remember { mutableStateOf(Catalogue.certificatsFokontany.first().nom) }
    var duree by remember { mutableStateOf(DureePartage.H24) }
    var menuOuvert by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 60.dp)
    ) {
        item {
            EcranHeader(
                titre = "Partage de document",
                sousTitre = "Donner un accès temporaire à un tiers",
                onRetour = onRetour
            )
        }

        item {
            Column(Modifier.padding(horizontal = 20.dp)) {
                Text("Document à partager", fontSize = 12.sp, color = EtataColors.InkSoft)
                Spacer(Modifier.height(8.dp))
                Box {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = EtataColors.Surface,
                        border = BorderStroke(1.dp, EtataColors.Line),
                        modifier = Modifier.fillMaxWidth().clickable { menuOuvert = true }
                    ) {
                        Row(
                            Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(document, fontSize = 14.sp, color = EtataColors.Ink, modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ExpandMore, null, tint = EtataColors.InkSoft, modifier = Modifier.size(18.dp))
                        }
                    }
                    DropdownMenu(expanded = menuOuvert, onDismissRequest = { menuOuvert = false }) {
                        Catalogue.certificatsFokontany.forEach { d ->
                            DropdownMenuItem(
                                text = { Text(d.nom, fontSize = 13.sp) },
                                onClick = { document = d.nom; menuOuvert = false }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
                Text("Durée d'accès", fontSize = 12.sp, color = EtataColors.InkSoft)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DureePartage.entries.forEach { d ->
                        PuceChoix(d.label, duree == d, { duree = d }, Modifier.weight(1f))
                    }
                }

                Spacer(Modifier.height(22.dp))
                BoutonPrincipal("Générer un code d'accès") { onCreer(document, duree) }

                Spacer(Modifier.height(30.dp))
                LabelSection("Accès générés")
            }
        }

        if (partages.isEmpty()) {
            item {
                Text(
                    "Aucun partage en cours.",
                    fontSize = 13.sp, color = EtataColors.InkSoft,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        items(partages, key = { it.id }) { p ->
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
                                Text(p.nomDocument, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = EtataColors.Ink)
                                Spacer(Modifier.height(4.dp))
                                Text("Valable ${p.duree.label}", fontSize = 12.sp, color = EtataColors.InkSoft)
                            }
                            if (p.actif) {
                                Pastille("Actif", EtataColors.VertSoft, EtataColors.Vert)
                            } else {
                                Pastille("Révoqué", EtataColors.Line, EtataColors.InkSoft)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider(color = EtataColors.Line)
                        Spacer(Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                p.code,
                                fontSize = 17.sp,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 3.sp,
                                color = if (p.actif) EtataColors.Rouge else EtataColors.InkSoft,
                                modifier = Modifier.weight(1f)
                            )
                            if (p.actif) {
                                IconButton(onClick = { onRevoquer(p.id) }, modifier = Modifier.size(28.dp)) {
                                    Icon(
                                        Icons.Default.DeleteOutline, "Révoquer",
                                        tint = EtataColors.InkSoft, modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
