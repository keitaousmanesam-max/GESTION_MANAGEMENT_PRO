package com.restaurant.dao;

import com.restaurant.model.Commande;
import com.restaurant.model.CommandePlat;
import com.restaurant.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeDAO {

    /* =====================================================
       CRÉER COMMANDE
       ===================================================== */
    public static int creerCommande(int idTable, int idServeur) {

        String sql = """
            INSERT INTO commande (id_table, id_serveur, etat_commande)
            VALUES (?, ?, 'EN_COURS')
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps =
                     c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, idTable);
            ps.setInt(2, idServeur);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int idCommande = rs.getInt(1);

                // ✅ OCCUPER TABLE À LA CRÉATION
                TableRestaurantDAO.occuperTable(idTable);
                return idCommande;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    /* =====================================================
       🔁 AJOUT PRO : COMMANDE EN COURS PAR TABLE
       ===================================================== */
    public static Commande getCommandeEnCoursParTable(int idTable) {

        String sql = """
            SELECT *
            FROM commande
            WHERE id_table = ?
              AND etat_commande = 'EN_COURS'
            LIMIT 1
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idTable);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Commande(
                        rs.getInt("id_commande"),
                        rs.getTimestamp("date_commande").toLocalDateTime(),
                        rs.getString("etat_commande"),
                        rs.getInt("id_table"),
                        rs.getInt("id_serveur")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /* =====================================================
       🔁 AJOUT PRO : OUVRIR OU CRÉER COMMANDE (ANTI-DOUBLON)
       ===================================================== */
    public static synchronized int ouvrirOuCreerCommande(
            int idTable, int idServeur) {

        Commande existante = getCommandeEnCoursParTable(idTable);

        if (existante != null) {
            return existante.getIdCommande();
        }

        return creerCommande(idTable, idServeur);
    }

    /* =====================================================
       SERVIR COMMANDE (INCHANGÉE)
       ===================================================== */
    public static boolean servirCommande(int idCommande) {

        String sql = """
            UPDATE commande
            SET etat_commande = 'SERVIE'
            WHERE id_commande = ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idCommande);
            if (ps.executeUpdate() == 0) return false;

            // ✅ facture + stock OK ici
            creerFactureEtMajStock(idCommande);

            // ❌ table libérée UNIQUEMENT au paiement
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /* =====================================================
       💰 PAYER COMMANDE (PRO)
       ===================================================== */
    public static boolean payerCommande(int idCommande, int idTable) {

        String sqlCommande = """
            UPDATE commande
            SET etat_commande = 'PAYEE'
            WHERE id_commande = ?
        """;

        String sqlTable = """
            UPDATE table_restaurant
            SET etat_table = 'LIBRE'
            WHERE id_table = ?
        """;

        try (Connection c = DBConnection.getConnection()) {

            c.setAutoCommit(false);

            try (PreparedStatement psCmd =
                         c.prepareStatement(sqlCommande);
                 PreparedStatement psTable =
                         c.prepareStatement(sqlTable)) {

                psCmd.setInt(1, idCommande);
                psCmd.executeUpdate();

                psTable.setInt(1, idTable);
                psTable.executeUpdate();

                c.commit();
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /* =====================================================
       FACTURE + STOCK
       ===================================================== */
    private static void creerFactureEtMajStock(int idCommande) {

        FactureDAO.creerFacture(idCommande);

        StockDAO stockDAO = new StockDAO();
        List<CommandePlat> plats =
                CommandePlatDAO.getPlatsParCommande(idCommande);

        for (CommandePlat cp : plats) {
            stockDAO.decrementerStock(
                    cp.getNomPlat(),
                    cp.getQuantite()
            );
        }
    }

    /* =====================================================
       SUPPRIMER COMMANDE
       ===================================================== */
    public static boolean supprimerCommande(int idCommande) {

        String sql = "DELETE FROM commande WHERE id_commande = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            CommandePlatDAO.supprimerTousLesPlats(idCommande);
            TableRestaurantDAO.libererTableParCommande(idCommande);

            ps.setInt(1, idCommande);
            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /* =====================================================
       HISTORIQUE
       ===================================================== */
    public static List<Commande> listeCommandesHistoriqueEtEnCours() {

        List<Commande> liste = new ArrayList<>();

        String sql = """
            SELECT *
            FROM commande
            WHERE etat_commande IN ('EN_COURS', 'SERVIE')
            ORDER BY date_commande DESC
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                liste.add(new Commande(
                        rs.getInt("id_commande"),
                        rs.getTimestamp("date_commande").toLocalDateTime(),
                        rs.getString("etat_commande"),
                        rs.getInt("id_table"),
                        rs.getInt("id_serveur")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return liste;
    }

    /* =====================================================
       COMMANDES SERVIES NON PAYÉES
       ===================================================== */
    public static List<Commande> listeCommandesServiesNonPayees() {

        List<Commande> liste = new ArrayList<>();

        String sql = """
            SELECT *
            FROM commande
            WHERE etat_commande = 'SERVIE'
            ORDER BY date_commande DESC
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                liste.add(new Commande(
                        rs.getInt("id_commande"),
                        rs.getTimestamp("date_commande").toLocalDateTime(),
                        rs.getString("etat_commande"),
                        rs.getInt("id_table"),
                        rs.getInt("id_serveur")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return liste;
    }
}
