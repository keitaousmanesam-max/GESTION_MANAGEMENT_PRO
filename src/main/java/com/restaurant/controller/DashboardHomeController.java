package com.restaurant.controller;

import com.restaurant.dao.StatistiqueDAO;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.text.NumberFormat;
import java.util.Locale;

public class DashboardHomeController {

    @FXML private Label nbUtilisateurs;
    @FXML private Label nbPlats;
    @FXML private Label nbCommandes;
    @FXML private Label chiffreAffaires;

    /* =========================
       INITIALISATION
       ========================= */
    @FXML
    public void initialize() {
        rafraichir();
        jouerAnimations();
    }

    /* =========================
       RAFRAÎCHIR LES STATISTIQUES (PRO)
       ========================= */
    public void rafraichir() {

        // 🔢 Récupération sécurisée
        int totalUtilisateurs = StatistiqueDAO.count("utilisateur");
        int totalPlats        = StatistiqueDAO.count("plat");
        int totalCommandes    = StatistiqueDAO.count("commande");
        double totalCA        = StatistiqueDAO.chiffreAffairesTotal();

        // 🛡️ Sécurité anti-null
        nbUtilisateurs.setText(String.valueOf(Math.max(0, totalUtilisateurs)));
        nbPlats.setText(String.valueOf(Math.max(0, totalPlats)));
        nbCommandes.setText(String.valueOf(Math.max(0, totalCommandes)));

        // 💰 Format GNF PRO
        NumberFormat nf = NumberFormat.getInstance(Locale.FRANCE);
        nf.setMaximumFractionDigits(0);

        chiffreAffaires.setText(
                nf.format(Math.max(0, totalCA)) + " GNF"
        );
    }

    /* =========================
       🎬 ANIMATIONS PRO
       ========================= */
    private void jouerAnimations() {

        animerLabel(nbUtilisateurs);
        animerLabel(nbPlats);
        animerLabel(nbCommandes);
        animerLabel(chiffreAffaires);
    }

    private void animerLabel(Label label) {

        if (label == null) return;

        FadeTransition fade = new FadeTransition(
                Duration.millis(600), label
        );
        fade.setFromValue(0);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(
                Duration.millis(600), label
        );
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1);
        scale.setToY(1);

        fade.play();
        scale.play();
    }
}
