package com.restaurant.dao;

import com.restaurant.model.Plat;
import com.restaurant.util.DBConnection;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;
import java.util.ArrayList;

public class PlatDAO {

    /* =====================================================
       =============== LISTE DE TOUS LES PLATS =============
       ===================================================== */
    public static List<Plat> listePlats() {

        List<Plat> liste = new ArrayList<>();
        String sql = "SELECT * FROM plat ORDER BY nom_plat";

        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                Plat p = new Plat(
                        rs.getInt("id_plat"),
                        rs.getString("nom_plat"),
                        rs.getString("categorie"),
                        rs.getDouble("prix"),
                        rs.getString("disponibilite"),
                        rs.getInt("id_menu")
                );

                liste.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return liste;
    }

    /* =====================================================
       ==================== AJOUT ==========================
       ===================================================== */
    public static boolean ajouter(Plat p) {

        String sql = """
            INSERT INTO plat (nom_plat, categorie, prix, disponibilite, id_menu)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, p.getNom());
            ps.setString(2, p.getCategorie());
            ps.setDouble(3, p.getPrix());
            ps.setString(4, p.getDisponibilite());
            ps.setInt(5, p.getIdMenu());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /* =====================================================
       =================== MODIFIER ========================
       ===================================================== */
    public static boolean modifier(Plat p) {

        String sql = """
            UPDATE plat
            SET nom_plat = ?,
                categorie = ?,
                prix = ?,
                disponibilite = ?,
                id_menu = ?
            WHERE id_plat = ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, p.getNom());
            ps.setString(2, p.getCategorie());
            ps.setDouble(3, p.getPrix());
            ps.setString(4, p.getDisponibilite());
            ps.setInt(5, p.getIdMenu());
            ps.setInt(6, p.getIdPlat());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /* =====================================================
       =================== SUPPRIMER =======================
       ===================================================== */
    public static boolean supprimer(int id) {

        String sql = "DELETE FROM plat WHERE id_plat = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /* =====================================================
       ===== PLATS PAR MENU (TOUS : DISP + INDISP) =========
       ===================================================== */
    public static List<Plat> listePlatsParMenu(int idMenu) {

        List<Plat> liste = new ArrayList<>();

        // ✅ AUCUN FILTRE SUR DISPONIBILITE
        String sql = """
            SELECT *
            FROM plat
            WHERE id_menu = ?
            ORDER BY nom_plat
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idMenu);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Plat p = new Plat(
                        rs.getInt("id_plat"),
                        rs.getString("nom_plat"),
                        rs.getString("categorie"),
                        rs.getDouble("prix"),
                        rs.getString("disponibilite"),
                        rs.getInt("id_menu")
                );

                liste.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return liste;
    }
}
