package com.etaratasy.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etaratasy.app.ui.components.BoutonPrincipal
import com.etaratasy.app.ui.components.ChampTexte
import com.etaratasy.app.ui.theme.EtataColors

/**
 * Écran de connexion.
 *
 * L'identifiant unique du citoyen est le NUMÉRO DE SON ACTE DE NAISSANCE.
 * Il n'y a pas de création de compte libre : le numéro doit exister dans le registre
 * d'état civil, ce qui évite les comptes fictifs et rattache chaque demande à une
 * identité légale réelle.
 */
@Composable
fun ConnexionScreen(
    onVerifierNumero: (String) -> String?,
    onVerifierCode: (String) -> String?
) {
    var etape by remember { mutableStateOf(0) } // 0 = acte, 1 = code SMS
    var numeroActe by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var erreur by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EtataColors.Paper)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 56.dp, bottom = 32.dp)
    ) {
        Surface(shape = RoundedCornerShape(14.dp), color = EtataColors.Rouge, modifier = Modifier.size(50.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Description, null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(Modifier.height(22.dp))
        Text("E-TaraTasy", fontSize = 32.sp, fontWeight = FontWeight.SemiBold, color = EtataColors.Ink)
        Spacer(Modifier.height(8.dp))
        Text(
            "Vos documents administratifs sans file d'attente. Fokontany et Arrondissement, dans votre poche.",
            fontSize = 14.sp, color = EtataColors.InkSoft, lineHeight = 20.sp
        )

        Spacer(Modifier.height(40.dp))

        if (etape == 0) {
            Text(
                "Numéro de votre acte de naissance",
                fontSize = 12.sp, fontWeight = FontWeight.Medium, color = EtataColors.InkSoft
            )
            Spacer(Modifier.height(8.dp))
            ChampTexte(
                valeur = numeroActe,
                onChange = { numeroActe = it; erreur = null },
                placeholder = "0453/2001-101",
                erreur = erreur
            )
            Spacer(Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = EtataColors.AmbreSoft,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        "Où trouver ce numéro ?",
                        fontSize = 12.sp, fontWeight = FontWeight.Medium, color = EtataColors.Ambre
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Il figure en haut de votre copie d'acte de naissance : numéro d'ordre, " +
                            "année d'enregistrement, puis code de la commune. Exemple : 0453/2001-101.",
                        fontSize = 12.sp, color = EtataColors.Ambre, lineHeight = 17.sp
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, null, tint = EtataColors.InkSoft, modifier = Modifier.size(13.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    "Connexion sécurisée après activation du compte au Fokontany.",
                    fontSize = 12.sp, color = EtataColors.InkSoft
                )
            }
            Spacer(Modifier.height(32.dp))
            BoutonPrincipal("Vérifier mon identité") {
                erreur = onVerifierNumero(numeroActe)
                if (erreur == null) etape = 1
            }
        } else {
            Text(
                "Code reçu par SMS",
                fontSize = 12.sp, fontWeight = FontWeight.Medium, color = EtataColors.InkSoft
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = code,
                onValueChange = { if (it.length <= 6) { code = it; erreur = null } },
                placeholder = { Text("• • • • • •", color = EtataColors.InkSoft) },
                singleLine = true,
                isError = erreur != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
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
                Text(erreur!!, fontSize = 12.sp, color = EtataColors.Rouge)
            }
            Spacer(Modifier.height(10.dp))
            Text(
                "Acte n° $numeroActe — un code à 6 chiffres a été envoyé au numéro " +
                    "enregistré à votre nom.",
                fontSize = 12.sp, color = EtataColors.InkSoft, lineHeight = 17.sp
            )
            Spacer(Modifier.height(32.dp))
            BoutonPrincipal("Se connecter") {
                erreur = onVerifierCode(code)
            }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = { etape = 0; code = ""; erreur = null }, modifier = Modifier.fillMaxWidth()) {
                Text("Modifier le numéro d'acte", color = EtataColors.InkSoft, fontSize = 13.sp)
            }
        }
    }
}
