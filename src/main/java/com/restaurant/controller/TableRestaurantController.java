package com.restaurant.controller;

import com.restaurant.dao.TableRestaurantDAO;
import com.restaurant.model.TableRestaurant;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class TableRestaurantController {

    @FXML private TableView<TableRestaurant> tableTables;
    @FXML private TableColumn<TableRestaurant, Integer> colNumero;
    @FXML private TableColumn<TableRestaurant, String> colEtat;

    // Barre d’actions (pour animation)
    @FXML private HBox actionBar;

    // Boutons
    @FXML private Button btnModifier;
    @FXML private Button btnLiberer;
    @FXML private Button btnOccuper;

    private final ObservableList<TableRestaurant> liste =
            FXCollections.observableArrayList();

    /* =========================
       INITIALISATION
       ========================= */
    @FXML
    public void initialize() {

        // Mapping colonnes
        colNumero.setCellValueFactory(
                data -> data.getValue()
                        .numeroTableProperty()
                        .asObject()
        );

        colEtat.setCellValueFactory(
                data -> data.getValue().etatTableProperty()
        );

        // 🎨 CellFactory PRO (couleurs + icônes)
        colEtat.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String etat, boolean empty) {
                super.updateItem(etat, empty);

                if (empty || etat == null) {
                    setText(null);
                    setStyle("");
                } else if ("LIBRE".equalsIgnoreCase(etat)) {
                    setText("🟢 LIBRE");
                    setStyle("-fx-text-fill:#16a34a; -fx-font-weight:bold;");
                } else {
                    setText("🔴 OCCUPÉE");
                    setStyle("-fx-text-fill:#dc2626; -fx-font-weight:bold;");
                }
            }
        });

        // 🔐 Gestion intelligente des boutons
        tableTables.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldT, newT) -> {

                    if (newT == null) {
                        btnModifier.setDisable(true);
                        btnLiberer.setDisable(true);
                        btnOccuper.setDisable(true);
                        return;
                    }

                    btnModifier.setDisable(false);
                    btnLiberer.setDisable(
                            "LIBRE".equalsIgnoreCase(newT.getEtatTable())
                    );
                    btnOccuper.setDisable(
                            "OCCUPEE".equalsIgnoreCase(newT.getEtatTable())
                    );
                });

        // Animations
        appliquerAnimations();

        chargerTables();
    }

    /* =========================
       ANIMATIONS PRO
       ========================= */
    private void appliquerAnimations() {

        FadeTransition fade =
                new FadeTransition(Duration.seconds(0.6), tableTables);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        if (actionBar != null) {
            TranslateTransition slide =
                    new TranslateTransition(Duration.seconds(0.5), actionBar);
            slide.setFromY(25);
            slide.setToY(0);
            slide.play();
        }
    }

    /* =========================
       CHARGER TABLES
       ========================= */
    private void chargerTables() {
        liste.clear();
        liste.addAll(TableRestaurantDAO.listeTables());
        tableTables.setItems(liste);
    }

    /* =========================
       AJOUTER
       ========================= */
    @FXML
    private void handleAjouter() {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ajouter une table");
        dialog.setHeaderText("Nouvelle table");
        dialog.setContentText("Numéro de table :");

        dialog.showAndWait().ifPresent(valeur -> {

            try {
                int numero = Integer.parseInt(valeur);

                TableRestaurant t = new TableRestaurant();
                t.setNumeroTable(numero);
                t.setEtatTable("LIBRE");

                if (TableRestaurantDAO.ajouter(t)) {
                    chargerTables();
                    afficherAlerte(Alert.AlertType.INFORMATION,
                            "Succès", "Table ajoutée avec succès.");
                } else {
                    afficherAlerte(Alert.AlertType.ERROR,
                            "Erreur", "Erreur lors de l’ajout.");
                }

            } catch (NumberFormatException e) {
                afficherAlerte(Alert.AlertType.ERROR,
                        "Erreur", "Numéro invalide.");
            }
        });
    }

    /* =========================
       MODIFIER
       ========================= */
    @FXML
    private void handleModifier() {

        TableRestaurant t =
                tableTables.getSelectionModel().getSelectedItem();

        if (t == null) {
            afficherAlerte(Alert.AlertType.WARNING,
                    "Sélection requise",
                    "Veuillez sélectionner une table.");
            return;
        }

        TextInputDialog dialog =
                new TextInputDialog(String.valueOf(t.getNumeroTable()));

        dialog.setTitle("Modifier table");
        dialog.setHeaderText("Modification");
        dialog.setContentText("Nouveau numéro :");

        dialog.showAndWait().ifPresent(valeur -> {

            try {
                int numero = Integer.parseInt(valeur);
                t.setNumeroTable(numero);

                if (TableRestaurantDAO.modifier(t)) {
                    chargerTables();
                    afficherAlerte(Alert.AlertType.INFORMATION,
                            "Succès", "Table modifiée.");
                } else {
                    afficherAlerte(Alert.AlertType.ERROR,
                            "Erreur", "Erreur lors de la modification.");
                }

            } catch (NumberFormatException e) {
                afficherAlerte(Alert.AlertType.ERROR,
                        "Erreur", "Numéro invalide.");
            }
        });
    }

    /* =========================
       LIBÉRER
       ========================= */
    @FXML
    private void handleLiberer() {

        TableRestaurant t =
                tableTables.getSelectionModel().getSelectedItem();

        if (t == null) return;

        t.setEtatTable("LIBRE");

        if (TableRestaurantDAO.changerEtat(t)) {
            chargerTables();
            afficherAlerte(Alert.AlertType.INFORMATION,
                    "Succès", "Table libérée.");
        } else {
            afficherAlerte(Alert.AlertType.ERROR,
                    "Erreur", "Impossible de libérer la table.");
        }
    }

    /* =========================
       OCCUPER
       ========================= */
    @FXML
    private void handleOccuper() {

        TableRestaurant t =
                tableTables.getSelectionModel().getSelectedItem();

        if (t == null) return;

        t.setEtatTable("OCCUPEE");

        if (TableRestaurantDAO.changerEtat(t)) {
            chargerTables();
            afficherAlerte(Alert.AlertType.INFORMATION,
                    "Succès", "Table occupée.");
        } else {
            afficherAlerte(Alert.AlertType.ERROR,
                    "Erreur", "Impossible d’occuper la table.");
        }
    }

    /* =========================
       OUTIL ALERTE
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
