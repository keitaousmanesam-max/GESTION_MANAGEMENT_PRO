package com.restaurant.dao;

import com.restaurant.model.Facture;
import com.restaurant.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class PaiementDAO {

    /* =====================================================
       💳 ENREGISTRER UN PAIEMENT (VERSION PRO STABLE)
       ===================================================== */
    public static boolean enregistrerPaiement(
            int idFacture,
            String modePaiement,
            double montant) {

        // 🔒 Modes autorisés
        if (!List.of("ESPECES", "CARTE").contains(modePaiement)) {
            System.err.println("❌ Mode de paiement invalide");
            return false;
        }

        // 🔎 Facture
        Facture facture = getFactureParId(idFacture);
        if (facture == null) {
            System.err.println("❌ Facture introuvable");
            return false;
        }

        // 💰 Montant
        if (montant < facture.getTotal()) {
            System.err.println("❌ Montant insuffisant");
            return false;
        }

        // 🔐 Anti double paiement
        if (factureDejaPayee(idFacture)) {
            System.err.println("⚠️ Facture déjà payée");
            return false;
        }

        String sqlPaiement = """
            INSERT INTO paiement (id_facture, montant, mode_paiement, date_paiement)
            VALUES (?, ?, ?, NOW())
        """;

        Connection c = null;

        try {
            c = DBConnection.getConnection();
            c.setAutoCommit(false); // 🔒 TRANSACTION

            // 1️⃣ Enregistrer paiement
            try (PreparedStatement ps = c.prepareStatement(sqlPaiement)) {
                ps.setInt(1, idFacture);
                ps.setDouble(2, montant);
                ps.setString(3, modePaiement);
                ps.executeUpdate();
            }

            // ✅ COMMIT
            c.commit();
            System.out.println("💰 Paiement enregistré avec succès");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erreur paiement → rollback");

            try {
                if (c != null) c.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } finally {
            try {
                if (c != null) {
                    c.setAutoCommit(true);
                    c.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /* =====================================================
       🔎 FACTURE DÉJÀ PAYÉE ?
       ===================================================== */
    public static boolean factureDejaPayee(int idFacture) {

        String sql = "SELECT COUNT(*) FROM paiement WHERE id_facture = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idFacture);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /* =====================================================
       🔎 FACTURE PAR ID
       ===================================================== */
    private static Facture getFactureParId(int idFacture) {

        String sql = "SELECT * FROM facture WHERE id_facture = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idFacture);
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
}
