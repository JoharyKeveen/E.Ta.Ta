package com.etaratasy.app.ui.screens.modules

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etaratasy.app.data.Catalogue
import com.etaratasy.app.data.DossierSante
import com.etaratasy.app.ui.components.EcranHeader
import com.etaratasy.app.ui.components.PuceChoix
import com.etaratasy.app.ui.theme.EtataColors

/**
 * Module santé : informations vitales rattachées au numéro d'acte de naissance,
 * consultables par les secours en cas d'urgence.
 */
@Composable
fun SanteScreen(
    sante: DossierSante,
    onRetour: () -> Unit,
    onModifier: ((DossierSante) -> DossierSante) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 60.dp)
    ) {
        EcranHeader(
            titre = "Module santé",
            sousTitre = "Groupe sanguin et allergies, visibles en cas d'urgence",
            onRetour = onRetour
        )

        Column(Modifier.padding(horizontal = 20.dp)) {
            Text("Groupe sanguin", fontSize = 12.sp, color = EtataColors.InkSoft)
            Spacer(Modifier.height(8.dp))
            Catalogue.groupesSanguins.chunked(4).forEach { ligne ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    ligne.forEach { g ->
                        PuceChoix(g, sante.groupeSanguin == g, {
                            onModifier { it.copy(groupeSanguin = g) }
                        }, Modifier.weight(1f))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("Allergies connues", fontSize = 12.sp, color = EtataColors.InkSoft)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = sante.allergies,
                onValueChange = { v -> onModifier { it.copy(allergies = v) } },
                placeholder = { Text("Ex. pénicilline, arachide…", color = EtataColors.InkSoft, fontSize = 14.sp) },
                minLines = 4,
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
            Text("Personne à prévenir", fontSize = 12.sp, color = EtataColors.InkSoft)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = sante.contactUrgence,
                onValueChange = { v -> onModifier { it.copy(contactUrgence = v) } },
                placeholder = { Text("Nom et numéro", color = EtataColors.InkSoft, fontSize = 14.sp) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EtataColors.Ink,
                    unfocusedBorderColor = EtataColors.Line,
                    focusedContainerColor = EtataColors.Surface,
                    unfocusedContainerColor = EtataColors.Surface
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Surface(shape = RoundedCornerShape(10.dp), color = EtataColors.VertSoft, modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Ces informations ne sont visibles que par les services de secours autorisés, " +
                        "et uniquement à partir de votre numéro d'acte de naissance.",
                    fontSize = 12.sp, color = EtataColors.Vert, lineHeight = 17.sp,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }
    }
}
