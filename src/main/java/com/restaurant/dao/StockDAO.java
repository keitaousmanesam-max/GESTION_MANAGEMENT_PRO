package com.restaurant.dao;

import com.restaurant.model.Stock;
import com.restaurant.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {

    /* =========================
       📌 AFFICHAGE DU STOCK
       ========================= */
    public List<Stock> getAllStocks() {

        List<Stock> stocks = new ArrayList<>();
        String sql = "SELECT * FROM stock ORDER BY nom_produit";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                stocks.add(new Stock(
                        rs.getInt("id_stock"),
                        rs.getString("nom_produit"),
                        rs.getInt("quantite"),
                        rs.getInt("seuil_min")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stocks;
    }

    /* =========================
       📉 DÉCRÉMENTATION APRÈS VENTE
       ========================= */
    public boolean decrementerStock(String nomProduit, int qteVendue) {

        String sql = """
            UPDATE stock
            SET quantite = quantite - ?
            WHERE nom_produit = ?
              AND quantite >= ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, qteVendue);
            ps.setString(2, nomProduit);
            ps.setInt(3, qteVendue);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /* =========================
       🚨 PRODUITS EN ALERTE
       ========================= */
    public List<Stock> getStocksEnAlerte() {

        List<Stock> alertes = new ArrayList<>();
        String sql = "SELECT * FROM stock WHERE quantite <= seuil_min";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                alertes.add(new Stock(
                        rs.getInt("id_stock"),
                        rs.getString("nom_produit"),
                        rs.getInt("quantite"),
                        rs.getInt("seuil_min")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return alertes;
    }

    /* =========================
       ➕ AJOUT PRODUIT
       ========================= */
    public boolean ajouterProduit(String nomProduit, int quantite, int seuilMin) {

        String sql =
                "INSERT INTO stock(nom_produit, quantite, seuil_min) VALUES (?,?,?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nomProduit);
            ps.setInt(2, quantite);
            ps.setInt(3, seuilMin);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /* =========================
       🔄 RÉAPPROVISIONNER
       ========================= */
    public boolean reapprovisionnerStock(String nomProduit, int quantite) {

        String sql =
                "UPDATE stock SET quantite = quantite + ? WHERE nom_produit = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantite);
            ps.setString(2, nomProduit);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /* =========================
       ✅ VÉRIFIER STOCK SUFFISANT
       ========================= */
    public boolean stockSuffisant(String nomProduit, int quantiteDemandee) {

        String sql =
                "SELECT quantite FROM stock WHERE nom_produit = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nomProduit);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("quantite") >= quantiteDemandee;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /* =========================
       🔍 RECHERCHE PRODUIT PAR NOM
       ========================= */
    public Stock findByNom(String nomProduit) {

        String sql = "SELECT * FROM stock WHERE nom_produit = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nomProduit);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Stock(
                        rs.getInt("id_stock"),
                        rs.getString("nom_produit"),
                        rs.getInt("quantite"),
                        rs.getInt("seuil_min")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /* =========================
       📊 QUANTITÉ ACTUELLE
       ========================= */
    public int getQuantiteActuelle(String nomProduit) {

        String sql =
                "SELECT quantite FROM stock WHERE nom_produit = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nomProduit);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("quantite");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}
