package com.restaurant.controller;

import com.restaurant.dao.PlatDAO;
import com.restaurant.model.Plat;
import com.restaurant.model.Menu;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AjoutPlatController {

    @FXML private TextField txtNom;
    @FXML private TextField txtCategorie;
    @FXML private TextField txtPrix;
    @FXML private CheckBox checkDisponible;

    // 🔥 MENU SÉLECTIONNÉ REÇU DE MenuGestionController
    private Menu menuSelectionne;

    /* ===== MÉTHODE TRÈS IMPORTANTE ===== */
    public void setMenuSelectionne(Menu menu) {
        this.menuSelectionne = menu;
    }

    /* ===== ENREGISTRER PLAT ===== */

    @FXML
    private void handleEnregistrer() {

        // Vérification des champs
        if (txtNom.getText().isEmpty()
                || txtCategorie.getText().isEmpty()
                || txtPrix.getText().isEmpty()) {

            afficherAlerte("Champs obligatoires",
                    "Veuillez remplir tous les champs.");
            return;
        }

        // Vérification menu sélectionné
        if (menuSelectionne == null) {
            afficherAlerte("Menu manquant",
                    "Aucun menu n’a été sélectionné.");
            return;
        }

        try {
            double prix = Double.parseDouble(txtPrix.getText());

            // Création du plat
            Plat p = new Plat();
            p.setNom(txtNom.getText());
            p.setCategorie(txtCategorie.getText());
            p.setPrix(prix);

            // Disponibilité
            if (checkDisponible.isSelected()) {
                p.setDisponibilite("DISPONIBLE");
            } else {
                p.setDisponibilite("INDISPONIBLE");
            }

            // 🔥 ID DU MENU AUTOMATIQUEMENT CORRECT
            p.setIdMenu(menuSelectionne.getIdMenu());

            // Insertion
            boolean ok = PlatDAO.ajouter(p);

            if (ok) {
                afficherInfo("Succès",
                        "Plat ajouté au menu : " + menuSelectionne.getNomMenu());
                fermer();
            } else {
                afficherAlerte("Erreur",
                        "Erreur lors de l'ajout du plat.");
            }

        } catch (NumberFormatException e) {
            afficherAlerte("Format incorrect",
                    "Le prix doit être un nombre valide.");
        }
    }

    @FXML
    private void handleAnnuler() {
        fermer();
    }

    /* ===== OUTILS ===== */

    private void fermer() {
        Stage stage = (Stage) txtNom.getScene().getWindow();
        stage.close();
    }

    private void afficherInfo(String titre, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void afficherAlerte(String titre, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
