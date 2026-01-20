package com.restaurant.controller;

import com.restaurant.dao.StockDAO;
import com.restaurant.model.Stock;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class StockController {

    /* ================= TABLE ================= */
    @FXML private TableView<Stock> tableStock;
    @FXML private TableColumn<Stock, String> colProduit;
    @FXML private TableColumn<Stock, Integer> colQuantite;
    @FXML private TableColumn<Stock, Integer> colSeuil;

    /* ================= CHAMPS ================= */
    @FXML private TextField txtProduit;
    @FXML private TextField txtQuantite;
    @FXML private TextField txtSeuil;

    /* ================= DAO ================= */
    private final StockDAO stockDAO = new StockDAO();

    /* =====================================================
       INITIALISATION
       ===================================================== */
    @FXML
    public void initialize() {

        colProduit.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getNomProduit()
                )
        );

        colQuantite.setCellValueFactory(
                data -> new SimpleIntegerProperty(
                        data.getValue().getQuantite()
                ).asObject()
        );

        colSeuil.setCellValueFactory(
                data -> new SimpleIntegerProperty(
                        data.getValue().getSeuilMin()
                ).asObject()
        );

        chargerStock();
    }

    /* =====================================================
       CHARGER LE STOCK
       ===================================================== */
    private void chargerStock() {
        try {
            ObservableList<Stock> stocks =
                    FXCollections.observableArrayList(
                            stockDAO.getAllStocks()
                    );

            tableStock.setItems(stocks);

            /* 🔴 LIGNE ROUGE SI SEUIL ATTEINT */
            tableStock.setRowFactory(tv -> new TableRow<>() {
                @Override
                protected void updateItem(Stock item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setStyle("");
                    } else if (item.getQuantite() <= item.getSeuilMin()) {
                        setStyle("-fx-background-color:#ffcccc;");
                    } else {
                        setStyle("");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte(
                    Alert.AlertType.ERROR,
                    "Erreur",
                    null,
                    "Impossible de charger le stock."
            );
        }
    }

    /* =====================================================
       ➕ AJOUT PRODUIT (PRO)
       ===================================================== */
    @FXML
    private void ajouterProduitStock() {

        try {
            String nom = txtProduit.getText();
            int quantite = Integer.parseInt(txtQuantite.getText());
            int seuil = Integer.parseInt(txtSeuil.getText());

            if (nom.isEmpty() || quantite < 0 || seuil < 0) {
                afficherAlerte(
                        Alert.AlertType.WARNING,
                        "Erreur",
                        null,
                        "Veuillez remplir correctement tous les champs."
                );
                return;
            }

            if (stockDAO.findByNom(nom) != null) {
                afficherAlerte(
                        Alert.AlertType.WARNING,
                        "Produit existant",
                        null,
                        "Ce produit existe déjà. Utilisez le réapprovisionnement."
                );
                return;
            }

            stockDAO.ajouterProduit(nom, quantite, seuil);
            chargerStock();
            nettoyerChamps();

            afficherAlerte(
                    Alert.AlertType.INFORMATION,
                    "Succès",
                    null,
                    "Produit ajouté avec succès."
            );

        } catch (NumberFormatException e) {
            afficherAlerte(
                    Alert.AlertType.ERROR,
                    "Erreur",
                    null,
                    "Quantité et seuil doivent être des nombres."
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* =====================================================
       🔄 RÉAPPROVISIONNEMENT (PRO)
       ===================================================== */
    @FXML
    private void reapprovisionnerStock() {

        try {
            String nom = txtProduit.getText();
            int quantite = Integer.parseInt(txtQuantite.getText());

            if (nom.isEmpty() || quantite <= 0) {
                afficherAlerte(
                        Alert.AlertType.WARNING,
                        "Erreur",
                        null,
                        "Veuillez saisir un produit et une quantité valide."
                );
                return;
            }

            if (stockDAO.findByNom(nom) == null) {
                afficherAlerte(
                        Alert.AlertType.ERROR,
                        "Produit inexistant",
                        null,
                        "Ce produit n’existe pas dans le stock."
                );
                return;
            }

            stockDAO.reapprovisionnerStock(nom, quantite);
            chargerStock();
            nettoyerChamps();

            afficherAlerte(
                    Alert.AlertType.INFORMATION,
                    "Succès",
                    null,
                    "Stock réapprovisionné avec succès."
            );

        } catch (NumberFormatException e) {
            afficherAlerte(
                    Alert.AlertType.ERROR,
                    "Erreur",
                    null,
                    "La quantité doit être un nombre."
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* =====================================================
       🧹 UTILITAIRE
       ===================================================== */
    private void nettoyerChamps() {
        txtProduit.clear();
        txtQuantite.clear();
        txtSeuil.clear();
    }

    /* =====================================================
       🔔 ALERTES
       ===================================================== */
    private void afficherAlerte(Alert.AlertType type,
                                String titre,
                                String header,
                                String message) {

        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
