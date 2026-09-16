package com.etaratasy.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etaratasy.app.model.StatutDemande
import com.etaratasy.app.ui.theme.EtataColors

@Composable
fun EcranHeader(
    titre: String,
    sousTitre: String? = null,
    onRetour: (() -> Unit)? = null,
    onCloche: (() -> Unit)? = null,
    nonLues: Int = 0
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.Top, modifier = Modifier.weight(1f)) {
            if (onRetour != null) {
                IconButton(onClick = onRetour, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour", tint = EtataColors.Ink)
                }
                Spacer(Modifier.width(8.dp))
            }
            Column {
                Text(titre, fontSize = 25.sp, fontWeight = FontWeight.SemiBold, color = EtataColors.Ink)
                if (sousTitre != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(sousTitre, fontSize = 13.sp, color = EtataColors.InkSoft, lineHeight = 18.sp)
                }
            }
        }
        if (onCloche != null) {
            Box {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = EtataColors.Surface,
                    border = BorderStroke(1.dp, EtataColors.Line),
                    modifier = Modifier.size(40.dp).clickable { onCloche() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Notifications, "Notifications", tint = EtataColors.Ink, modifier = Modifier.size(18.dp))
                    }
                }
                if (nonLues > 0) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = EtataColors.Rouge,
                        modifier = Modifier.size(17.dp).align(Alignment.TopEnd)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("$nonLues", color = Color.White, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LabelSection(texte: String, modifier: Modifier = Modifier) {
    Text(
        texte.uppercase(),
        fontSize = 11.sp,
        letterSpacing = 0.8.sp,
        fontWeight = FontWeight.Medium,
        color = EtataColors.InkSoft,
        modifier = modifier.padding(bottom = 10.dp)
    )
}

@Composable
fun PastilleStatut(statut: StatutDemande) {
    val (fond, texte) = when (statut) {
        StatutDemande.DEPOSEE, StatutDemande.EN_TRAITEMENT -> EtataColors.AmbreSoft to EtataColors.Ambre
        StatutDemande.PRETE -> EtataColors.VertSoft to EtataColors.Vert
        StatutDemande.RETIREE -> EtataColors.Line to EtataColors.InkSoft
        StatutDemande.REJETEE -> EtataColors.RougeSoft to EtataColors.Rouge
    }
    Pastille(statut.label, fond, texte)
}

@Composable
fun Pastille(texte: String, fond: Color, couleurTexte: Color) {
    Surface(shape = RoundedCornerShape(50), color = fond) {
        Text(
            texte,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = couleurTexte,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
        )
    }
}

/** Ligne cliquable standard de la liste des documents. */
@Composable
fun LigneDocument(
    titre: String,
    sousTitre: String? = null,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = EtataColors.Surface,
        border = BorderStroke(1.dp, EtataColors.Line),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(titre, fontSize = 14.sp, color = EtataColors.Ink, lineHeight = 19.sp)
                if (sousTitre != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(sousTitre, fontSize = 11.sp, color = EtataColors.InkSoft)
                }
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight, null,
                tint = EtataColors.InkSoft, modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun BoutonPrincipal(
    texte: String,
    modifier: Modifier = Modifier,
    couleur: Color = EtataColors.Ink,
    actif: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = actif,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = couleur, contentColor = Color.White),
        modifier = modifier.fillMaxWidth().height(50.dp)
    ) {
        Text(texte, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun BoutonSecondaire(
    texte: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = EtataColors.Ink),
        border = BorderStroke(1.dp, EtataColors.Line),
        modifier = modifier.fillMaxWidth().height(46.dp)
    ) {
        Text(texte, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ChampTexte(
    valeur: String,
    onChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    erreur: String? = null
) {
    Column(modifier) {
        OutlinedTextField(
            value = valeur,
            onValueChange = onChange,
            placeholder = { Text(placeholder, color = EtataColors.InkSoft, fontSize = 14.sp) },
            singleLine = true,
            isError = erreur != null,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EtataColors.Ink,
                unfocusedBorderColor = EtataColors.Line,
                focusedContainerColor = EtataColors.Surface,
                unfocusedContainerColor = EtataColors.Surface
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (erreur != null) {
            Spacer(Modifier.height(6.dp))
            Text(erreur, fontSize = 12.sp, color = EtataColors.Rouge)
        }
    }
}

@Composable
fun EtatVide(message: String, detail: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, fontSize = 13.sp, color = EtataColors.InkSoft, textAlign = TextAlign.Center)
        Spacer(Modifier.height(4.dp))
        Text(detail, fontSize = 12.sp, color = EtataColors.InkSoft, textAlign = TextAlign.Center)
    }
}

@Composable
fun PuceChoix(texte: String, selectionne: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selectionne) EtataColors.Ink else EtataColors.Surface,
        border = BorderStroke(1.dp, if (selectionne) EtataColors.Ink else EtataColors.Line),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(Modifier.padding(vertical = 11.dp), contentAlignment = Alignment.Center) {
            Text(
                texte,
                fontSize = 13.sp,
                color = if (selectionne) Color.White else EtataColors.Ink
            )
        }
    }
}
