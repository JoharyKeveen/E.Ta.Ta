# E-TaraTasy (E.Ta.Ta) — application Android en Kotlin

Application mobile destinée à réduire, voire supprimer, les files d'attente lors des
demandes de documents administratifs auprès des Fokontany et des Arrondissements à
Madagascar.

## L'identifiant : le numéro de l'acte de naissance

Le numéro d'identification utilisé dans toute l'application est **le numéro de l'acte
de naissance** du citoyen. Ce choix est structurant :

- c'est la première et souvent la seule pièce que possède un Malagasy avant la CIN ;
- il existe dès la naissance, ce qui permet d'inscrire aussi les mineurs ;
- il est déjà rattaché à un registre officiel, ce qui empêche la création de comptes fictifs ;
- il permet de déclencher automatiquement la notification de majorité (demande de CIN),
  puisque l'année de naissance est contenue dans le numéro.

Format retenu, modélisé dans `NumeroActeNaissance` :

```
NNNN/AAAA-CCC      exemple : 0453/2001-101
 |    |     |
 |    |     +-- code de la commune / arrondissement d'enregistrement
 |    +-------- année d'enregistrement de l'acte
 +------------- numéro d'ordre dans le registre
```

Le numéro sert également à construire la référence du récépissé de chaque demande
(`ETT-0453.2001.101-8241`), pour que l'agent du guichet retrouve immédiatement le
dossier dans le registre d'état civil.

> La classe `NumeroActeNaissance` centralise la validation. Si le format officiel retenu
> par l'administration diffère, il suffit de modifier la regex à cet endroit : le reste
> de l'application n'a pas à changer.

## Fonctionnalités couvertes

| Point du cahier des charges | Où c'est implémenté |
|---|---|
| Liste des 10 certificats du Fokontany | `Catalogue.certificatsFokontany`, `AccueilScreen` |
| Demande de diplôme (BACC) | `Catalogue.autresDocuments`, `AccueilScreen` |
| Notifications (majorité, permis, RDV, MAJ agent) | `EtataRepository.notificationsInitiales`, `NotificationsScreen` |
| Login sécurisé après activation | `ConnexionScreen` (acte de naissance + code SMS) |
| Accès temporaire aux documents (partage) | `PartageScreen` |
| Contrôle des forces de l'ordre | `ControleScreen` |
| Module santé | `SanteScreen` |
| Documents spéciaux sur rendez-vous | `Catalogue.documentsSpeciaux`, `RendezVousScreen` |

## Architecture

```
app/src/main/java/com/etaratasy/app/
├── MainActivity.kt            navigation, barre d'onglets, feuille de confirmation
├── EtataViewModel.kt          état de l'application (StateFlow) et actions
├── data/
│   ├── Models.kt              NumeroActeNaissance, Citoyen, Demande, RendezVous…
│   ├── Catalogue.kt           catalogue des documents
│   └── EtataRepository.kt     registre simulé + règles métier
└── ui/
    ├── theme/Theme.kt         palette papier administratif / couleurs nationales
    ├── components/Common.kt   composants réutilisables
    └── screens/               Connexion, Accueil, Demandes, RendezVous, Profil,
                               Notifications, modules/
```

Jetpack Compose + Material 3, `minSdk 24`, Kotlin 2.0.

## Lancer le projet

1. Ouvrir le dossier dans Android Studio (Ladybug ou plus récent).
2. Laisser Gradle synchroniser, puis `Run`.

Comptes de test présents dans le registre simulé :

| Numéro d'acte | Citoyen |
|---|---|
| `0453/2001-101` | RASOA Soa Hanitra |
| `1207/1994-104` | RAKOTO Jean Michel |

Le code SMS accepté est n'importe quelle suite de 6 chiffres.

## Étapes suivantes pour une mise en production

- **Backend** : remplacer `EtataRepository` par un client Retrofit/Ktor vers une API de
  l'administration. Les signatures de méthodes sont déjà pensées pour ça.
- **Vérification d'identité** : le seul numéro d'acte ne suffit pas comme preuve. Prévoir
  l'activation en présentiel au Fokontany (l'agent vérifie la copie papier et rattache le
  numéro de téléphone), puis OTP à chaque connexion.
- **Stockage local chiffré** : `EncryptedSharedPreferences` ou DataStore chiffré pour la
  session et le dossier santé.
- **Signature des documents** : PDF signé électroniquement par le Fokontany, avec QR code
  vérifiable hors ligne — c'est ce qui rend le document acceptable au guichet.
- **Traçabilité du module forces de l'ordre** : journaliser chaque consultation avec le
  matricule de l'agent, et restreindre l'accès par rôle côté serveur, pas seulement côté app.
- **Mode hors-ligne** : Room + file d'attente de synchronisation, indispensable vu la
  couverture réseau en dehors des grandes villes.
- **Traduction malagasy** : externaliser tous les textes dans `strings.xml` et fournir
  `values-mg/`.
