# 📋 Documentation Complète - Restaurant Management Pro

**Date** : 7 février 2026  
**Projet** : RestaurantManagementPro  
**Technologie** : Java 11+ avec JavaFX  
**Base de Données** : MySQL

---

## 📑 Table des Matières

1. [Vue d'ensemble du projet](#vue-densemble)
2. [Architecture et structure](#architecture)
3. [Stack technologique](#stack)
4. [Structure des répertoires](#structure)
5. [Modèles de données](#modeles)
6. [Couche DAO (Accès aux données)](#dao)
7. [Contrôleurs et vues](#controllers)
8. [Services métier](#services)
9. [Utilitaires](#utils)
10. [Flux d'utilisation](#flux)
11. [Base de données](#bdd)
12. [Guide d'installation](#installation)
13. [Fonctionnalités détaillées](#fonctionnalites)

---

## <a id="vue-densemble"></a>1. Vue d'ensemble du projet

### 🎯 Objectif

**Restaurant Management Pro** est une application de gestion intégrée pour restaurants permettant :

- La gestion des utilisateurs et des rôles
- La prise de commandes
- La gestion des menus et plats
- Le suivi des tables de restaurant
- La facturation et les paiements
- La gestion des stocks
- Le suivi audit via journal
- La génération de rapports et statistiques

### 👥 Utilisateurs cibles

- **Gérants** : Gestion globale du restaurant
- **Serveurs** : Prise de commandes
- **Cuisinier** : Suivi des commandes
- **Caissier** : Facturation et paiement
- **Admin** : Gestion système et utilisateurs

### 💡 Cas d'usage principaux

1. **Authentification sécurisée** avec hashage des mots de passe
2. **Gestion des commandes** en temps réel
3. **Suivi des tables** et états de commande
4. **Facturation automatique** avec historique
5. **Gestion inventaire** des plats et stocks
6. **Rapports** sur ventes, statistiques, journal d'audit

---

## <a id="architecture"></a>2. Architecture et Structure

### 🏗️ Pattern Architectural

L'application suit le pattern **MVC (Model-View-Controller)** enrichi :

```
┌─────────────────────────────────────────────────┐
│         COUCHE PRÉSENTATION (LayerView)         │
│    JavaFX Controllers + FXML (Interface GUI)    │
└────────────────┬────────────────────────────────┘
                 │ Appel de méthodes
                 ▼
┌─────────────────────────────────────────────────┐
│      COUCHE MÉTIER (Business Logic)             │
│    Services (AuthService, ExportService, etc)   │
└────────────────┬────────────────────────────────┘
                 │ Appel de méthodes
                 ▼
┌─────────────────────────────────────────────────┐
│      COUCHE DONNÉES (DAO - Data Access)         │
│    XXX_DAO.java (Requêtes SQL)                  │
└────────────────┬────────────────────────────────┘
                 │ Requêtes SQL
                 ▼
┌─────────────────────────────────────────────────┐
│      BASE DE DONNÉES MYSQL                      │
│    restaurant_db                                │
└─────────────────────────────────────────────────┘
```

### 🔄 Flux de données

1. **Utilisateur interagit** avec l'interface JavaFX (Controller)
2. **Controller appelle** un Service ou DAO
3. **Service** exécute la logique métier
4. **DAO** exécute les requêtes SQL
5. **Données** retournées en tant qu'objets Model
6. **Controller** met à jour l'interface

### 🔐 Sécurité

- **Authentification** : Hash MD5/SHA256 des mots de passe
- **Gestion de session** : `SessionUtilisateur` stocke l'utilisateur connecté
- **Contrôle d'accès** : Vérification du rôle pour les actions sensibles
- **Limitation des tentatives** : 3 tentatives max avant blocage de login
- **Audit** : Journal de toutes les opérations critiques

---

## <a id="stack"></a>3. Stack Technologique

| Composant             | Technologie     | Version      |
| --------------------- | --------------- | ------------ |
| **Langage**           | Java            | 11+          |
| **Framework UI**      | JavaFX          | 19+          |
| **Layout XML**        | FXML            | Natif JavaFX |
| **Stylesheet**        | CSS             | JavaFX CSS   |
| **Base de données**   | MySQL           | 5.7+         |
| **Driver JDBC**       | MySQL Connector | J 8.0+       |
| **Gestion de projet** | Maven/Gradle    | (optionnel)  |
| **IDE**               | IntelliJ IDEA   | Recommandé   |

### Dépendances principales

```xml
<!-- MySQL JDBC Driver -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>

<!-- JavaFX (intégré dans le JDK 11+) -->
```

---

## <a id="structure"></a>4. Structure des Répertoires

```
RestaurantManagementPro/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/restaurant/
│       │       ├── app/
│       │       │   └── MainApp.java          # Point d'entrée JavaFX
│       │       │
│       │       ├── controller/               # 26 Controllers JavaFX
│       │       │   ├── LoginController.java
│       │       │   ├── DashboardController.java
│       │       │   ├── PriseCommandeController.java
│       │       │   ├── FacturationController.java
│       │       │   ├── UtilisateurController.java
│       │       │   └── ... (23 autres)
│       │       │
│       │       ├── model/                    # 12 Classes de modèles
│       │       │   ├── Utilisateur.java
│       │       │   ├── Commande.java
│       │       │   ├── CommandePlat.java
│       │       │   ├── Plat.java
│       │       │   ├── Menu.java
│       │       │   ├── Facture.java
│       │       │   ├── DetailFacture.java
│       │       │   ├── Paiement.java
│       │       │   ├── Role.java
│       │       │   ├── Stock.java
│       │       │   ├── TableRestaurant.java
│       │       │   └── Journal.java
│       │       │
│       │       ├── dao/                      # 13 Data Access Objects
│       │       │   ├── UtilisateurDAO.java
│       │       │   ├── CommandeDAO.java
│       │       │   ├── CommandePlatDAO.java
│       │       │   ├── PlatDAO.java
│       │       │   ├── MenuDAO.java
│       │       │   ├── FactureDAO.java
│       │       │   ├── PaiementDAO.java
│       │       │   ├── TableRestaurantDAO.java
│       │       │   ├── StockDAO.java
│       │       │   ├── JournalDAO.java
│       │       │   ├── RoleDAO.java
│       │       │   ├── StatistiqueDAO.java
│       │       │   └── RapportDAO.java
│       │       │
│       │       ├── service/                  # 7 Services métier
│       │       │   ├── AuthService.java      # Authentification
│       │       │   ├── ExportService.java
│       │       │   ├── FacturePdfService.java
│       │       │   ├── JournalExportService.java
│       │       │   ├── JournalPDFService.java
│       │       │   ├── RapportGlobalService.java
│       │       │   └── RapportServiceExcel.java
│       │       │
│       │       └── util/                     # Utilitaires
│       │           ├── DBConnection.java     # Connexion MySQL
│       │           ├── PasswordUtils.java    # Hash mots de passe
│       │           └── SessionUtilisateur.java # Gestion session
│       │
│       └── resources/
│           ├── css/
│           │   ├── style.css                 # Styles globaux
│           │   └── prise_commande.css        # Styles commandes
│           │
│           ├── fxml/                         # 23 fichiers FXML
│           │   ├── login.fxml
│           │   ├── dashboard.fxml
│           │   ├── prise_commande.fxml
│           │   ├── facturation.fxml
│           │   ├── utilisateurs.fxml
│           │   └── ... (18 autres)
│           │
│           └── images/                       # Ressources images
│
└── RestaurantManagementPro.iml               # Configuration IntelliJ
```

---

## <a id="modeles"></a>5. Modèles de Données

### 📦 Classe Utilisateur

```java
public class Utilisateur {
    - idUtilisateur: int
    - nom: String
    - prenom: String
    - login: String (unique)
    - motDePasse: String (hashé)
    - etatCompte: String (ACTIF/INACTIF)
    - role: Role (relation N-1)
}
```

**Propriétés JavaFX** : Les attributs utilisent `StringProperty`, `IntegerProperty`, `ObjectProperty` pour la liaison avec les contrôles UI.

### 📦 Classe Commande

```java
public class Commande {
    - idCommande: int (PK)
    - dateCommande: LocalDateTime
    - etatCommande: String (EN_COURS/COMPLETÉE/ANNULÉE)
    - idTable: int (FK → TableRestaurant)
    - idServeur: int (FK → Utilisateur)
}
```

### 📦 Classe CommandePlat

```java
public class CommandePlat {
    - idCommande: int (FK)
    - idPlat: int (FK)
    - nomPlat: String (pour affichage)
    - quantite: int
}
```

**Note** : Liaison many-to-many entre Commande et Plat avec la quantité.

### 📦 Classe Plat

```java
public class Plat {
    - idPlat: int (PK)
    - nom: String
    - categorie: String (ENTREE/PLAT/DESSERT/BOISSON)
    - prix: double
    - disponibilite: String (DISPONIBLE/INDISPONIBLE)
    - idMenu: int (FK → Menu)
}
```

### 📦 Classe Menu

```java
public class Menu {
    - idMenu: int (PK)
    - nomMenu: String
    - etatMenu: String (ACTIF/INACTIF)
}
```

### 📦 Classe Facture

```java
public class Facture {
    - idFacture: int (PK)
    - dateFacture: LocalDateTime
    - total: double
    - idCommande: int (FK)
}
```

### 📦 Classe Paiement

```java
public class Paiement {
    - idPaiement: int (PK)
    - idFacture: int (FK)
    - montantPaye: double
    - typePaiement: String (CASH/CARTE/CHEQUE)
    - dateEmprunt: LocalDateTime
}
```

### 📦 Classe TableRestaurant

```java
public class TableRestaurant {
    - idTable: int (PK)
    - numeroTable: int
    - capacite: int
    - etat: String (LIBRE/OCUPÉE)
}
```

### 📦 Classe Stock

```java
public class Stock {
    - idStock: int (PK)
    - idPlat: int (FK)
    - quantiteDisponible: int
    - dateVerification: LocalDateTime
}
```

### 📦 Classe Role

```java
public class Role {
    - idRole: int (PK)
    - nomRole: String (ADMIN/GERANT/SERVEUR/CUISINIER/CAISSIER)
}
```

### 📦 Classe Journal

```java
public class Journal {
    - idJournal: int (PK)
    - idUtilisateur: int (FK)
    - action: String (description)
    - dateAction: LocalDateTime
    - details: String
}
```

### 📦 Classe DetailFacture

```java
public class DetailFacture {
    - idDetails: int (PK)
    - idFacture: int (FK)
    - idPlat: int (FK)
    - quantite: int
    - prixUnitaire: double
    - sousTotal: double
}
```

---

## <a id="dao"></a>6. Couche DAO (Data Access Layer)

### 🗂️ Rôle du DAO

La couche DAO encapsule toutes les opérations de base de données :

- **Requêtes SELECT** (récupérer les données)
- **Requêtes INSERT** (créer)
- **Requêtes UPDATE** (modifier)
- **Requêtes DELETE** (supprimer)

### 📝 Pattern DAO utilisé

Chaque classe DAO suit le même pattern :

```java
public class XXX_DAO {

    // Récupérer tous les enregistrements
    public List<XXX> getAll() { ... }

    // Récupérer par ID
    public XXX getById(int id) { ... }

    // Créer
    public void create(XXX objet) { ... }

    // Modifier
    public void update(XXX objet) { ... }

    // Supprimer
    public void delete(int id) { ... }

    // Créer l'objet depuis ResultSet (privé)
    private XXX construire(ResultSet rs) { ... }
}
```

### 🔑 DAOs principaux

| DAO                    | Table                | Méthodes principales                                               |
| ---------------------- | -------------------- | ------------------------------------------------------------------ |
| **UtilisateurDAO**     | utilisateur          | login(), getAll(), create(), update(), delete()                    |
| **CommandeDAO**        | commande             | getAll(), getById(), create(), updateEtat(), getCommandesEnCours() |
| **CommandePlatDAO**    | commande_plat        | getById(), getAll(), add(), delete()                               |
| **PlatDAO**            | plat                 | getAll(), getById(), create(), update(), delete(), getByMenu()     |
| **MenuDAO**            | menu                 | getAll(), getById(), create(), update(), delete()                  |
| **FactureDAO**         | facture              | getAll(), create(), getById(), getTotalMensuel()                   |
| **PaiementDAO**        | paiement             | create(), getByFacture(), getTotalPaye()                           |
| **TableRestaurantDAO** | table_restaurant     | getAll(), updateEtat()                                             |
| **StockDAO**           | stock                | getAll(), update(), getQuantite()                                  |
| **JournalDAO**         | journal              | insert(), getAll(), getByUtilisateur()                             |
| **RoleDAO**            | role                 | getAll(), getById()                                                |
| **StatistiqueDAO**     | (requêtes complexes) | getTotalVentes(), getCommandesParServeur()                         |
| **RapportDAO**         | (requêtes complexes) | getRapportJournalier(), getRapportMensuel()                        |

### 🔌 Connexion à la BD

```java
public class DBConnection {
    private static final String URL =
        "jdbc:mysql://127.0.0.1:3306/restaurant_db?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Sam219592";

    public static Connection getConnection() {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
```

---

## <a id="controllers"></a>7. Contrôleurs et Vues

### 🎮 Rôle des contrôleurs

Les contrôleurs JavaFX gèrent :

- Initialisation de l'interface FXML
- Liaisons entre le modèle et la vue
- Gestion des événements utilisateur (clics, saisies)
- Appels aux services/DAO
- Mise à jour de l'interface

### 📋 Liste des 26 contrôleurs

#### **Authentification & Dashboard**

1. **LoginController** - Écran de connexion
   - Gère l'authentification utilisateur
   - Limitation des tentatives (max 3)
   - Animations d'entrée
   - Redirection vers Dashboard

2. **DashboardController** - Tableau de bord principal
   - Menu de navigation
   - Gestion des vues dynamiques
   - Logout

3. **DashboardHomeController** - Page d'accueil du dashboard
   - Résumés et statistiques rapides
   - Cartes de données

#### **Gestion des Utilisateurs**

4. **UtilisateurController** - Liste des utilisateurs
   - Affichage dans TableView
   - Filtrage et recherche
   - Boutons : Ajouter, Modifier, Supprimer

5. **AjoutUtilisateurController** - Formulaire d'ajout
   - Saisie des données
   - Sélection du rôle
   - Validation du formulaire

6. **ModifierUtilisateurController** - Formulaire de modification
   - Pré-remplissage des données
   - Mise à jour
   - Confirmation

7. **ChangerMotDePasseController** - Changement mot de passe
   - Vérification ancien mot de passe
   - Nouvelle double saisie
   - Hachage sécurisé

#### **Gestion des Menus et Plats**

8. **MenuGestionController** - Liste des menus
   - Affichage des menus actifs/inactifs
   - Gestion des états

9. **AjoutMenuController** - Ajout d'un menu
   - Formulaire simple
   - Liaison à la base de données

10. **ModifierMenuController** - Modification d'un menu
    - Édition du nom et état

11. **GestionMenusPlatsController** - Gestion menus & plats
    - Vue combinée menus/plats
    - Assignation plats à menus

12. **AjoutPlatController** - Ajout d'un plat
    - Sélection du menu
    - Saisie prix, catégorie
    - Vérification disponibilité

13. **ModifierPlatController** - Modification d'un plat
    - Édition complète du plat

#### **Prise de Commande**

14. **PriseCommandeController** - Interface principale de commande
    - Sélection table
    - Sélection plats/menu
    - Panier de commande (TableView)
    - Soumission de commande

15. **CommandeController** - Vue détaillée commande
    - Affichage des commandes
    - Modification état
    - Annulation

16. **CommandesServiesController** - Commandes servies
    - Historique des commandes complétées
    - Détails et plats

#### **Facturation & Paiement**

17. **FacturationController** - Module de facturation
    - Génération de factures
    - Calcul automatique du total
    - Détails articles

18. **FactureController** - Liste des factures
    - Historique des factures
    - Filtrage par date
    - Détails

19. **FacturePrintController** - Impression/export facture
    - Génération PDF
    - Mise en forme
    - Export

20. **PaiementController** - Gestion des paiements
    - Enregistrement des paiements
    - Type de paiement (cash, carte, chèque)
    - Calcul montant restant

#### **Gestion des Stocks**

21. **StockController** - Gestion des stocks
    - Affichage quantités disponibles
    - Mise à jour stocks
    - Alertes faible stock

#### **Rapports & Statistiques**

22. **RapportController** - Génération rapports
    - Rapports journaliers/mensuels
    - Export Excel
    - Filtres temporels

23. **StatistiqueController** - Statistiques
    - Graphiques (ventes, commandes)
    - Tendances
    - KPIs

#### **Historique & Audit**

24. **HistoriqueCommandesController** - Historique commandes
    - Recherche et filtrage
    - Détails anciennes commandes

25. **JournalController** - Journal d'audit
    - Toutes les actions utilisateurs
    - Traçabilité complète
    - Export

#### **Tables du Restaurant**

26. **TableRestaurantController** - Gestion tables
    - Affichage des tables
    - États (libre, ocupée)
    - Assignation commandes

### 🔗 Interaction Controllers-Views

Chaque contrôleur est lié à un fichier FXML :

```
LoginController ↔ login.fxml
PriseCommandeController ↔ prise_commande.fxml
FacturationController ↔ facturation.fxml
...
```

Le chargement se fait via FXMLLoader :

```java
@FXML
private void loadView(String fxmlFile) {
    FXMLLoader loader = new FXMLLoader(
        getClass().getResource("/fxml/" + fxmlFile)
    );
    Parent root = loader.load();
    // Afficher la vue
}
```

---

## <a id="services"></a>8. Services Métier

### 🎯 Rôle des Services

Les services encapsulent la logique métier complexe :

- Authentification
- Génération de rapports
- Export de données
- Calculs métier

### 📚 Services disponibles

#### **AuthService**

```java
public class AuthService {
    public Utilisateur authentifier(String login, String motDePasse) {
        // Logique d'authentification
        // Hash le mot de passe et vérifie en base
        return utilisateurDAO.login(login, motDePasse);
    }
}
```

#### **ExportService**

Gère les exports de données génériques :

- Export en fichier texte
- Préparation des données

#### **FacturePdfService**

Génère des factures au format PDF :

- Mise en forme
- Calculs totaux
- Génération PDF avec iText ou Apache PDFBox

#### **JournalExportService**

Export du journal d'audit :

- Export CSV/Excel
- Filtrage par dates
- formatage

#### **JournalPDFService**

PDF du journal d'audit :

- Rapport formaté
- Pagination

#### **RapportGlobalService**

Rapports synthétiques :

- Synthèse ventes
- Comparaisons périodes
- Tendances

#### **RapportServiceExcel**

Export rapports en Excel :

- Feuilles multiples
- Graphiques intégrés
- Formatage professionnel

---

## <a id="utils"></a>9. Utilitaires

### 🔧 Classes utilitaires

#### **DBConnection**

Singleton de connexion à MySQL :

```java
public class DBConnection {
    public static Connection getConnection() {
        // Retourne une connexion MySQL
        // Gère les erreurs de connexion
    }
}
```

#### **PasswordUtils**

Hashage sécurisé des mots de passe :

```java
public class PasswordUtils {
    public static String hash(String mdpClair) {
        // Hash MD5/SHA256 du mot de passe
        // Retourne String hashé
    }

    public static boolean verifier(String mdpClair, String mdpHash) {
        // Vérifie que le mot de passe correspond au hash
    }
}
```

#### **SessionUtilisateur**

Gestion de la session courante :

```java
public class SessionUtilisateur {
    private static Utilisateur utilisateurConnecte;

    public static void setUtilisateurConnecte(Utilisateur u) { ... }
    public static Utilisateur getUtilisateurConnecte() { ... }
    public static void clear() { ... }
}
```

Cette classe est un **Singleton** qui maintient l'utilisateur connecté globalement.

---

## <a id="flux"></a>10. Flux d'Utilisation (Use Cases)

### 🔐 Flux d'Authentification

```
1. Utilisateur lance l'application
2. MainApp charge login.fxml
3. LoginController s'initialise
4. Utilisateur saisit login/motdepasse
5. handleLogin() appelé
6. AuthService → UtilisateurDAO.login()
7. DAO exécute SELECT avec hash du mot de passe
8. ✓ Login OK → SessionUtilisateur.set() + Redirection Dashboard
9. ✗ Login KO → tentatives++ → Message erreur
10. Si tentatives > 3 → Compte bloqué 30 secondes
```

### 📋 Flux de Prise de Commande

```
1. Serveur ouvre "Prise de Commande"
2. Sélectionne une table (TableRestaurantController)
3. Affiche le menu des plats
4. Serveur sélectionne plats + quantité
5. Plats ajoutés au panier (ObservableList)
6. Calcul du total dynamique
7. Clique "Valider commande"
   → CommandeDAO.create() (crée enregistrement Commande)
   → CommandePlatDAO.crée liaisons (une par plat)
   → Table passe à état OCUPÉE
   → Journal.insert() (log de l'action)
8. Affichage message succès
```

### 💵 Flux de Facturation

```
1. Caissier ouvre "Facturation"
2. Sélectionne une commande (complétée)
3. Affiche détails commandes/plats
4. Système calcule total automatiquement
5. Caissier valide la facture
   → FactureDAO.create()
   → DetailFacture.insert() pour chaque plat
   → Charge la commande à "FACTURÉE"
6. Impression de la facture
   → FacturePdfService.generer()
   → Affichage PDF
7. Enregistrement du paiement
   → PaiementController
   → Choix type paiement
   → PaiementDAO.create()
8. Table retour à LIBRE
```

### 📊 Flux de Rapport

```
1. Utilisateur ouvre "Rapports"
2. Sélectionne plage de dates
3. Sélectionne type de rapport (journalier/mensuel)
4. Clique "Générer"
   → RapportDAO.executeComplexQuery()
   → RapportGlobalService.calculer()
   → RapportServiceExcel.exporter()
5. Téléchargement fichier Excel
```

---

## <a id="bdd"></a>11. Base de Données

### 🗄️ Schéma MySQL

```sql
CREATE DATABASE restaurant_db;
USE restaurant_db;

-- ROLE
CREATE TABLE role (
    id_role INT PRIMARY KEY AUTO_INCREMENT,
    nom_role VARCHAR(50) UNIQUE
);

-- UTILISATEUR
CREATE TABLE utilisateur (
    id_utilisateur INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100),
    prenom VARCHAR(100),
    login VARCHAR(50) UNIQUE,
    mot_de_passe VARCHAR(255),  -- Hashé
    etat_compte ENUM('ACTIF', 'INACTIF'),
    id_role INT,
    FOREIGN KEY (id_role) REFERENCES role(id_role)
);

-- MENU
CREATE TABLE menu (
    id_menu INT PRIMARY KEY AUTO_INCREMENT,
    nom_menu VARCHAR(100),
    etat_menu ENUM('ACTIF', 'INACTIF')
);

-- PLAT
CREATE TABLE plat (
    id_plat INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100),
    categorie VARCHAR(50),  -- ENTREE, PLAT, DESSERT, BOISSON
    prix DECIMAL(10,2),
    disponibilite ENUM('DISPONIBLE', 'INDISPONIBLE'),
    id_menu INT,
    FOREIGN KEY (id_menu) REFERENCES menu(id_menu)
);

-- TABLE_RESTAURANT
CREATE TABLE table_restaurant (
    id_table INT PRIMARY KEY AUTO_INCREMENT,
    numero_table INT,
    capacite INT,
    etat ENUM('LIBRE', 'OCUPÉE')
);

-- COMMANDE
CREATE TABLE commande (
    id_commande INT PRIMARY KEY AUTO_INCREMENT,
    date_commande DATETIME,
    etat_commande ENUM('EN_COURS', 'COMPLETÉE', 'ANNULÉE'),
    id_table INT,
    id_serveur INT,
    FOREIGN KEY (id_table) REFERENCES table_restaurant(id_table),
    FOREIGN KEY (id_serveur) REFERENCES utilisateur(id_utilisateur)
);

-- COMMANDE_PLAT (relation M-N)
CREATE TABLE commande_plat (
    id_commande INT,
    id_plat INT,
    quantite INT,
    PRIMARY KEY (id_commande, id_plat),
    FOREIGN KEY (id_commande) REFERENCES commande(id_commande),
    FOREIGN KEY (id_plat) REFERENCES plat(id_plat)
);

-- FACTURE
CREATE TABLE facture (
    id_facture INT PRIMARY KEY AUTO_INCREMENT,
    date_facture DATETIME,
    total DECIMAL(10,2),
    id_commande INT,
    FOREIGN KEY (id_commande) REFERENCES commande(id_commande)
);

-- DETAIL_FACTURE
CREATE TABLE detail_facture (
    id_details INT PRIMARY KEY AUTO_INCREMENT,
    id_facture INT,
    id_plat INT,
    quantite INT,
    prix_unitaire DECIMAL(10,2),
    sous_total DECIMAL(10,2),
    FOREIGN KEY (id_facture) REFERENCES facture(id_facture),
    FOREIGN KEY (id_plat) REFERENCES plat(id_plat)
);

-- PAIEMENT
CREATE TABLE paiement (
    id_paiement INT PRIMARY KEY AUTO_INCREMENT,
    id_facture INT,
    montant_paye DECIMAL(10,2),
    type_paiement ENUM('CASH', 'CARTE', 'CHEQUE'),
    date_empunt DATETIME,
    FOREIGN KEY (id_facture) REFERENCES facture(id_facture)
);

-- STOCK
CREATE TABLE stock (
    id_stock INT PRIMARY KEY AUTO_INCREMENT,
    id_plat INT,
    quantite_disponible INT,
    date_verification DATETIME,
    FOREIGN KEY (id_plat) REFERENCES plat(id_plat)
);

-- JOURNAL (Audit)
CREATE TABLE journal (
    id_journal INT PRIMARY KEY AUTO_INCREMENT,
    id_utilisateur INT,
    action VARCHAR(255),
    date_action DATETIME,
    details LONGTEXT,
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id_utilisateur)
);
```

### 🔑 Relations principales

- **1:N** : Role → Utilisateur
- **1:N** : Menu → Plat
- **1:N** : Commande → CommandePlat
- **1:N** : Plat → CommandePlat
- **1:N** : Utilisateur → Commande (serveur)
- **1:N** : TableRestaurant → Commande
- **1:N** : Commande → Facture
- **M:N** : Commande ↔ Plat (via CommandePlat)

---

## <a id="installation"></a>12. Guide d'Installation

### ✅ Prérequis

1. **Java JDK 11+** installé
2. **MySQL 5.7+** lancé localement
3. **IntelliJ IDEA** (ou autre IDE Java)
4. **Drivers MySQL JDBC** (mysql-connector-java 8.0+)

### 📦 Étapes d'installation

#### 1️⃣ Cloner le projet

```bash
cd Desktop/
git clone <url_repo>
cd RestaurantManagementPro
```

#### 2️⃣ Configurer la base de données

```bash
# Lancer MySQL
mysql -u root -p

# Créer la BD
CREATE DATABASE restaurant_db;
USE restaurant_db;

# Importer le schéma (si fichier SQL disponible)
source schema.sql;
```

#### 3️⃣ Configurer la connexion

Modifier `DBConnection.java` :

```java
private static final String URL =
    "jdbc:mysql://127.0.0.1:3306/restaurant_db?serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "votre_mot_de_passe_mysql";
```

#### 4️⃣ Ouvrir dans l'IDE

- **IntelliJ** : File → Open → RestaurantManagementPro
- Attendre l'indexation des classes
- Maven/Gradle : refresh dependencies

#### 5️⃣ Compiler et exécuter

```bash
# Compilation
javac -d out -cp "lib/*" src/main/java/com/restaurant/**/*.java

# Exécution
java -cp "out:lib/*" com.restaurant.app.MainApp
```

**Ou via l'IDE** :

- Clic droit sur `MainApp.java` → Run

#### 6️⃣ Login initial

Utilisateurs par défaut (à créer en BD) :

| Login    | Mot de passe | Rôle    |
| -------- | ------------ | ------- |
| admin    | admin123     | ADMIN   |
| serveur1 | mdp123       | SERVEUR |
| gerant   | mdp123       | GERANT  |

---

## <a id="fonctionnalites"></a>13. Fonctionnalités Détaillées

### 🔐 1. Authentification et Sécurité

#### Authentification

- ✓ Login/Mot de passe hashé (MD5/SHA256)
- ✓ Vérification etat_compte (ACTIF/INACTIF)
- ✓ Limitation tentatives (3 max, blocage 30s)
- ✓ Gestion Session utilisateur

#### Gestion des Rôles

- ✓ 5 rôles : ADMIN, GERANT, SERVEUR, CUISINIER, CAISSIER
- ✓ Contrôle d'accès par rôle (menu dynamique)
- ✓ Autorisations spécifiques par action

### 👥 2. Gestion des Utilisateurs

- ✓ **Ajouter** : Création nouvel utilisateur avec rôle
- ✓ **Modifier** : Edition données utilisateur
- ✓ **Supprimer** : Désactivation de compte
- ✓ **Changement mot de passe** : Sécurisé avec ancienne vérification
- ✓ **Liste** : Affichage TableView filtrable
- ✓ **Recherche** : Filtrage par nom/prénom/login
- ✓ **Activer/Désactiver** : État du compte

### 📋 3. Gestion des Menus

- ✓ **Créer** : Nouveau menu (état ACTIF/INACTIF)
- ✓ **Modifier** : Édition menu existant
- ✓ **Supprimer** : Suppression logique
- ✓ **Activer/Désactiver** : Basculement état
- ✓ **Visualiser** : Plats du menu
- ✓ **Affectation plats** : Association plats → menu

### 🍽️ 4. Gestion des Plats

- ✓ **Ajouter** : Plat avec menu, catégorie, prix
- ✓ **Modifier** : Édition complet plat
- ✓ **Supprimer** : Suppression plat
- ✓ **Disponibilité** : DISPONIBLE/INDISPONIBLE
- ✓ **Catégories** : ENTREE, PLAT, DESSERT, BOISSON
- ✓ **Prix** : Gestion dynamique
- ✓ **Filtrage** : Par menu, par catégorie

### 📝 5. Prise de Commande

- ✓ **Sélection table** : Liste des tables (LIBRE/OCUPÉE)
- ✓ **Sélection plats** : Menu ou liste plats
- ✓ **Panier** : Ajout/suppression/quantité
- ✓ **Calcul total** : Automatique dynamique
- ✓ **Validation** : Création enregistrement
- ✓ **Annulation** : Abandon commande
- ✓ **États** : EN_COURS → COMPLETÉE → FACTURÉE
- ✓ **Historique** : Affichage commandes serveur

### 🧾 6. Facturation et Paiement

- ✓ **Génération factures** : Automatique depuis commande
- ✓ **Détails** : Articles, quantités, prix unitaire
- ✓ **Calculs** : Total HT/TTC (si TVA)
- ✓ **Impression PDF** : Export facture
- ✓ **Types paiement** : CASH, CARTE, CHEQUE
- ✓ **Enregistrement** : Historique paiements
- ✓ **Reste à payer** : Calcul automatique
- ✓ **Recherche factures** : Par date, commande

### 📊 7. Gestion des Stocks

- ✓ **Quantités** : Par plat
- ✓ **Mise à jour** : Augmentation/diminution
- ✓ **Alertes** : Stock faible (< seuil)
- ✓ **Historique** : Traçabilité mouvements
- ✓ **Rapports** : Consommation plats

### 📈 8. Rapports et Statistiques

#### Rapports

- ✓ **Journalier** : Ventes du jour
- ✓ **Mensuel** : Synthèse du mois
- ✓ **Plats populaires** : Top ventes
- ✓ **Serveurs** : Commandes par serveur
- ✓ **Heures de pointe** : Analyse heures
- ✓ **Export Excel** : Téléchargement

#### Statistiques

- ✓ **Graphiques** : Courbes ventes
- ✓ **KPIs** : Moyenne commande, CA, etc
- ✓ **Comparaisons** : Jour/semaine/mois
- ✓ **Tendances** : Évolution

### 📅 9. Journal d'Audit

- ✓ **Traçabilité complète** : Toutes les actions
- ✓ **Utilisateur** : Qui a fait l'action
- ✓ **Date** : Quand exactement
- ✓ **Action** : Création/Modification/Suppression
- ✓ **Détails** : Informations complètes
- ✓ **Recherche** : Filtrage utilisateur/date
- ✓ **Export** : CSV/PDF

### 🛏️ 10. Gestion des Tables

- ✓ **État** : LIBRE/OCUPÉE
- ✓ **Capacité** : Nombre couverts
- ✓ **Assignation** : À une commande
- ✓ **Libération** : Après paiement
- ✓ **Affichage** : Vue tabulaire claire
- ✓ **Filtrage** : Par état, capacité

---

## 📚 Résumé Architecture

```
┌─────────────────────────────────────────┐
│     APPLICATION JAVA RESTAURANT         │
│              Version 1.0                │
└─────────────────────────────────────────┘
           │
           ├──→ INTERFACE (JavaFX)
           │    - 26 Controllers
           │    - 23 vues FXML
           │    - CSS styling
           │
           ├──→ MÉTIER (Services)
           │    - Authentification
           │    - Exports PDF/Excel
           │    - Calculs
           │
           ├──→ DONNÉES (DAOs)
           │    - 13 DAO classes
           │    - Requêtes SQL
           │    - Transactions
           │
           └──→ BASE DE DONNÉES
                - MySQL 5.7+
                - 10 tables
                - Relations complexes
```

---

## 🎯 Conclusion

Ce projet **Restaurant Management Pro** est une application complète, modulaire et bien structurée, suivant les meilleures pratiques Java :

✅ **Architecture claire** : MVC + Services + DAO  
✅ **Interface moderne** : JavaFX avec animations  
✅ **Sécurité** : Authentification, hachage, audit  
✅ **Données robustes** : MySQL avec schéma normalisé  
✅ **Fonctionnalités complètes** : Commandes, facturation, rapports  
✅ **Code maintenable** : Pattern DAO, séparation concerns  
✅ **Scalabilité** : Extensible pour nouveaux modules

### 🚀 Améliorations futures possibles

- **Cache** : Redis pour les sessions
- **API REST** : Web service pour mobile
- **Threading** : Opérations BD asynchrones
- **Validation** : Validateurs côté client/serveur
- **Logging** : Log4j au lieu de println
- **Tests** : JUnit pour DAO/Service
- **ORM** : Hibernate pour simplifier DAO
- **Microservices** : Découpage en services

---

**Documentation créée le** : 7 février 2026  
**Version du projet** : 1.0  
**Développé avec** : Java 11+, JavaFX, MySQL
