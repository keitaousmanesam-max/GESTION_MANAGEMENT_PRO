package com.restaurant.controller;

import com.restaurant.dao.StatistiqueDAO;
import com.restaurant.service.RapportGlobalService;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class RapportController {

    /* ================= ROOT ================= */
    @FXML private VBox root;

    /* ================= INDICATEURS ================= */
    @FXML private Label lblCommandesJour;
    @FXML private Label lblCAJour;
    @FXML private Label lblCAMois;

    /* ================= TOP PLATS ================= */
    @FXML private TableView<TopPlat> tableTopPlats;
    @FXML private TableColumn<TopPlat, String> colPlat;
    @FXML private TableColumn<TopPlat, Integer> colQuantite;

    /* ================= HISTORIQUE ================= */
    @FXML private TableView<Vente> tableVentes;
    @FXML private TableColumn<Vente, String> colDate;
    @FXML private TableColumn<Vente, Integer> colFacture;
    @FXML private TableColumn<Vente, Double> colTotal;
    @FXML private TableColumn<Vente, String> colMode;

    /* ================= FORMAT ================= */
    private final NumberFormat monnaie =
            NumberFormat.getInstance(Locale.FRANCE);

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /* =====================================================
       INITIALISATION
       ===================================================== */
    @FXML
    public void initialize() {

        monnaie.setMaximumFractionDigits(0);

        /* ===== Animation d’entrée ===== */
        root.setOpacity(0);
        FadeTransition ft =
                new FadeTransition(Duration.millis(400), root);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        /* ===== Colonnes TOP PLATS ===== */
        colPlat.setCellValueFactory(d -> d.getValue().nomPlatProperty());
        colQuantite.setCellValueFactory(
                d -> d.getValue().quantiteProperty().asObject()
        );

        /* ===== Colonnes HISTORIQUE ===== */
        colDate.setCellValueFactory(d -> d.getValue().dateProperty());
        colFacture.setCellValueFactory(
                d -> d.getValue().factureProperty().asObject()
        );
        colTotal.setCellValueFactory(
                d -> d.getValue().totalProperty().asObject()
        );
        colMode.setCellValueFactory(d -> d.getValue().modeProperty());

        rafraichirRapport();
    }

    /* =====================================================
       🔄 RAFRAÎCHISSEMENT GLOBAL
       ===================================================== */
    private void rafraichirRapport() {
        chargerIndicateurs();
        chargerTopPlats();
        chargerHistorique();
    }

    /* =====================================================
       📊 INDICATEURS
       ===================================================== */
    private void chargerIndicateurs() {

        int commandesJour =
                StatistiqueDAO.commandesParJour();

        double caJour =
                StatistiqueDAO.chiffreAffairesParJour();

        LocalDate now = LocalDate.now();
        double caMois =
                StatistiqueDAO.chiffreAffairesMois(
                        now.getYear(),
                        now.getMonthValue()
                );

        lblCommandesJour.setText(String.valueOf(commandesJour));
        lblCAJour.setText(monnaie.format(caJour) + " GNF");
        lblCAMois.setText(monnaie.format(caMois) + " GNF");
    }

    /* =====================================================
       🍽️ TOP PLATS
       ===================================================== */
    private void chargerTopPlats() {

        ObservableList<TopPlat> data =
                FXCollections.observableArrayList();

        for (Map.Entry<String, Integer> e :
                StatistiqueDAO.topPlats().entrySet()) {

            data.add(new TopPlat(
                    e.getKey(),
                    e.getValue()
            ));
        }

        tableTopPlats.setItems(data);
    }

    /* =====================================================
       🧾 HISTORIQUE DES VENTES
       ===================================================== */
    private void chargerHistorique() {

        ObservableList<Vente> data =
                FXCollections.observableArrayList();

        for (Map<String, Object> v :
                StatistiqueDAO.historiqueVentesSimple()) {

            String date = v.get("date") != null
                    ? v.get("date").toString()
                    : "";

            int facture = v.get("facture") != null
                    ? ((Number) v.get("facture")).intValue()
                    : 0;

            double total = v.get("total") != null
                    ? ((Number) v.get("total")).doubleValue()
                    : 0.0;

            String mode = v.get("mode") != null
                    ? v.get("mode").toString()
                    : "";

            data.add(new Vente(date, facture, total, mode));
        }

        tableVentes.setItems(data);
    }

    /* =====================================================
       📦 EXPORT RAPPORT GLOBAL (CSV)
       ===================================================== */
    @FXML
    private void exporterExcel() {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exporter le rapport global");
        chooser.setInitialFileName(
                "rapport_global_" + LocalDate.now() + ".csv"
        );

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Fichier Excel (*.csv)", "*.csv")
        );

        File fichier =
                chooser.showSaveDialog(
                        root.getScene().getWindow()
                );

        if (fichier != null) {

            RapportGlobalService
                    .exporterRapportCompletCSV(
                            fichier.getAbsolutePath()
                    );

            new Alert(
                    Alert.AlertType.INFORMATION,
                    "Rapport global exporté avec succès ✔"
            ).showAndWait();
        }
    }

    /* =====================================================
       📄 EXPORT PDF (EXTENSIBLE)
       ===================================================== */
    @FXML
    private void exporterPDF() {

        new Alert(
                Alert.AlertType.INFORMATION,
                "Export PDF prêt (OpenPDF / iText à brancher)"
        ).showAndWait();
    }

    /* =====================================================
       🧱 MODELS VIEW (PRO)
       ===================================================== */
    public static class TopPlat {

        private final javafx.beans.property.SimpleStringProperty nomPlat;
        private final javafx.beans.property.SimpleIntegerProperty quantite;

        public TopPlat(String nomPlat, int quantite) {
            this.nomPlat =
                    new javafx.beans.property.SimpleStringProperty(nomPlat);
            this.quantite =
                    new javafx.beans.property.SimpleIntegerProperty(quantite);
        }

        public javafx.beans.property.SimpleStringProperty nomPlatProperty() {
            return nomPlat;
        }

        public javafx.beans.property.SimpleIntegerProperty quantiteProperty() {
            return quantite;
        }
    }

    public static class Vente {

        private final javafx.beans.property.SimpleStringProperty date;
        private final javafx.beans.property.SimpleIntegerProperty facture;
        private final javafx.beans.property.SimpleDoubleProperty total;
        private final javafx.beans.property.SimpleStringProperty mode;

        public Vente(String date, int facture,
                     double total, String mode) {

            this.date =
                    new javafx.beans.property.SimpleStringProperty(date);
            this.facture =
                    new javafx.beans.property.SimpleIntegerProperty(facture);
            this.total =
                    new javafx.beans.property.SimpleDoubleProperty(total);
            this.mode =
                    new javafx.beans.property.SimpleStringProperty(mode);
        }

        public javafx.beans.property.SimpleStringProperty dateProperty() {
            return date;
        }

        public javafx.beans.property.SimpleIntegerProperty factureProperty() {
            return facture;
        }

        public javafx.beans.property.SimpleDoubleProperty totalProperty() {
            return total;
        }

        public javafx.beans.property.SimpleStringProperty modeProperty() {
            return mode;
        }
    }
}
