package com.restaurant.controller;

import com.restaurant.dao.UtilisateurDAO;
import com.restaurant.model.Role;
import com.restaurant.model.Utilisateur;
import com.restaurant.util.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ModifierUtilisateurController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField loginField;
    @FXML private PasswordField mdpField;
    @FXML private ComboBox<Role> roleCombo;

    // 🛑 Message PRO d’avertissement
    @FXML private Label lblAvertissement;

    private Utilisateur utilisateur;
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    /* =========================
       INITIALISATION DONNÉES
       ========================= */
    public void initData(Utilisateur u) {
        this.utilisateur = u;

        nomField.setText(u.getNom());
        prenomField.setText(u.getPrenom());
        loginField.setText(u.getLogin());
        roleCombo.setValue(u.getRole());

        verifierDernierAdministrateur();
    }

    @FXML
    public void initialize() {
        chargerRoles();

        if (lblAvertissement != null) {
            lblAvertissement.setVisible(false);
        }
    }

    /* =========================
       CHARGER ROLES
       ========================= */
    private void chargerRoles() {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM role")) {

            while (rs.next()) {
                roleCombo.getItems().add(
                        new Role(
                                rs.getInt("id_role"),
                                rs.getString("nom_role")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* =========================
       🔐 RÈGLE PRO : DERNIER ADMIN
       ========================= */
    private void verifierDernierAdministrateur() {

        if (utilisateur == null) return;

        boolean estAdmin =
                "ADMINISTRATEUR".equalsIgnoreCase(
                        utilisateur.getRole().getNomRole()
                );

        if (!estAdmin) return;

        int nbAdminsActifs = utilisateurDAO.compterAdminsActifs();

        if (nbAdminsActifs <= 1) {
            // 🔒 Blocage UI
            roleCombo.setDisable(true);

            if (lblAvertissement != null) {
                lblAvertissement.setText(
                        "🔐 Ce compte est le dernier administrateur actif.\n" +
                                "Le rôle ne peut pas être modifié."
                );
                lblAvertissement.setVisible(true);
            }
        }
    }

    /* =========================
       ENREGISTRER
       ========================= */
    @FXML
    private void handleEnregistrer() {

        // 🔐 Sécurité finale (backend logique)
        if ("ADMINISTRATEUR".equalsIgnoreCase(utilisateur.getRole().getNomRole())
                && utilisateurDAO.compterAdminsActifs() <= 1) {

            Role roleChoisi = roleCombo.getValue();

            if (!"ADMINISTRATEUR".equalsIgnoreCase(roleChoisi.getNomRole())) {
                afficherAlerte(
                        "Action interdite",
                        "Impossible de modifier le rôle du dernier administrateur actif."
                );
                return;
            }
        }

        utilisateur.setNom(nomField.getText());
        utilisateur.setPrenom(prenomField.getText());
        utilisateur.setLogin(loginField.getText());
        utilisateur.setRole(roleCombo.getValue());

        // 🔐 Mot de passe optionnel
        if (!mdpField.getText().isEmpty()) {
            utilisateur.setMotDePasse(mdpField.getText());
        } else {
            utilisateur.setMotDePasse(null);
        }

        boolean ok = utilisateurDAO.modifier(utilisateur);

        if (ok) {
            fermer();
        } else {
            afficherAlerte("Erreur", "Impossible de modifier l'utilisateur.");
        }
    }

    @FXML
    private void handleAnnuler() {
        fermer();
    }

    private void fermer() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    private void afficherAlerte(String titre, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
