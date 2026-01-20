package com.restaurant.controller;

import com.restaurant.dao.CommandeDAO;
import com.restaurant.dao.CommandePlatDAO;
import com.restaurant.dao.PlatDAO;
import com.restaurant.dao.TableRestaurantDAO;
import com.restaurant.dao.StockDAO;

import com.restaurant.model.CommandePlat;
import com.restaurant.model.Plat;
import com.restaurant.model.TableRestaurant;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CommandeController {

    /* =========================
       COMPOSANTS FXML
       ========================= */

    @FXML private ComboBox<TableRestaurant> comboTables;
    @FXML private ComboBox<Plat> comboPlats;
    @FXML private Spinner<Integer> spinnerQuantite;

    @FXML private TableView<Plat> tableCommande;
    @FXML private TableColumn<Plat, String> colNom;
    @FXML private TableColumn<Plat, Double> colPrix;

    /* =========================
       DONNÉES
       ========================= */

    private final ObservableList<TableRestaurant> listeTables =
            FXCollections.observableArrayList();

    private final ObservableList<Plat> listePlats =
            FXCollections.observableArrayList();

    private final ObservableList<Plat> platsCommande =
            FXCollections.observableArrayList();

    private int idCommandeCourante = -1;

    private final StockDAO stockDAO = new StockDAO();

    /* =========================
       INITIALISATION
       ========================= */

    @FXML
    public void initialize() {

        colNom.setCellValueFactory(data -> data.getValue().nomProperty());
        colPrix.setCellValueFactory(
                data -> data.getValue().prixProperty().asObject()
        );

        tableCommande.setItems(platsCommande);

        spinnerQuantite.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1)
        );

        chargerTables();
        chargerPlats();
    }

    /* =========================
       CHARGEMENT DONNÉES
       ========================= */

    private void chargerTables() {
        listeTables.setAll(TableRestaurantDAO.listeTablesLibres());
        comboTables.setItems(listeTables);
    }

    private void chargerPlats() {
        listePlats.setAll(PlatDAO.listePlats());
        comboPlats.setItems(listePlats);
    }

    /* =========================
       CRÉER COMMANDE
       ========================= */

    @FXML
    private void handleCreerCommande() {

        TableRestaurant table = comboTables.getValue();

        if (table == null) {
            afficherErreur("Veuillez choisir une table.");
            return;
        }

        int idServeur = 1; // à remplacer par l'utilisateur connecté

        idCommandeCourante =
                CommandeDAO.creerCommande(table.getIdTable(), idServeur);

        if (idCommandeCourante != -1) {
            afficherSucces(
                    "Commande créée pour la table " + table.getNumeroTable()
            );
            comboTables.setDisable(true);
        } else {
            afficherErreur("Impossible de créer la commande.");
        }
    }

    /* =========================
       AJOUTER PLAT
       ========================= */

    @FXML
    private void handleAjouterPlat() {

        if (idCommandeCourante == -1) {
            afficherErreur("Veuillez d'abord créer une commande.");
            return;
        }

        Plat plat = comboPlats.getValue();
        int quantite = spinnerQuantite.getValue();

        if (plat == null) {
            afficherErreur("Veuillez choisir un plat.");
            return;
        }

        boolean ok = CommandePlatDAO.ajouterOuIncrementerPlat(
                idCommandeCourante,
                plat.getIdPlat(),
                quantite
        );

        if (ok) {
            if (!platsCommande.contains(plat)) {
                platsCommande.add(plat);
            }
            afficherSucces("Plat ajouté à la commande.");
        } else {
            afficherErreur("Impossible d’ajouter le plat.");
        }
    }

    /* =========================
       SUPPRIMER PLAT (LOGIQUE PRO)
       ========================= */

    @FXML
    private void handleSupprimerPlat() {

        Plat plat = tableCommande.getSelectionModel().getSelectedItem();

        if (plat == null || idCommandeCourante == -1) {
            afficherErreur("Veuillez sélectionner un plat.");
            return;
        }

        boolean ok = CommandePlatDAO.supprimerPlat(
                idCommandeCourante,
                plat.getIdPlat()
        );

        if (!ok) {
            afficherErreur("Impossible de supprimer le plat.");
            return;
        }

        platsCommande.remove(plat);

        // 🔥 SI la commande a été supprimée automatiquement
        // le controller doit juste réagir
        if (CommandeDAO.getCommandeEnCoursParTable(
                comboTables.getValue().getIdTable()) == null) {

            afficherSucces(
                    "Tous les plats ont été retirés.\n" +
                            "La commande a été supprimée automatiquement."
            );

            resetInterface();
            return;
        }

        afficherSucces("Plat supprimé de la commande.");
    }

    /* =========================
       SERVIR COMMANDE
       ========================= */

    @FXML
    private void handleServirCommande() {

        if (idCommandeCourante == -1) {
            afficherErreur("Aucune commande en cours.");
            return;
        }

        if (!stockSuffisantPourCommande()) {
            return;
        }

        boolean ok = CommandeDAO.servirCommande(idCommandeCourante);

        if (ok) {
            afficherSucces("Commande servie avec succès.");
            resetInterface();
        } else {
            afficherErreur("Impossible de servir la commande.");
        }
    }

    /* =========================
       CONTRÔLE STOCK
       ========================= */

    private boolean stockSuffisantPourCommande() {

        try {
            for (CommandePlat cp :
                    CommandePlatDAO.getPlatsParCommande(idCommandeCourante)) {

                if (!stockDAO.stockSuffisant(
                        cp.getNomPlat(),
                        cp.getQuantite())) {

                    afficherErreur(
                            "Stock insuffisant pour le plat : "
                                    + cp.getNomPlat()
                    );
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur lors de la vérification du stock.");
            return false;
        }
        return true;
    }

    /* =========================
       RESET INTERFACE
       ========================= */

    private void resetInterface() {
        idCommandeCourante = -1;
        platsCommande.clear();
        comboTables.setDisable(false);
        comboTables.getSelectionModel().clearSelection();
        comboPlats.getSelectionModel().clearSelection();
        chargerTables();
    }

    /* =========================
       ALERTES UI
       ========================= */

    private void afficherErreur(String message) {
        afficher("Erreur", message, Alert.AlertType.ERROR);
    }

    private void afficherSucces(String message) {
        afficher("Succès", message, Alert.AlertType.INFORMATION);
    }

    private void afficher(String titre, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
