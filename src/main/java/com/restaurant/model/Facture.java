package com.restaurant.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;

/**
 * Représente une facture (table facture).
 * L'état de paiement est déterminé dynamiquement
 * via la table paiement (PAS stocké ici).
 */
public class Facture {

    private final IntegerProperty idFacture = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDateTime> dateFacture = new SimpleObjectProperty<>();
    private final DoubleProperty total = new SimpleDoubleProperty();
    private final IntegerProperty idCommande = new SimpleIntegerProperty();

    public Facture() {
    }

    public Facture(int idFacture,
                   LocalDateTime dateFacture,
                   double total,
                   int idCommande) {

        this.idFacture.set(idFacture);
        this.dateFacture.set(dateFacture);
        this.total.set(total);
        this.idCommande.set(idCommande);
    }

    /* ================= GETTERS ================= */

    public int getIdFacture() {
        return idFacture.get();
    }

    public LocalDateTime getDateFacture() {
        return dateFacture.get();
    }

    public double getTotal() {
        return total.get();
    }

    public int getIdCommande() {
        return idCommande.get();
    }

    /* ================= PROPERTIES ================= */

    public IntegerProperty idFactureProperty() {
        return idFacture;
    }

    public ObjectProperty<LocalDateTime> dateFactureProperty() {
        return dateFacture;
    }

    public DoubleProperty totalProperty() {
        return total;
    }

    public IntegerProperty idCommandeProperty() {
        return idCommande;
    }
}
