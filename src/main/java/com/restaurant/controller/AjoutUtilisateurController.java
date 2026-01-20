package com.restaurant.controller;

import com.restaurant.dao.UtilisateurDAO;
import com.restaurant.model.Role;
import com.restaurant.model.Utilisateur;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AjoutUtilisateurController {

    /* ===================== FXML ===================== */

    @FXML private BorderPane root;

    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtLogin;
    @FXML private PasswordField txtMotDePasse;
    @FXML private ComboBox<Role> comboRole;

    /* ===================== DAO ===================== */

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    /* ===================== INIT ===================== */

    @FXML
    public void initialize() {
        chargerRoles();
        jouerAnimationEntree();
    }

    /* ===================== CHARGEMENT ===================== */

    private void chargerRoles() {
        comboRole.setItems(FXCollections.observableArrayList(
                new Role(1, "ADMINISTRATEUR"),
                new Role(2, "SERVEUR"),
                new Role(3, "CAISSIER"),
                new Role(4, "GESTIONNAIRE")
        ));
    }

    /* ===================== ACTIONS ===================== */

    @FXML
    private void handleEnregistrer() {

        if (!formulaireValide()) {
            afficherAlerte(
                    Alert.AlertType.WARNING,
                    "Champs obligatoires",
                    "Veuillez remplir correctement tous les champs."
            );
            return;
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(txtNom.getText().trim());
        utilisateur.setPrenom(txtPrenom.getText().trim());
        utilisateur.setLogin(txtLogin.getText().trim());
        utilisateur.setMotDePasse(txtMotDePasse.getText()); // hashage plus tard
        utilisateur.setRole(comboRole.getValue());

        boolean succes = utilisateurDAO.ajouter(utilisateur);

        if (succes) {
            afficherAlerte(
                    Alert.AlertType.INFORMATION,
                    "Succès",
                    "Utilisateur ajouté avec succès."
            );
            fermerFenetre();
        } else {
            afficherAlerte(
                    Alert.AlertType.ERROR,
                    "Erreur",
                    "Une erreur est survenue lors de l'ajout."
            );
        }
    }

    @FXML
    private void handleAnnuler() {
        fermerFenetre();
    }

    /* ===================== VALIDATION ===================== */

    private boolean formulaireValide() {

        if (txtNom.getText().trim().isEmpty()) return false;
        if (txtPrenom.getText().trim().isEmpty()) return false;
        if (txtLogin.getText().trim().isEmpty()) return false;
        if (txtMotDePasse.getText().trim().isEmpty()) return false;
        if (comboRole.getValue() == null) return false;

        return true;
    }

    /* ===================== UI ===================== */

    private void fermerFenetre() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /* ===================== ANIMATION ===================== */

    private void jouerAnimationEntree() {

        // Fade
        FadeTransition fade = new FadeTransition(Duration.millis(600), root);
        fade.setFromValue(0);
        fade.setToValue(1);

        // Slide
        TranslateTransition slide = new TranslateTransition(Duration.millis(400), root);
        slide.setFromY(30);
        slide.setToY(0);

        fade.play();
        slide.play();
    }
}
