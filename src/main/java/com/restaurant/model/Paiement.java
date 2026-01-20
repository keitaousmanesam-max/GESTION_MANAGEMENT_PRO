package com.restaurant.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Paiement {

    /* ================= PROPERTIES ================= */
    private final IntegerProperty idPaiement = new SimpleIntegerProperty();
    private final IntegerProperty idFacture = new SimpleIntegerProperty();
    private final StringProperty modePaiement = new SimpleStringProperty();
    private final DoubleProperty montant = new SimpleDoubleProperty();
    private final ObjectProperty<LocalDateTime> datePaiement = new SimpleObjectProperty<>();

    /* ================= CONSTRUCTEURS ================= */
    public Paiement() {}

    public Paiement(int idPaiement,
                    int idFacture,
                    String modePaiement,
                    double montant,
                    LocalDateTime datePaiement) {

        this.idPaiement.set(idPaiement);
        this.idFacture.set(idFacture);
        this.modePaiement.set(modePaiement);
        this.montant.set(montant);
        this.datePaiement.set(datePaiement);
    }

    /* ================= GETTERS ================= */
    public int getIdPaiement() {
        return idPaiement.get();
    }

    public int getIdFacture() {
        return idFacture.get();
    }

    public String getModePaiement() {
        return modePaiement.get();
    }

    public double getMontant() {
        return montant.get();
    }

    public LocalDateTime getDatePaiement() {
        return datePaiement.get();
    }

    /* ================= SETTERS ================= */
    public void setIdPaiement(int idPaiement) {
        this.idPaiement.set(idPaiement);
    }

    public void setIdFacture(int idFacture) {
        this.idFacture.set(idFacture);
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement.set(modePaiement);
    }

    public void setMontant(double montant) {
        this.montant.set(montant);
    }

    public void setDatePaiement(LocalDateTime datePaiement) {
        this.datePaiement.set(datePaiement);
    }

    /* ================= PROPERTIES (JavaFX) ================= */
    public IntegerProperty idPaiementProperty() {
        return idPaiement;
    }

    public IntegerProperty idFactureProperty() {
        return idFacture;
    }

    public StringProperty modePaiementProperty() {
        return modePaiement;
    }

    public DoubleProperty montantProperty() {
        return montant;
    }

    public ObjectProperty<LocalDateTime> datePaiementProperty() {
        return datePaiement;
    }

    /* ================= MÉTHODES UTILES ================= */

    /** Vérifie si le paiement est en espèces */
    public boolean estEspeces() {
        return "ESPECES".equalsIgnoreCase(getModePaiement());
    }

    /** Vérifie si le paiement est par carte */
    public boolean estCarte() {
        return "CARTE".equalsIgnoreCase(getModePaiement());
    }

    @Override
    public String toString() {
        return "Paiement #" + getIdPaiement() +
                " | Mode=" + getModePaiement() +
                " | Montant=" + getMontant() +
                " | Date=" + getDatePaiement();
    }
}
