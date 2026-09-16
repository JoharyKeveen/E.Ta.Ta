package com.etaratasy.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etaratasy.app.model.Catalogue
import com.etaratasy.app.model.RendezVous
import com.etaratasy.app.model.TypeDocument
import com.etaratasy.app.ui.components.*
import com.etaratasy.app.ui.theme.EtataColors

@Composable
fun RendezVousScreen(
    rendezVous: List<RendezVous>,
    nonLues: Int,
    onNotifications: () -> Unit,
    onPrendreRdv: (TypeDocument, String, String) -> Unit,
    onAnnuler: (Long) -> Unit,
    onRetrait: (Long) -> Unit
) {
    var docSelectionne by remember { mutableStateOf<TypeDocument?>(null) }

    val doc = docSelectionne
    if (doc != null) {
        FormulaireRdv(
            doc = doc,
            onRetour = { docSelectionne = null },
            onConfirmer = { date, heure ->
                onPrendreRdv(doc, date, heure)
                docSelectionne = null
            }
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            EcranHeader(
                titre = "Rendez-vous",
                sousTitre = "Documents exigeant un passage au guichet",
                onCloche = onNotifications,
                nonLues = nonLues
            )
        }

        if (rendezVous.isNotEmpty()) {
            item { LabelSection("Vos rendez-vous", Modifier.padding(horizontal = 20.dp)) }
            items(rendezVous, key = { it.id }) { r ->
                val typeDoc = Catalogue.parId(r.typeDocumentId)
                val estPieceOfficielle = typeDoc?.pieceOfficiellePermanente == true
                Box(Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EtataColors.Ink,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(r.nomDocument, fontSize = 14.sp, color = Color.White)
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "${r.date} à ${r.heure} · ${r.guichet.label}",
                                        fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f)
                                    )
                                }
                                IconButton(onClick = { onAnnuler(r.id) }, modifier = Modifier.size(28.dp)) {
                                    Icon(
                                        Icons.Default.Close, "Annuler",
                                        tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(17.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.08f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable { onRetrait(r.id) }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle, null,
                                        tint = Color.White, modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        if (estPieceOfficielle)
                                            "J'ai retiré ce document — l'ajouter en pièce permanente"
                                        else
                                            "J'ai retiré ce document",
                                        fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(22.dp)) }
        }

        item { LabelSection("Choisir un document", Modifier.padding(horizontal = 20.dp)) }
        items(Catalogue.documentsSpeciaux, key = { it.id }) { d ->
            Box(Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                LigneDocument(d.nom, d.guichet.label) { docSelectionne = d }
            }
        }
    }
}

@Composable
private fun FormulaireRdv(
    doc: TypeDocument,
    onRetour: () -> Unit,
    onConfirmer: (String, String) -> Unit
) {
    var date by remember { mutableStateOf("") }
    var heure by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(bottom = 100.dp)
    ) {
        EcranHeader(titre = "Prendre rendez-vous", sousTitre = doc.nom, onRetour = onRetour)

        Column(Modifier.padding(horizontal = 20.dp)) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = EtataColors.Surface,
                border = BorderStroke(1.dp, EtataColors.Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, null, tint = EtataColors.InkSoft, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(doc.guichet.label, fontSize = 13.sp, color = EtataColors.InkSoft)
                }
            }

            if (doc.pieceOfficiellePermanente) {
                Spacer(Modifier.height(12.dp))
                Surface(shape = RoundedCornerShape(10.dp), color = EtataColors.VertSoft, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Une fois retiré(e), ce document restera affiché en permanence dans votre " +
                            "portefeuille et ne pourra pas être supprimé.",
                        fontSize = 12.sp, color = EtataColors.Vert, lineHeight = 17.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(Modifier.height(22.dp))
            Text("Date souhaitée", fontSize = 12.sp, color = EtataColors.InkSoft)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                placeholder = { Text("JJ/MM/AAAA", color = EtataColors.InkSoft, fontSize = 14.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EtataColors.Ink,
                    unfocusedBorderColor = EtataColors.Line,
                    focusedContainerColor = EtataColors.Surface,
                    unfocusedContainerColor = EtataColors.Surface
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))
            Text("Créneau", fontSize = 12.sp, color = EtataColors.InkSoft)
            Spacer(Modifier.height(8.dp))
            Catalogue.creneaux.chunked(3).forEach { ligne ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    ligne.forEach { h ->
                        PuceChoix(h, heure == h, { heure = h }, Modifier.weight(1f))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            BoutonPrincipal(
                texte = "Confirmer le rendez-vous",
                couleur = EtataColors.Rouge,
                actif = date.isNotBlank() && heure.isNotBlank()
            ) { onConfirmer(date, heure) }
        }
    }
}
