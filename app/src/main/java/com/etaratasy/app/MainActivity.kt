package com.etaratasy.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import com.etaratasy.app.SecurityUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.etaratasy.app.model.*
import com.etaratasy.app.model.DocumentNumerique
import com.etaratasy.app.ui.components.BoutonPrincipal
import com.etaratasy.app.ui.screens.*
import com.etaratasy.app.ui.screens.modules.ControleScreen
import com.etaratasy.app.ui.screens.modules.PartageScreen
import com.etaratasy.app.ui.screens.modules.SanteScreen
import com.etaratasy.app.ui.theme.ETaraTasyTheme
import com.etaratasy.app.ui.theme.EtataColors
import com.etaratasy.app.viewmodel.EtataViewModel

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ETaraTasyTheme {
                EtataApp()
            }
        }
    }
}

private enum class Onglet(val label: String, val icone: ImageVector) {
    ACCUEIL("Accueil", Icons.Default.Home),
    DEMANDES("Demandes", Icons.AutoMirrored.Filled.ListAlt),
    RDV("RDV", Icons.Default.CalendarMonth),
    PORTEFEUILLE("Portefeuille", Icons.Default.AccountBalanceWallet),
    PROFIL("Profil", Icons.Default.Person)
}

/**
 * Composition racine (couche "View" de MVVM) : lit l'état exposé par [EtataViewModel]
 * et se contente d'appeler ses fonctions publiques. Aucune règle métier ici.
 */
@Composable
fun EtataApp(vm: EtataViewModel = viewModel()) {
    val ui by vm.ui.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    var onglet by remember { mutableStateOf(Onglet.ACCUEIL) }
    var module by remember { mutableStateOf<String?>(null) }
    var notifsOuvertes by remember { mutableStateOf(false) }
    var docAConfirmer by remember { mutableStateOf<TypeDocument?>(null) }
    var documentAffiche by remember { mutableStateOf<DocumentNumerique?>(null) }

    val citoyen = ui.citoyen

    // Écran de Chargement
    if (ui.chargement) {
        ChargementScreen()
        return
    }

    // Sécurité Biométrique (Lancement auto ou accès protégé)
    if (ui.securiteActivee && !ui.authentifieBiometrique && (ui.connecte || ui.sessionSauvegardee)) {
        LaunchedEffect(Unit) {
            SecurityUtils.showBiometricPrompt(
                activity = context as FragmentActivity,
                onSuccess = { vm.validerAuthentificationBiometrique() },
                onError = { /* Gérer l'erreur */ }
            )
        }
        SecurityGate(
            onUnlock = {
                SecurityUtils.showBiometricPrompt(
                    activity = context as FragmentActivity,
                    onSuccess = { vm.validerAuthentificationBiometrique() },
                    onError = { }
                )
            }
        )
        return
    }

    if (!ui.connecte || citoyen == null) {
        ConnexionScreen(
            sessionSauvegardee = ui.sessionSauvegardee,
            onVerifierNumero = vm::soumettreNumeroActe,
            onVerifierCode = vm::soumettreCode,
            onBiometrie = {
                SecurityUtils.showBiometricPrompt(
                    activity = context as FragmentActivity,
                    onSuccess = { vm.validerAuthentificationBiometrique() },
                    onError = { }
                )
            }
        )
        return
    }

    val doc = documentAffiche
    if (doc != null) {
        DocumentApercuScreen(document = doc, citoyen = citoyen, onRetour = { documentAffiche = null })
        return
    }

    Scaffold(
        containerColor = EtataColors.Paper,
        bottomBar = {
            if (module == null && !notifsOuvertes) {
                BarreNavigation(onglet) { onglet = it }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize().background(EtataColors.Paper)) {
            when {
                notifsOuvertes -> NotificationsScreen(
                    notifications = ui.notifications,
                    onRetour = { notifsOuvertes = false },
                    onLue = vm::marquerLue
                )

                module == "partage" -> PartageScreen(
                    partages = ui.partages,
                    onRetour = { module = null },
                    onCreer = vm::creerPartage,
                    onRevoquer = vm::revoquerPartage
                )

                module == "sante" -> SanteScreen(
                    sante = ui.sante,
                    onRetour = { module = null },
                    onModifier = vm::majSante
                )

                module == "controle" -> ControleScreen(
                    resultat = ui.resultatControle,
                    onRetour = { module = null; vm.effacerControle() },
                    onRechercher = vm::rechercherControle
                )

                onglet == Onglet.ACCUEIL -> AccueilScreen(
                    citoyen = citoyen,
                    nonLues = ui.nonLues,
                    onNotifications = { notifsOuvertes = true },
                    onDemander = { docAConfirmer = it },
                    onDocumentsSpeciaux = { onglet = Onglet.RDV },
                    onModule = { module = it }
                )

                onglet == Onglet.DEMANDES -> DemandesScreen(
                    demandes = ui.demandes,
                    documentsNumeriques = ui.documentsNumeriques,
                    nonLues = ui.nonLues,
                    onNotifications = { notifsOuvertes = true },
                    onVoirDocument = { documentAffiche = it }
                )

                onglet == Onglet.RDV -> RendezVousScreen(
                    rendezVous = ui.rendezVous,
                    nonLues = ui.nonLues,
                    onNotifications = { notifsOuvertes = true },
                    onPrendreRdv = vm::prendreRendezVous,
                    onAnnuler = vm::annulerRendezVous,
                    onRetrait = vm::marquerRetrait
                )

                onglet == Onglet.PORTEFEUILLE -> PortefeuilleScreen(
                    piecesOfficielles = ui.piecesOfficielles,
                    documentsNumeriques = ui.documentsNumeriques,
                    nonLues = ui.nonLues,
                    onNotifications = { notifsOuvertes = true },
                    onOuvrirDocument = { documentAffiche = it }
                )

                onglet == Onglet.PROFIL -> ProfilScreen(
                    citoyen = citoyen,
                    nonLues = ui.nonLues,
                    securiteActivee = ui.securiteActivee,
                    onNotifications = { notifsOuvertes = true },
                    onModule = { module = it },
                    onSecuriteToggle = vm::toggleSecurite,
                    onDeconnexion = vm::deconnexion
                )
            }
        }
    }

    val demande = docAConfirmer
    if (demande != null) {
        FeuilleConfirmation(
            doc = demande,
            reference = citoyen.numeroActe.toString(),
            onConfirmer = {
                vm.deposerDemande(demande)
                docAConfirmer = null
                onglet = Onglet.PORTEFEUILLE
            },
            onAnnuler = { docAConfirmer = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeuilleConfirmation(
    doc: TypeDocument,
    reference: String,
    onConfirmer: () -> Unit,
    onAnnuler: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onAnnuler,
        containerColor = EtataColors.Paper
    ) {
        Column(Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            Text("Nouvelle demande", fontSize = 11.sp, letterSpacing = 0.8.sp, color = EtataColors.InkSoft)
            Spacer(Modifier.height(6.dp))
            Text(doc.nom, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = EtataColors.Ink, lineHeight = 26.sp)
            Spacer(Modifier.height(6.dp))
            Text("Délivré par : ${doc.guichet.label}", fontSize = 13.sp, color = EtataColors.InkSoft)
            Spacer(Modifier.height(18.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = EtataColors.AmbreSoft,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "La demande sera rattachée à votre acte de naissance n° $reference. " +
                        "Le document sera généré immédiatement et visible dans votre portefeuille — " +
                        "aucun déplacement n'est nécessaire.",
                    fontSize = 12.sp, color = EtataColors.Ambre, lineHeight = 17.sp,
                    modifier = Modifier.padding(14.dp)
                )
            }
            Spacer(Modifier.height(22.dp))
            BoutonPrincipal("Confirmer la demande", couleur = EtataColors.Rouge, onClick = onConfirmer)
            Spacer(Modifier.height(4.dp))
            TextButton(onClick = onAnnuler, modifier = Modifier.fillMaxWidth()) {
                Text("Annuler", color = EtataColors.InkSoft, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun SecurityGate(onUnlock: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(EtataColors.Paper),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.Transparent
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_app),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(24.dp))
            Icon(
                Icons.Default.Shield,
                null,
                tint = EtataColors.Rouge,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text("Sécurisé par le système", color = EtataColors.InkSoft)
            Spacer(Modifier.height(24.dp))
            BoutonPrincipal("Déverrouiller", onClick = onUnlock)
        }
    }
}

@Composable
private fun ChargementScreen() {
    Box(
        Modifier
            .fillMaxSize()
            .background(EtataColors.Paper),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(120.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.Transparent
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_app),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(32.dp))
            CircularProgressIndicator(color = EtataColors.Rouge, strokeWidth = 3.dp)
            Spacer(Modifier.height(16.dp))
            Text("Chargement de vos documents...", fontSize = 14.sp, color = EtataColors.InkSoft)
        }
    }
}

@Composable
private fun BarreNavigation(actuel: Onglet, onSelect: (Onglet) -> Unit) {
    NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
        Onglet.entries.forEach { o ->
            NavigationBarItem(
                selected = actuel == o,
                onClick = { onSelect(o) },
                icon = { Icon(o.icone, o.label, modifier = Modifier.size(21.dp)) },
                label = { Text(o.label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = EtataColors.Rouge,
                    selectedTextColor = EtataColors.Rouge,
                    unselectedIconColor = EtataColors.InkSoft,
                    unselectedTextColor = EtataColors.InkSoft,
                    indicatorColor = EtataColors.RougeSoft
                )
            )
        }
    }
}
