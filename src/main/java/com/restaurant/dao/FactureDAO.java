package com.restaurant.dao;

import com.restaurant.model.Facture;
import com.restaurant.model.DetailFacture;
import com.restaurant.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FactureDAO {

    /* =====================================================
       🔥 CALCUL TOTAL COMMANDE (SÛR)
       ===================================================== */
    public static double calculerTotalCommande(int idCommande) {

        String sql = """
            SELECT COALESCE(SUM(p.prix * cp.quantite), 0) AS total
            FROM commande_plat cp
            JOIN plat p ON p.id_plat = cp.id_plat
            WHERE cp.id_commande = ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idCommande);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    /* =====================================================
       🧾 CRÉER FACTURE (ANTI-DOUBLON)
       ===================================================== */
    public static boolean creerFacture(int idCommande) {

        if (getFactureParCommande(idCommande) != null) {
            return false; // déjà existante
        }

        double total = calculerTotalCommande(idCommande);

        String sql = """
            INSERT INTO facture (total, id_commande)
            VALUES (?, ?)
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, total);
            ps.setInt(2, idCommande);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /* =====================================================
       🔎 FACTURE PAR COMMANDE
       ===================================================== */
    public static Facture getFactureParCommande(int idCommande) {

        String sql = "SELECT * FROM facture WHERE id_commande = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idCommande);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Facture(
                        rs.getInt("id_facture"),
                        rs.getTimestamp("date_facture").toLocalDateTime(),
                        rs.getDouble("total"),
                        rs.getInt("id_commande")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /* =====================================================
       🔑 ID COMMANDE PAR FACTURE
       ===================================================== */
    public static int getIdCommandeParFacture(int idFacture) {

        String sql = "SELECT id_commande FROM facture WHERE id_facture = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idFacture);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt("id_commande");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    /* =====================================================
       📄 FACTURES DES COMMANDES SERVIES
       ===================================================== */
    public static List<Facture> listeFactures() {

        List<Facture> liste = new ArrayList<>();

        String sql = """
            SELECT f.*
            FROM facture f
            JOIN commande c ON f.id_commande = c.id_commande
            WHERE c.etat_commande = 'SERVIE'
            ORDER BY f.date_facture DESC
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                liste.add(new Facture(
                        rs.getInt("id_facture"),
                        rs.getTimestamp("date_facture").toLocalDateTime(),
                        rs.getDouble("total"),
                        rs.getInt("id_commande")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return liste;
    }

    /* =====================================================
       💰 FACTURES PAYÉES (VÉRITÉ MÉTIER)
       ===================================================== */
    public static List<Facture> listeFacturesPayees() {

        List<Facture> liste = new ArrayList<>();

        String sql = """
            SELECT DISTINCT f.*
            FROM facture f
            JOIN paiement p ON p.id_facture = f.id_facture
            ORDER BY f.date_facture DESC
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                liste.add(new Facture(
                        rs.getInt("id_facture"),
                        rs.getTimestamp("date_facture").toLocalDateTime(),
                        rs.getDouble("total"),
                        rs.getInt("id_commande")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return liste;
    }

    /* =====================================================
       ❓ FACTURE PAYÉE ?
       ===================================================== */
    public static boolean facturePayee(int idFacture) {

        String sql = "SELECT COUNT(*) FROM paiement WHERE id_facture = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idFacture);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt(1) > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /* =====================================================
       📋 DÉTAIL FACTURE
       ===================================================== */
    public static List<DetailFacture> detailsFacture(int idFacture) {

        List<DetailFacture> liste = new ArrayList<>();

        String sql = """
            SELECT p.nom_plat, cp.quantite, p.prix
            FROM facture f
            JOIN commande_plat cp ON f.id_commande = cp.id_commande
            JOIN plat p ON p.id_plat = cp.id_plat
            WHERE f.id_facture = ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idFacture);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                liste.add(new DetailFacture(
                        rs.getString("nom_plat"),
                        rs.getInt("quantite"),
                        rs.getDouble("prix")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return liste;
    }
}
