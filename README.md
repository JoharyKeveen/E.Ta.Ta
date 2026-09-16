# E-TaraTasy (E.Ta.Ta) — application Android en Kotlin (MVVM)

Application mobile destinée à réduire, voire supprimer, les files d'attente lors des
demandes de documents administratifs auprès des Fokontany et des Arrondissements à
Madagascar.

## Les deux règles ajoutées dans cette version

**1. Documents sans rendez-vous → visibles numériquement, immédiatement.**
Les certificats du Fokontany et le diplôme (BACC) ne nécessitent aucun passage
physique. Dès le dépôt de la demande, `EtataViewModel.deposerDemande()` génère un
`DocumentNumerique` et l'ajoute au portefeuille : le citoyen le consulte dans
l'onglet **Portefeuille** ou via le bouton « Voir le document numérique » dans
**Mes demandes**. Aucune attente, aucun guichet.

**2. CIN et permis de conduire → permanents et non supprimables après retrait.**
ces deux documents restent `surRendezVous = true` (ils exigent une remise en main
propre), mais portent en plus `pieceOfficiellePermanente = true` dans le catalogue.
Une fois le rendez-vous honoré, le citoyen appuie sur *« J'ai retiré ce document »*
dans l'onglet RDV : `EtataViewModel.marquerRetrait()` crée alors une entrée dans
`piecesOfficielles`, affichée en permanence dans le Portefeuille. **Aucune action de
suppression n'existe dans l'interface pour cette liste** — contrairement aux codes de
partage, qui eux sont révocables.

## Architecture MVVM

```
com.etaratasy.app/
├── model/                     MODEL — entités métier, immuables
│   ├── Identite.kt            NumeroActeNaissance (identifiant = acte de naissance), Citoyen
│   ├── Catalogue.kt           Guichet, TypeDocument (surRendezVous, pieceOfficiellePermanente)
│   ├── Demande.kt             StatutDemande, Demande
│   ├── RendezVous.kt
│   ├── DocumentNumerique.kt   document affiché dans le portefeuille (permanent ou non)
│   ├── Partage.kt             DureePartage, PartageAcces
│   ├── Sante.kt                DossierSante
│   ├── Notification.kt
│   └── Controle.kt            ResultatControle (module forces de l'ordre)
│
├── repository/
│   └── EtataRepository.kt     MODEL (accès aux données) — registre d'état civil simulé,
│                               génération des références et des documents numériques.
│                               Seule classe à remplacer par de vrais appels réseau.
│
├── viewmodel/
│   ├── EtataUiState.kt        état unique et immuable de l'écran
│   └── EtataViewModel.kt      VIEWMODEL — StateFlow<EtataUiState> + actions publiques
│                               (deposerDemande, marquerRetrait, prendreRendezVous…).
│                               Aucune classe Android, aucun Compose ici : testable
│                               unitairement sans émulateur.
│
├── MainActivity.kt            VIEW (racine) — observe le StateFlow, route entre les
│                               écrans, ne contient aucune règle métier.
└── ui/
    ├── theme/                 palette et typographie
    ├── components/            composants réutilisables (boutons, pastilles, en-têtes)
    └── screens/                VIEW — un composable par écran, stateless, reçoit l'état
                                 et des lambdas ; module/ regroupe Partage, Santé, Contrôle.
```

Le flux de données est à sens unique : `View` appelle une fonction du `ViewModel` →
le `ViewModel` met à jour son `StateFlow` via le `Repository` → la `View` se
recompose automatiquement. Aucun écran ne modifie l'état directement.

## Fonctionnalités couvertes

| Point du cahier des charges | Implémentation |
|---|---|
| 10 certificats du Fokontany, visibles numériquement | `Catalogue.certificatsFokontany`, `PortefeuilleScreen` |
| Demande de diplôme (BACC) | `Catalogue.autresDocuments` |
| CIN et permis permanents après retrait | `TypeDocument.pieceOfficiellePermanente`, `marquerRetrait()`, `PortefeuilleScreen` |
| Notifications (majorité, permis, RDV, MAJ agent) | `EtataRepository.notificationsInitiales`, `NotificationsScreen` |
| Login sécurisé après activation | `ConnexionScreen` (acte de naissance + code SMS) |
| Accès temporaire aux documents (partage, révocable) | `PartageScreen` |
| Contrôle des forces de l'ordre | `ControleScreen` |
| Module santé | `SanteScreen` |
| Documents spéciaux sur rendez-vous | `Catalogue.documentsSpeciaux`, `RendezVousScreen` |

## Lancer le projet

1. Ouvrir le dossier dans Android Studio (Ladybug ou plus récent), laisser Gradle
   synchroniser, puis `Run`.
2. Se connecter avec le numéro `0453/2001-101` ou `1207/1994-104` (registre simulé),
   et n'importe quel code à 6 chiffres.
3. Demander un certificat depuis **Accueil** → il apparaît aussitôt dans
   **Portefeuille**. Prendre un RDV pour la CIN, puis appuyer sur *« J'ai retiré ce
   document »* dans l'onglet **RDV** → la pièce devient permanente dans le
   Portefeuille.

## Étapes suivantes pour une mise en production

- Remplacer `EtataRepository` par un client Retrofit/Ktor : les signatures ne
  changent pas, seul le corps des méthodes devient asynchrone (`suspend`).
- Ajouter des tests unitaires sur `EtataViewModel` (aucune dépendance Android dans
  cette classe, donc testable avec `kotlinx-coroutines-test` seul).
- Stockage local chiffré (DataStore) pour persister session, portefeuille et dossier
  santé entre les lancements — actuellement tout est en mémoire.
- Signature électronique des documents (PDF + QR code vérifiable hors ligne).
- Journalisation du module forces de l'ordre par matricule d'agent, côté serveur.
- Mode hors-ligne (Room + file de synchronisation).
- Traduction malagasy (`values-mg/`).
