package com.restaurant.dao;

import com.restaurant.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class StatistiqueDAO {

    /* =====================================================
       🔢 COMPTER LE NOMBRE DE LIGNES D’UNE TABLE
       ===================================================== */
    public static int count(String table) {

        if (!table.matches("[a-zA-Z_]+")) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM " + table;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    /* =====================================================
       💰 CHIFFRE D’AFFAIRES TOTAL
       ===================================================== */
    public static double chiffreAffairesTotal() {

        String sql = "SELECT COALESCE(SUM(montant),0) FROM paiement";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getDouble(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    /* =====================================================
       📅 CA DU JOUR
       ===================================================== */
    public static double chiffreAffairesParJour() {

        String sql = """
            SELECT COALESCE(SUM(montant),0)
            FROM paiement
            WHERE DATE(date_paiement) = CURDATE()
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getDouble(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    /* =====================================================
       📅 CA DU MOIS
       ===================================================== */
    public static double chiffreAffairesMois(int annee, int mois) {

        String sql = """
            SELECT COALESCE(SUM(montant),0)
            FROM paiement
            WHERE YEAR(date_paiement) = ?
              AND MONTH(date_paiement) = ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, annee);
            ps.setInt(2, mois);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    /* =====================================================
       📦 COMMANDES DU JOUR (SERVIES)
       ===================================================== */
    public static int commandesParJour() {

        String sql = """
            SELECT COUNT(*)
            FROM commande
            WHERE DATE(date_commande) = CURDATE()
              AND etat_commande = 'SERVIE'
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

    /* =====================================================
       🍽️ TOP 5 PLATS
       ===================================================== */
    public static Map<String, Integer> topPlats() {

        Map<String, Integer> result = new LinkedHashMap<>();

        String sql = """
            SELECT p.nom_plat, SUM(cp.quantite) AS total
            FROM commande_plat cp
            JOIN plat p ON p.id_plat = cp.id_plat
            JOIN commande c ON c.id_commande = cp.id_commande
            WHERE c.etat_commande = 'SERVIE'
            GROUP BY p.nom_plat
            ORDER BY total DESC
            LIMIT 5
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.put(
                        rs.getString("nom_plat"),
                        rs.getInt("total")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /* =====================================================
       📈 CA PAR JOUR (FACTURES PAYÉES)
       ===================================================== */
    public static Map<String, Double> chiffreAffairesParJourGraph() {

        Map<String, Double> data = new LinkedHashMap<>();

        String sql = """
            SELECT DATE(date_paiement) AS jour,
                   COALESCE(SUM(montant),0) AS total
            FROM paiement
            GROUP BY jour
            ORDER BY jour
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                data.put(
                        rs.getString("jour"),
                        rs.getDouble("total")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    /* =====================================================
       📊 COMMANDES PAR JOUR (SERVIES)
       ===================================================== */
    public static Map<String, Integer> commandesParJourGraph() {

        Map<String, Integer> data = new LinkedHashMap<>();

        String sql = """
            SELECT DATE(date_commande) AS jour,
                   COUNT(*) AS total
            FROM commande
            WHERE etat_commande = 'SERVIE'
            GROUP BY jour
            ORDER BY jour
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                data.put(
                        rs.getString("jour"),
                        rs.getInt("total")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    /* =====================================================
       🥧 RÉPARTITION DES MODES DE PAIEMENT
       ===================================================== */
    public static Map<String, Double> repartitionPaiements() {

        Map<String, Double> data = new LinkedHashMap<>();

        String sql = """
            SELECT
                CASE
                    WHEN LOWER(mode_paiement) LIKE '%espe%' THEN 'ESPÈCES'
                    WHEN LOWER(mode_paiement) LIKE '%cart%' THEN 'CARTE'
                    ELSE 'AUTRE'
                END AS mode,
                COALESCE(SUM(montant),0) AS total
            FROM paiement
            GROUP BY mode
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                data.put(
                        rs.getString("mode"),
                        rs.getDouble("total")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    /* =====================================================
       📜 HISTORIQUE SIMPLE DES VENTES
       ===================================================== */
    public static List<Map<String, Object>> historiqueVentesSimple() {

        List<Map<String, Object>> ventes = new ArrayList<>();

        String sql = """
            SELECT
                f.date_facture  AS date,
                f.id_facture    AS facture,
                p.montant       AS total,
                p.mode_paiement AS mode
            FROM facture f
            JOIN paiement p ON p.id_facture = f.id_facture
            ORDER BY f.date_facture DESC
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Map<String, Object> vente = new HashMap<>();

                vente.put("date", rs.getTimestamp("date"));
                vente.put("facture", rs.getInt("facture"));
                vente.put("total", rs.getDouble("total"));
                vente.put("mode", rs.getString("mode"));

                ventes.add(vente);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ventes;
    }
}
