package com.etaratasy.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etaratasy.app.R
import com.etaratasy.app.ui.components.BoutonPrincipal
import com.etaratasy.app.ui.components.ChampTexte
import com.etaratasy.app.ui.theme.EtataColors
import com.etaratasy.app.SecurityUtils

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
    sessionSauvegardee: Boolean,
    onVerifierNumero: (String) -> String?,
    onVerifierCode: (String) -> String?,
    onBiometrie: () -> Unit
) {
    var etape by remember { mutableStateOf(0) } // 0 = acte, 1 = code SMS
    var numeroActe by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var erreur by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    var showManualCode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EtataColors.Paper)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 56.dp, bottom = 32.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.Transparent,
            modifier = Modifier.size(80.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_app),
                contentDescription = "Logo E-TaraTasy",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
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

            if (sessionSauvegardee) {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onBiometrie,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EtataColors.Ink),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EtataColors.Line),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Icon(androidx.compose.material.icons.Icons.Default.Fingerprint, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Utiliser la sécurité du système", fontSize = 14.sp)
                }
            }
        } else {
            Text(
                "Code reçu par SMS",
                fontSize = 12.sp, fontWeight = FontWeight.Medium, color = EtataColors.InkSoft
            )
            Spacer(Modifier.height(8.dp))
            if (sessionSauvegardee && SecurityUtils.isBiometricAvailable(context) && !showManualCode) {
                OutlinedButton(
                    onClick = onBiometrie,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EtataColors.Ink),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EtataColors.Line),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Icon(Icons.Default.Fingerprint, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Se connecter avec la sécurité du système", fontSize = 14.sp)
                }
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = { showManualCode = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Entrer le code manuellement", color = EtataColors.InkSoft, fontSize = 13.sp)
                }
            }

            if (!sessionSauvegardee || showManualCode.not().not() || !SecurityUtils.isBiometricAvailable(context)) {
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
}
