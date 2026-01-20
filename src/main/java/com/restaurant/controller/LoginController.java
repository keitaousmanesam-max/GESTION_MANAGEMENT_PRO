package com.restaurant.controller;

import com.restaurant.dao.JournalDAO;
import com.restaurant.dao.UtilisateurDAO;
import com.restaurant.model.Utilisateur;
import com.restaurant.service.AuthService;
import com.restaurant.util.SessionUtilisateur;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisibleField;
    @FXML private Label messageLabel;
    @FXML private ImageView backgroundImage;
    @FXML private VBox loginBox;

    private final AuthService authService = new AuthService();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    // 🔐 Sécurité tentatives
    private int tentatives = 0;
    private static final int MAX_TENTATIVES = 3;
    private boolean bloque = false;

    /* =========================
       INITIALISATION
       ========================= */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        loginBox.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {

                backgroundImage.fitWidthProperty()
                        .bind(newScene.widthProperty());
                backgroundImage.fitHeightProperty()
                        .bind(newScene.heightProperty());

                FadeTransition fade =
                        new FadeTransition(Duration.seconds(1.2), loginBox);
                fade.setFromValue(0);
                fade.setToValue(1);

                TranslateTransition slide =
                        new TranslateTransition(Duration.seconds(0.8), loginBox);
                slide.setFromY(60);
                slide.setToY(0);

                fade.play();
                slide.play();
            }
        });
    }

    /* =========================
       ACTION LOGIN
       ========================= */
    @FXML
    private void handleLogin() {

        if (bloque) return;

        String login = loginField.getText().trim();
        String password = passwordField.isVisible()
                ? passwordField.getText()
                : passwordVisibleField.getText();

        if (login.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs.");
            shakeForm();
            return;
        }

        Utilisateur utilisateur =
                authService.authentifier(login, password);

        /* ===== ÉCHEC ===== */
        if (utilisateur == null) {

            tentatives++;

            // 🧾 JOURNAL : ÉCHEC DE CONNEXION
            JournalDAO.log(
                    0,
                    "Échec de connexion pour le login : " + login
            );

            Utilisateur uInactif =
                    utilisateurDAO.loginSansEtat(login, password);

            if (uInactif != null &&
                    "INACTIF".equalsIgnoreCase(uInactif.getEtatCompte())) {

                messageLabel.setText("Votre compte est désactivé.");
            } else {
                messageLabel.setText(
                        "Login ou mot de passe incorrect (" +
                                tentatives + "/" + MAX_TENTATIVES + ")"
                );
            }

            shakeForm();

            if (tentatives >= MAX_TENTATIVES) {
                bloquerConnexion();
            }
            return;
        }

        /* ===== SUCCÈS ===== */
        tentatives = 0;

        // 🧾 JOURNAL : CONNEXION RÉUSSIE
        JournalDAO.log(
                utilisateur.getIdUtilisateur(),
                "Connexion réussie au système"
        );

        SessionUtilisateur.setUtilisateurConnecte(utilisateur);
        ouvrirDashboard(utilisateur);
    }

    /* =========================
       BLOCAGE TEMPORAIRE
       ========================= */
    private void bloquerConnexion() {

        bloque = true;
        messageLabel.setText("Trop de tentatives. Réessayez dans 30 secondes.");

        loginField.setDisable(true);
        passwordField.setDisable(true);
        passwordVisibleField.setDisable(true);

        new Thread(() -> {
            try {
                Thread.sleep(30_000);
            } catch (InterruptedException ignored) {}

            Platform.runLater(() -> {
                tentatives = 0;
                bloque = false;
                messageLabel.setText("");
                loginField.setDisable(false);
                passwordField.setDisable(false);
                passwordVisibleField.setDisable(false);
            });
        }).start();
    }

    /* =========================
       ŒIL MOT DE PASSE
       ========================= */
    @FXML
    private void togglePassword() {

        if (passwordVisibleField.isVisible()) {

            passwordField.setText(passwordVisibleField.getText());
            passwordVisibleField.setVisible(false);
            passwordVisibleField.setManaged(false);

            passwordField.setVisible(true);
            passwordField.setManaged(true);

        } else {

            passwordVisibleField.setText(passwordField.getText());
            passwordVisibleField.setVisible(true);
            passwordVisibleField.setManaged(true);

            passwordField.setVisible(false);
            passwordField.setManaged(false);
        }
    }

    /* =========================
       ANIMATION ERREUR
       ========================= */
    private void shakeForm() {

        TranslateTransition shake =
                new TranslateTransition(Duration.millis(80), loginBox);

        shake.setFromX(-10);
        shake.setToX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }

    /* =========================
       DASHBOARD
       ========================= */
    private void ouvrirDashboard(Utilisateur utilisateur) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/dashboard.fxml")
            );

            Scene scene = new Scene(loader.load());

            var cssUrl = getClass().getResource("/css/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            // 🔎 TRACE DE CONTRÔLE (IMPORTANT)
            System.out.println(
                    "👤 LOGIN OK → ID UTILISATEUR = "
                            + utilisateur.getIdUtilisateur()
            );

            DashboardController controller = loader.getController();
            controller.initData(utilisateur);

            Stage loginStage =
                    (Stage) loginField.getScene().getWindow();
            loginStage.close();

            Stage stage = new Stage();
            stage.setTitle("Dashboard - Gestion Restaurant");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText(
                    "Erreur lors de l'ouverture du dashboard."
            );
        }
    }
}
