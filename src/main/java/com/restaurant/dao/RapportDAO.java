package com.restaurant.dao;

import com.restaurant.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RapportDAO {

    /* =========================
       TOTAL DES VENTES (PÉRIODE)
       ========================= */
    public double totalVentes(Date debut, Date fin) {

        String sql = """
            SELECT SUM(f.total) AS total
            FROM facture f
            WHERE f.date_facture BETWEEN ? AND ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, debut);
            ps.setDate(2, fin);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /* =========================
       VENTES PAR MODE DE PAIEMENT
       ========================= */
    public List<String[]> ventesParModePaiement(Date debut, Date fin) {

        List<String[]> list = new ArrayList<>();

        String sql = """
            SELECT p.mode_paiement, SUM(p.montant) AS total
            FROM paiement p
            WHERE p.date_paiement BETWEEN ? AND ?
            GROUP BY p.mode_paiement
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, debut);
            ps.setDate(2, fin);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("mode_paiement"),
                        rs.getString("total")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /* =========================
       TOP PLATS VENDUS
       ========================= */
    public List<String[]> topPlats(int limit) {

        List<String[]> list = new ArrayList<>();

        String sql = """
            SELECT p.nom_plat, SUM(cp.quantite) AS total
            FROM commande_plat cp
            JOIN plat p ON cp.id_plat = p.id_plat
            GROUP BY p.nom_plat
            ORDER BY total DESC
            LIMIT ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("nom_plat"),
                        rs.getString("total")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /* =========================
       PRODUITS EN STOCK FAIBLE
       ========================= */
    public List<String[]> stockFaible() {

        List<String[]> list = new ArrayList<>();

        String sql = """
            SELECT nom_produit, quantite
            FROM stock
            WHERE quantite <= seuil_min
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("nom_produit"),
                        rs.getString("quantite")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
