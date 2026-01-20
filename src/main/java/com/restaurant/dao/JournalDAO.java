package com.restaurant.dao;

import com.restaurant.model.Journal;
import com.restaurant.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JournalDAO {

    /* =====================================================
       📝 ENREGISTRER UNE ACTION (PRO & SÉCURISÉ)
       ===================================================== */
    public static boolean enregistrer(int idUtilisateur, String action) {

        if (action == null || action.isBlank()) {
            System.err.println("⚠️ Action journal vide – ignorée");
            return false;
        }

        String sql = """
            INSERT INTO journal (action, id_utilisateur)
            VALUES (?, ?)
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, action.trim());
            ps.setInt(2, idUtilisateur);
            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /* Alias propre */
    public static void log(int idUtilisateur, String action) {
        enregistrer(idUtilisateur, action);
    }

    /* =====================================================
       📋 JOURNAL COMPLET (ADMIN / HISTORIQUE)
       ===================================================== */
    public static List<Journal> getAll() {

        List<Journal> list = new ArrayList<>();

        String sql = """
            SELECT j.id_journal                                   AS id,
                   COALESCE(CONCAT(u.prenom, ' ', u.nom),
                            'Utilisateur supprimé')             AS utilisateur,
                   COALESCE(r.nom_role, '-')                     AS role,
                   j.action                                      AS action,
                   DATE_FORMAT(j.date_action, '%d/%m/%Y %H:%i')  AS date_action
            FROM journal j
            LEFT JOIN utilisateur u ON j.id_utilisateur = u.id_utilisateur
            LEFT JOIN role r ON u.id_role = r.id_role
            ORDER BY j.date_action DESC
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Journal(
                        rs.getInt("id"),
                        rs.getString("utilisateur"),
                        rs.getString("role"),
                        rs.getString("action"),
                        rs.getString("date_action")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /* =====================================================
       🔐 JOURNAL PAR UTILISATEUR
       ===================================================== */
    public static List<Journal> getByUtilisateur(int idUtilisateur) {

        List<Journal> list = new ArrayList<>();

        String sql = """
            SELECT j.id_journal                              AS id,
                   CONCAT(u.prenom, ' ', u.nom)             AS utilisateur,
                   r.nom_role                               AS role,
                   j.action                                 AS action,
                   DATE_FORMAT(j.date_action, '%d/%m/%Y %H:%i') AS date_action
            FROM journal j
            JOIN utilisateur u ON j.id_utilisateur = u.id_utilisateur
            JOIN role r ON u.id_role = r.id_role
            WHERE u.id_utilisateur = ?
            ORDER BY j.date_action DESC
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idUtilisateur);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Journal(
                            rs.getInt("id"),
                            rs.getString("utilisateur"),
                            rs.getString("role"),
                            rs.getString("action"),
                            rs.getString("date_action")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /* =====================================================
       📅 JOURNAL PAR DATE (BUG FIXÉ)
       ===================================================== */
    public static List<Journal> getByDate(LocalDate date) {

        List<Journal> list = new ArrayList<>();

        String sql = """
            SELECT j.id_journal                              AS id,
                   CONCAT(u.prenom, ' ', u.nom)             AS utilisateur,
                   r.nom_role                               AS role,
                   j.action                                 AS action,
                   DATE_FORMAT(j.date_action, '%d/%m/%Y %H:%i') AS date_action
            FROM journal j
            JOIN utilisateur u ON j.id_utilisateur = u.id_utilisateur
            JOIN role r ON u.id_role = r.id_role
            WHERE DATE(j.date_action) = ?
            ORDER BY j.date_action DESC
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Journal(
                            rs.getInt("id"),
                            rs.getString("utilisateur"),
                            rs.getString("role"),
                            rs.getString("action"),
                            rs.getString("date_action")
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /* =====================================================
       🧮 STATISTIQUES (ADMIN)
       ===================================================== */
    public static long countActions() {

        String sql = "SELECT COUNT(*) FROM journal";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getLong(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}
