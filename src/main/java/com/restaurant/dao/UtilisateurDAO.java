package com.restaurant.dao;

import com.restaurant.model.Role;
import com.restaurant.model.Utilisateur;
import com.restaurant.util.DBConnection;
import com.restaurant.util.PasswordUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    /* =========================
       AUTHENTIFICATION (ACTIF)
       ========================= */
    public Utilisateur login(String login, String mdpClair) {

        String sql = """
            SELECT u.*, r.id_role, r.nom_role
            FROM utilisateur u
            JOIN role r ON u.id_role = r.id_role
            WHERE u.login = ?
              AND u.mot_de_passe = ?
              AND u.etat_compte = 'ACTIF'
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setString(2, PasswordUtils.hash(mdpClair));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return construireUtilisateur(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /* =========================
       AUTHENTIFICATION SANS ÉTAT
       ========================= */
    public Utilisateur loginSansEtat(String login, String mdpClair) {

        String sql = """
            SELECT u.*, r.id_role, r.nom_role
            FROM utilisateur u
            JOIN role r ON u.id_role = r.id_role
            WHERE u.login = ?
              AND u.mot_de_passe = ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setString(2, PasswordUtils.hash(mdpClair));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return construireUtilisateur(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /* =========================
       LISTE UTILISATEURS
       ========================= */
    public List<Utilisateur> listeUtilisateurs() {

        List<Utilisateur> liste = new ArrayList<>();

        String sql = """
            SELECT u.*, r.id_role, r.nom_role
            FROM utilisateur u
            JOIN role r ON u.id_role = r.id_role
        """;

        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                liste.add(construireUtilisateur(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return liste;
    }

    /* =========================
       AJOUT UTILISATEUR
       ========================= */
    public boolean ajouter(Utilisateur u) {

        String sql = """
            INSERT INTO utilisateur
            (nom, prenom, login, mot_de_passe, etat_compte, id_role)
            VALUES (?, ?, ?, ?, 'ACTIF', ?)
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getLogin());
            ps.setString(4, PasswordUtils.hash(u.getMotDePasse()));
            ps.setInt(5, u.getRole().getIdRole());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /* =========================
       MODIFIER UTILISATEUR (SANS MDP)
       ========================= */
    public boolean modifier(Utilisateur u) {

        String sql = """
            UPDATE utilisateur
            SET nom = ?, prenom = ?, login = ?, etat_compte = ?, id_role = ?
            WHERE id_utilisateur = ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getLogin());
            ps.setString(4, u.getEtatCompte());
            ps.setInt(5, u.getRole().getIdRole());
            ps.setInt(6, u.getIdUtilisateur());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /* =========================
       🔐 MODIFIER UTILISATEUR AVEC MDP OPTIONNEL (PRO)
       ========================= */
    public boolean modifierAvecMotDePasse(Utilisateur u) {

        boolean modifierMdp =
                u.getMotDePasse() != null && !u.getMotDePasse().isEmpty();

        String sql = modifierMdp ?
                """
                UPDATE utilisateur
                SET nom=?, prenom=?, login=?, etat_compte=?, id_role=?, mot_de_passe=?
                WHERE id_utilisateur=?
                """
                :
                """
                UPDATE utilisateur
                SET nom=?, prenom=?, login=?, etat_compte=?, id_role=?
                WHERE id_utilisateur=?
                """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getLogin());
            ps.setString(4, u.getEtatCompte());
            ps.setInt(5, u.getRole().getIdRole());

            if (modifierMdp) {
                ps.setString(6, PasswordUtils.hash(u.getMotDePasse()));
                ps.setInt(7, u.getIdUtilisateur());
            } else {
                ps.setInt(6, u.getIdUtilisateur());
            }

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /* =========================
       🔐 CHANGER MOT DE PASSE (ÉCRAN DÉDIÉ)
       ========================= */
    public boolean modifierMotDePasse(int idUtilisateur, String mdpClair) {

        String sql = """
            UPDATE utilisateur
            SET mot_de_passe = ?
            WHERE id_utilisateur = ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, PasswordUtils.hash(mdpClair));
            ps.setInt(2, idUtilisateur);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /* =========================
       CHANGER ÉTAT COMPTE
       ========================= */
    public boolean changerEtatCompte(int id, String etat) {

        String sql =
                "UPDATE utilisateur SET etat_compte = ? WHERE id_utilisateur = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, etat);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /* =========================
       COMPTER ADMINS ACTIFS
       ========================= */
    public int compterAdminsActifs() {

        String sql = """
            SELECT COUNT(*)
            FROM utilisateur u
            JOIN role r ON u.id_role = r.id_role
            WHERE r.nom_role = 'ADMINISTRATEUR'
              AND u.etat_compte = 'ACTIF'
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    /* =========================
       SUPPRIMER UTILISATEUR (SÉCURISÉ)
       ========================= */
    public boolean supprimer(int idUtilisateur) {

        String sqlRole = """
            SELECT r.nom_role
            FROM utilisateur u
            JOIN role r ON u.id_role = r.id_role
            WHERE u.id_utilisateur = ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement psRole = c.prepareStatement(sqlRole)) {

            psRole.setInt(1, idUtilisateur);
            ResultSet rs = psRole.executeQuery();

            if (rs.next()) {
                String role = rs.getString("nom_role");

                if ("ADMINISTRATEUR".equalsIgnoreCase(role)
                        && compterAdminsActifs() <= 1) {
                    return false;
                }
            }

            String sqlDelete =
                    "DELETE FROM utilisateur WHERE id_utilisateur = ?";

            try (PreparedStatement psDelete =
                         c.prepareStatement(sqlDelete)) {

                psDelete.setInt(1, idUtilisateur);
                return psDelete.executeUpdate() > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /* =========================
       🔍 LOGIN EXISTANT (SÉCURITÉ)
       ========================= */
    public boolean loginExiste(String login, int idExclu) {

        String sql = """
            SELECT 1 FROM utilisateur
            WHERE login = ?
              AND id_utilisateur <> ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setInt(2, idExclu);

            return ps.executeQuery().next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /* =========================
       FACTORY UTILISATEUR
       ========================= */
    private Utilisateur construireUtilisateur(ResultSet rs) throws SQLException {

        Role role = new Role(
                rs.getInt("id_role"),
                rs.getString("nom_role")
        );

        return new Utilisateur(
                rs.getInt("id_utilisateur"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("login"),
                rs.getString("etat_compte"),
                role
        );
    }
}
