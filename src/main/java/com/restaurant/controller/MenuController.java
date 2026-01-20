package com.restaurant.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.restaurant.dao.PlatDAO;
import com.restaurant.model.Plat;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import com.restaurant.controller.ModifierPlatController;


public class MenuController {

    /* ===== COMPOSANTS FXML ===== */
    @FXML private TableView<Plat> tablePlats;

    @FXML private TableColumn<Plat, Integer> colId;
    @FXML private TableColumn<Plat, String> colNom;
    @FXML private TableColumn<Plat, Double> colPrix;
    @FXML private TableColumn<Plat, String> colCategorie;
    @FXML private TableColumn<Plat, String> colDisponible;   // STRING

    private ObservableList<Plat> liste =
            FXCollections.observableArrayList();

    /* ===== INITIALISATION ===== */
    @FXML
    public void initialize() {
        System.out.println("📋 MenuController initialisé");

        // 🔗 Liaison colonnes ↔ propriétés
        colId.setCellValueFactory(
                data -> data.getValue().idPlatProperty().asObject()
        );

        colNom.setCellValueFactory(
                data -> data.getValue().nomProperty()
        );

        colPrix.setCellValueFactory(
                data -> data.getValue().prixProperty().asObject()
        );

        colCategorie.setCellValueFactory(
                data -> data.getValue().categorieProperty()
        );

        // ✅ Disponibilité = String
        colDisponible.setCellValueFactory(
                data -> data.getValue().disponibiliteProperty()
        );

        // 🎨 COLORATION DISPONIBILITÉ (VERT / ROUGE)
        colDisponible.setCellFactory(column -> new TableCell<Plat, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);

                    if ("DISPONIBLE".equals(item)) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // 📥 Chargement réel depuis la DB
        chargerListe();
    }

    private void chargerListe() {
        liste.clear();
        liste.addAll(PlatDAO.listePlats());
        tablePlats.setItems(liste);

        System.out.println("✅ Plats chargés : " + liste.size());
    }

    /* ===== ACTIONS ===== */

    @FXML
    private void handleAjouter() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/ajout_plat.fxml")
            );

            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setTitle("Ajouter un plat");
            stage.setScene(scene);

            // 🔒 Fenêtre modale
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();   // attendre fermeture

            // 🔄 Recharger la table après ajout
            chargerListe();

        } catch (Exception e) {
            e.printStackTrace();
            afficherInfo("Erreur",
                    "Impossible d'ouvrir le formulaire d'ajout.");
        }
    }

    @FXML
    private void handleModifier() {

        Plat p =
                tablePlats.getSelectionModel()
                        .getSelectedItem();

        if (p == null) {
            afficherInfo("Sélection requise",
                    "Veuillez sélectionner un plat.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/modifier_plat.fxml")
            );

            Scene scene = new Scene(loader.load());

            // Récupérer le contrôleur
            ModifierPlatController controller = loader.getController();
            controller.setPlat(p);   // 🔥 envoi du plat sélectionné

            Stage stage = new Stage();
            stage.setTitle("Modifier un plat");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();


            // 🔄 Recharger la table après modification
            chargerListe();

        } catch (Exception e) {
            e.printStackTrace();
            afficherInfo("Erreur",
                    "Impossible d'ouvrir le formulaire de modification.");
        }
    }

    @FXML
    private void handleGestionMenus() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/menu_gestion.fxml")
            );

            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setTitle("Gestion des menus");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            afficherInfo("Erreur",
                    "Impossible d'ouvrir la gestion des menus.");
        }
    }


    @FXML
    private void handleSupprimer() {

        Plat p =
                tablePlats.getSelectionModel()
                        .getSelectedItem();

        if (p == null) {
            afficherInfo("Sélection requise",
                    "Veuillez sélectionner un plat.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText(
                "Supprimer le plat : " + p.getNom() + " ?"
        );

        if (confirm.showAndWait().get() == ButtonType.OK) {

            boolean ok = PlatDAO.supprimer(p.getIdPlat());

            if (ok) {
                chargerListe();
                afficherInfo("Succès",
                        "Plat supprimé avec succès.");
            } else {
                afficherInfo("Erreur",
                        "Erreur lors de la suppression.");
            }
        }
    }

    /* ===== CHANGER DISPONIBILITÉ ===== */

    @FXML
    private void handleToggleDisponible() {

        Plat p =
                tablePlats.getSelectionModel()
                        .getSelectedItem();

        if (p == null) {
            afficherInfo("Sélection requise",
                    "Veuillez sélectionner un plat.");
            return;
        }

        // 🔁 Inversion DISPONIBLE / INDISPONIBLE
        if ("DISPONIBLE".equals(p.getDisponibilite())) {
            p.setDisponibilite("INDISPONIBLE");
        } else {
            p.setDisponibilite("DISPONIBLE");
        }

        boolean ok = PlatDAO.modifier(p);

        if (ok) {
            chargerListe();
            afficherInfo("Disponibilité",
                    "Statut mis à jour.");
        } else {
            afficherInfo("Erreur",
                    "Impossible de changer la disponibilité.");
        }
    }

    /* ===== UTILITAIRE ===== */

    private void afficherInfo(String titre, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
