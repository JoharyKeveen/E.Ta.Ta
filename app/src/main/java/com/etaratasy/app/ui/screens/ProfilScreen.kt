package com.etaratasy.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etaratasy.app.model.Citoyen
import com.etaratasy.app.ui.components.EcranHeader
import com.etaratasy.app.ui.components.LabelSection
import com.etaratasy.app.ui.theme.EtataColors

@Composable
fun ProfilScreen(
    citoyen: Citoyen,
    nonLues: Int,
    onNotifications: () -> Unit,
    onModule: (String) -> Unit,
    onDeconnexion: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
    ) {
        EcranHeader(titre = "Profil", onCloche = onNotifications, nonLues = nonLues)

        Column(Modifier.padding(horizontal = 20.dp)) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = EtataColors.Surface,
                border = BorderStroke(1.dp, EtataColors.Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = EtataColors.Ink, modifier = Modifier.size(48.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(citoyen.initiales, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(citoyen.nomComplet, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = EtataColors.Ink)
                            Spacer(Modifier.height(2.dp))
                            Text(
                                if (citoyen.compteActive) "Compte activé" else "Compte non activé",
                                fontSize = 12.sp, color = EtataColors.InkSoft
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = EtataColors.Line)
                    Spacer(Modifier.height(14.dp))
                    LigneInfo("Numéro d'acte de naissance", citoyen.numeroActe.toString(), mono = true)
                    Spacer(Modifier.height(10.dp))
                    LigneInfo("Date de naissance", citoyen.dateNaissance)
                    Spacer(Modifier.height(10.dp))
                    LigneInfo("Fokontany de rattachement", citoyen.fokontany)
                    Spacer(Modifier.height(10.dp))
                    LigneInfo("Téléphone", citoyen.telephone)
                }
            }

            Spacer(Modifier.height(26.dp))
            LabelSection("Modules")
            LigneModule("Partage de documents", Icons.Default.Share) { onModule("partage") }
            Spacer(Modifier.height(8.dp))
            LigneModule("Informations santé", Icons.Default.MonitorHeart) { onModule("sante") }
            Spacer(Modifier.height(8.dp))
            LigneModule("Contrôle des forces de l'ordre", Icons.Default.Shield) { onModule("controle") }

            Spacer(Modifier.height(26.dp))
            TextButton(onClick = onDeconnexion, modifier = Modifier.fillMaxWidth()) {
                Text("Se déconnecter", color = EtataColors.Rouge, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun LigneInfo(intitule: String, valeur: String, mono: Boolean = false) {
    Column {
        Text(intitule, fontSize = 11.sp, color = EtataColors.InkSoft)
        Spacer(Modifier.height(2.dp))
        Text(
            valeur,
            fontSize = 13.sp,
            color = EtataColors.Ink,
            fontFamily = if (mono) FontFamily.Monospace else FontFamily.Default
        )
    }
}

@Composable
private fun LigneModule(titre: String, icone: ImageVector, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = EtataColors.Surface,
        border = BorderStroke(1.dp, EtataColors.Line),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icone, null, tint = EtataColors.Ink, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(12.dp))
            Text(titre, fontSize = 14.sp, color = EtataColors.Ink, modifier = Modifier.weight(1f))
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight, null,
                tint = EtataColors.InkSoft, modifier = Modifier.size(18.dp)
            )
        }
    }
}
