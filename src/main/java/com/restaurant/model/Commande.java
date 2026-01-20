package com.restaurant.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Commande {

    private IntegerProperty idCommande = new SimpleIntegerProperty();
    private ObjectProperty<LocalDateTime> dateCommande = new SimpleObjectProperty<>();
    private StringProperty etatCommande = new SimpleStringProperty();
    private IntegerProperty idTable = new SimpleIntegerProperty();
    private IntegerProperty idServeur = new SimpleIntegerProperty();

    public Commande() {}

    public Commande(int id, LocalDateTime date, String etat, int idTable, int idServeur) {
        this.idCommande.set(id);
        this.dateCommande.set(date);
        this.etatCommande.set(etat);
        this.idTable.set(idTable);
        this.idServeur.set(idServeur);
    }

    // ===== GETTERS =====
    public int getIdCommande() { return idCommande.get(); }
    public LocalDateTime getDateCommande() { return dateCommande.get(); }
    public String getEtatCommande() { return etatCommande.get(); }
    public int getIdTable() { return idTable.get(); }
    public int getIdServeur() { return idServeur.get(); }

    // ===== SETTERS =====
    public void setIdCommande(int id) { this.idCommande.set(id); }
    public void setDateCommande(LocalDateTime date) { this.dateCommande.set(date); }
    public void setEtatCommande(String etat) { this.etatCommande.set(etat); }
    public void setIdTable(int idTable) { this.idTable.set(idTable); }
    public void setIdServeur(int idServeur) { this.idServeur.set(idServeur); }

    // ===== PROPERTIES =====
    public IntegerProperty idCommandeProperty() { return idCommande; }
    public ObjectProperty<LocalDateTime> dateCommandeProperty() { return dateCommande; }
    public StringProperty etatCommandeProperty() { return etatCommande; }
    public IntegerProperty idTableProperty() { return idTable; }
    public IntegerProperty idServeurProperty() { return idServeur; }
}
