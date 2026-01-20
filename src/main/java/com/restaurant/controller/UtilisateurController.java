package com.restaurant.controller;

import com.restaurant.dao.UtilisateurDAO;
import com.restaurant.model.Utilisateur;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class UtilisateurController {

    /* =========================
       TABLE
       ========================= */
    @FXML private TableView<Utilisateur> tableUtilisateurs;
    @FXML private TableColumn<Utilisateur, Integer> colId;
    @FXML private TableColumn<Utilisateur, String> colNom;
    @FXML private TableColumn<Utilisateur, String> colPrenom;
    @FXML private TableColumn<Utilisateur, String> colRole;
    @FXML private TableColumn<Utilisateur, String> colEtat;

    /* =========================
       BOUTONS
       ========================= */
    @FXML private Button btnModifier;
    @FXML private Button btnChangerEtat;
    @FXML private Button btnSupprimer;

    @FXML private HBox actionBar;

    private final ObservableList<Utilisateur> liste =
            FXCollections.observableArrayList();

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    /* =========================
       INITIALISATION
       ========================= */
    @FXML
    public void initialize() {

        colId.setCellValueFactory(
                data -> data.getValue().idUtilisateurProperty().asObject()
        );
        colNom.setCellValueFactory(data -> data.getValue().nomProperty());
        colPrenom.setCellValueFactory(data -> data.getValue().prenomProperty());
        colRole.setCellValueFactory(data -> data.getValue().roleNomProperty());
        colEtat.setCellValueFactory(data -> data.getValue().etatCompteProperty());

        /* 🎨 Style comptes INACTIFS */
        tableUtilisateurs.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Utilisateur u, boolean empty) {
                super.updateItem(u, empty);

                if (u == null || empty) {
                    setStyle("");
                } else if ("INACTIF".equalsIgnoreCase(u.getEtatCompte())) {
                    setStyle("""
                        -fx-background-color: #fee2e2;
                        -fx-text-fill: #991b1b;
                    """);
                } else {
                    setStyle("");
                }
            }
        });

        /* 🔐 Gestion intelligente des boutons */
        tableUtilisateurs.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldU, u) -> gererBoutons(u));

        chargerListe();
        playIntroAnimations();
    }

    /* =========================
       LOGIQUE BOUTONS (PRO)
       ========================= */
    private void gererBoutons(Utilisateur u) {

        if (u == null) {
            btnModifier.setDisable(true);
            btnChangerEtat.setDisable(true);
            btnSupprimer.setDisable(true);
            return;
        }

        btnModifier.setDisable(
                "INACTIF".equalsIgnoreCase(u.getEtatCompte())
        );

        // 🔐 Protection dernier ADMIN
        if ("ADMINISTRATEUR".equalsIgnoreCase(u.getRole().getNomRole())
                && utilisateurDAO.compterAdminsActifs() <= 1) {

            btnChangerEtat.setDisable(true);
            btnSupprimer.setDisable(true);

            Tooltip t = new Tooltip(
                    "Impossible d’agir sur le dernier administrateur actif"
            );
            btnChangerEtat.setTooltip(t);
            btnSupprimer.setTooltip(t);
            return;
        }

        btnChangerEtat.setDisable(false);
        btnSupprimer.setDisable(false);
        btnChangerEtat.setTooltip(null);
        btnSupprimer.setTooltip(null);
    }

    /* =========================
       ANIMATIONS
       ========================= */
    private void playIntroAnimations() {

        FadeTransition fade =
                new FadeTransition(Duration.seconds(0.6), tableUtilisateurs);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        if (actionBar != null) {
            TranslateTransition slide =
                    new TranslateTransition(Duration.seconds(0.5), actionBar);
            slide.setFromY(20);
            slide.setToY(0);
            slide.play();
        }
    }

    /* =========================
       CHARGEMENT
       ========================= */
    private void chargerListe() {
        liste.clear();
        liste.addAll(utilisateurDAO.listeUtilisateurs());
        tableUtilisateurs.setItems(liste);
    }

    /* =========================
       AJOUT
       ========================= */
    @FXML
    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/ajout_utilisateur.fxml")
            );

            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Ajouter un utilisateur");
            stage.setScene(scene);
            stage.showAndWait();

            chargerListe();

        } catch (Exception e) {
            afficherAlerte(
                    Alert.AlertType.ERROR,
                    "Erreur",
                    "Impossible d’ouvrir le formulaire d’ajout."
            );
        }
    }

    /* =========================
       MODIFIER
       ========================= */
    @FXML
    private void handleModifier() {

        Utilisateur u =
                tableUtilisateurs.getSelectionModel().getSelectedItem();

        if (u == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/modifier_utilisateur.fxml")
            );

            Scene scene = new Scene(loader.load());
            ModifierUtilisateurController controller =
                    loader.getController();
            controller.initData(u);

            Stage stage = new Stage();
            stage.setTitle("Modifier utilisateur");
            stage.setScene(scene);
            stage.showAndWait();

            chargerListe();

        } catch (Exception e) {
            afficherAlerte(
                    Alert.AlertType.ERROR,
                    "Erreur",
                    "Impossible d’ouvrir le formulaire de modification."
            );
        }
    }

    /* =========================
       ACTIVER / DÉSACTIVER
       ========================= */
    @FXML
    private void handleChangerEtat() {

        Utilisateur u =
                tableUtilisateurs.getSelectionModel().getSelectedItem();

        if (u == null) return;

        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment changer l’état de cet utilisateur ?",
                ButtonType.YES, ButtonType.NO
        );

        confirm.setHeaderText(null);

        if (confirm.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
            return;
        }

        String nouvelEtat =
                "ACTIF".equalsIgnoreCase(u.getEtatCompte())
                        ? "INACTIF"
                        : "ACTIF";

        if (utilisateurDAO.changerEtatCompte(
                u.getIdUtilisateur(),
                nouvelEtat)) {
            chargerListe();
        }
    }

    /* =========================
       SUPPRIMER (PROTÉGÉ)
       ========================= */
    @FXML
    private void handleSupprimer() {

        Utilisateur u =
                tableUtilisateurs.getSelectionModel().getSelectedItem();

        if (u == null) return;

        // 🔐 Sécurité finale
        if ("ADMINISTRATEUR".equalsIgnoreCase(u.getRole().getNomRole())
                && utilisateurDAO.compterAdminsActifs() <= 1) {

            afficherAlerte(
                    Alert.AlertType.ERROR,
                    "Suppression interdite",
                    "Impossible de supprimer le dernier administrateur."
            );
            return;
        }

        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "⚠ Suppression définitive\n\n" +
                        "Nom : " + u.getNom() + " " + u.getPrenom() + "\n" +
                        "Rôle : " + u.getRole().getNomRole() + "\n\n" +
                        "Voulez-vous continuer ?",
                ButtonType.YES, ButtonType.NO
        );

        confirm.setHeaderText(null);

        if (confirm.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
            return;
        }

        // ✅ APPEL CORRECT
        if (utilisateurDAO.supprimer(u.getIdUtilisateur())) {
            chargerListe();
            afficherAlerte(
                    Alert.AlertType.INFORMATION,
                    "Suppression réussie",
                    "L’utilisateur a été supprimé avec succès."
            );
        }
    }

    /* =========================
       ALERTE UTILITAIRE
       ========================= */
    private void afficherAlerte(
            Alert.AlertType type,
            String titre,
            String message) {

        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
