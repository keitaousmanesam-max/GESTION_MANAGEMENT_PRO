package com.restaurant.controller;

import com.restaurant.dao.JournalDAO;
import com.restaurant.model.Utilisateur;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DashboardController {

    /* ===== COMPOSANTS FXML ===== */
    @FXML private Label userLabel;
    @FXML private VBox menu;
    @FXML private StackPane contentPane;

    @FXML private Button btnUtilisateurs;
    @FXML private Button btnMenus;
    @FXML private Button btnTables;
    @FXML private Button btnStock;

    @FXML private Button btnCommandes;
    @FXML private Button btnCommandesServies;
    @FXML private Button btnHistoriqueCommandes;

    @FXML private Button btnFacturation;
    @FXML private Button btnRapports;
    @FXML private Button btnStatistiques;

    @FXML private Button btnJournal;

    private Utilisateur utilisateurConnecte;

    /* =========================
       INITIALISATION
       ========================= */
    public void initData(Utilisateur utilisateur) {

        this.utilisateurConnecte = utilisateur;

        userLabel.setText(
                utilisateur.getPrenom() + " " +
                        utilisateur.getNom() + " | " +
                        utilisateur.getRole().getNomRole()
        );

        appliquerDroits();           // 🔐 DROITS
        appliquerEffetsBoutons();    // 🎨 UX
        playStartupAnimations();     // 🎬 ANIM

        // 🧾 LOG CONNEXION
        JournalDAO.log(
                utilisateur.getIdUtilisateur(),
                "Connexion au dashboard"
        );

        // Vue par défaut
        chargerVue("dashboard_home.fxml");
    }

    /* =========================
       GESTION DES DROITS (PRO)
       ========================= */
    private void appliquerDroits() {

        // 🔒 Masquer tout par défaut
        Button[] tous = {
                btnUtilisateurs, btnMenus, btnTables, btnStock,
                btnCommandes, btnCommandesServies, btnHistoriqueCommandes,
                btnFacturation, btnRapports, btnStatistiques, btnJournal
        };

        for (Button b : tous) {
            if (b != null) {
                b.setVisible(false);
                b.setManaged(false);
            }
        }

        String role = utilisateurConnecte.getRole().getNomRole();

        switch (role) {

            case "ADMINISTRATEUR" -> {
                afficher(btnUtilisateurs, btnMenus, btnTables, btnStock);
                afficher(btnRapports, btnStatistiques, btnHistoriqueCommandes);
                afficher(btnJournal); // 🔐 ADMIN SEUL
            }

            case "SERVEUR" -> {
                afficher(btnCommandes, btnCommandesServies);
                afficher(btnHistoriqueCommandes); // ✅ autorisé
            }

            case "CAISSIER" -> {
                afficher(btnFacturation, btnCommandesServies);
            }

            case "GESTIONNAIRE" -> {
                afficher(btnRapports, btnStatistiques);
                // ❌ PAS D’HISTORIQUE (règle métier respectée)
            }
        }
    }

    /* =========================
       MÉTHODE UTILITAIRE PRO
       ========================= */
    private void afficher(Button... boutons) {
        for (Button b : boutons) {
            if (b != null) {
                b.setVisible(true);
                b.setManaged(true);
            }
        }
    }

    /* =========================
       NAVIGATION + LOGS
       ========================= */
    @FXML private void showUtilisateurs() {
        chargerVue("utilisateurs.fxml");
        log("Consultation de la gestion des utilisateurs");
    }

    @FXML private void showMenus() {
        chargerVue("menu_gestion.fxml");
        log("Consultation des menus & plats");
    }

    @FXML private void showTables() {
        chargerVue("table_restaurant.fxml");
        log("Consultation des tables du restaurant");
    }

    @FXML private void showStock() {
        chargerVue("stock.fxml");
        log("Consultation du stock");
    }

    @FXML private void showCommandes() {
        chargerVue("prise_commande.fxml");
        log("Prise de commandes");
    }

    @FXML private void showCommandesServies() {
        chargerVue("commandes_servies.fxml");
        log("Consultation des commandes servies");
    }

    @FXML private void showHistoriqueCommandes() {
        chargerVue("historique_commandes.fxml");
        log("Consultation de l’historique des commandes");
    }

    @FXML private void showFacturation() {
        chargerVue("facturation.fxml");
        log("Facturation & paiement");
    }

    @FXML private void showRapports() {
        chargerVue("rapport.fxml");
        log("Consultation des rapports");
    }

    @FXML private void showStatistiques() {
        chargerVue("statistique.fxml");
        log("Consultation des statistiques");
    }

    @FXML
    private void showJournal() {
        chargerVue("journal.fxml");
        log("Consultation du journal des activités");
    }

    /* =========================
       CHARGEMENT CENTRAL
       ========================= */
    private void chargerVue(String fxml) {

        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/fxml/" + fxml));

            Parent vue = loader.load();
            contentPane.getChildren().setAll(vue);

            FadeTransition fade =
                    new FadeTransition(Duration.seconds(0.4), contentPane);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

        } catch (Exception e) {
            System.err.println("❌ Erreur chargement : /fxml/" + fxml);
            e.printStackTrace();
        }
    }

    /* =========================
       ANIMATIONS
       ========================= */
    private void playStartupAnimations() {

        TranslateTransition slideMenu =
                new TranslateTransition(Duration.seconds(0.6), menu);
        slideMenu.setFromX(-260);
        slideMenu.setToX(0);
        slideMenu.play();

        FadeTransition fadeContent =
                new FadeTransition(Duration.seconds(0.6), contentPane);
        fadeContent.setFromValue(0);
        fadeContent.setToValue(1);
        fadeContent.play();
    }

    /* =========================
       EFFETS UX
       ========================= */
    private void appliquerEffetsBoutons() {

        Button[] buttons = {
                btnUtilisateurs, btnMenus, btnTables, btnStock,
                btnCommandes, btnCommandesServies,
                btnHistoriqueCommandes, btnFacturation,
                btnRapports, btnStatistiques, btnJournal
        };

        for (Button btn : buttons) {
            if (btn == null) continue;

            btn.setOnMouseEntered(e -> {
                btn.setScaleX(1.05);
                btn.setScaleY(1.05);
            });

            btn.setOnMouseExited(e -> {
                btn.setScaleX(1);
                btn.setScaleY(1);
            });
        }
    }

    /* =========================
       DÉCONNEXION
       ========================= */
    @FXML
    private void handleLogout() {

        try {
            log("Déconnexion du système");

            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/fxml/login.fxml"));

            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(
                    getClass().getResource("/css/style.css").toExternalForm()
            );

            Stage stage = new Stage();
            stage.setTitle("Connexion - Gestion Restaurant");
            stage.setScene(scene);
            stage.show();

            ((Stage) userLabel.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* =========================
       LOG CENTRALISÉ (PRO)
       ========================= */
    private void log(String action) {
        if (utilisateurConnecte != null) {
            JournalDAO.log(
                    utilisateurConnecte.getIdUtilisateur(),
                    action
            );
        }
    }
}
