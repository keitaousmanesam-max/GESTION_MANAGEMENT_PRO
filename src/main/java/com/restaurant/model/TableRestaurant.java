package com.restaurant.model;

import javafx.beans.property.*;

public class TableRestaurant {

    private IntegerProperty idTable = new SimpleIntegerProperty();
    private IntegerProperty numeroTable = new SimpleIntegerProperty();
    private StringProperty etatTable = new SimpleStringProperty();

    public TableRestaurant() {}

    public TableRestaurant(int id, int numero, String etat) {
        this.idTable.set(id);
        this.numeroTable.set(numero);
        this.etatTable.set(etat);
    }

    // ===== GETTERS =====
    public int getIdTable() { return idTable.get(); }
    public int getNumeroTable() { return numeroTable.get(); }
    public String getEtatTable() { return etatTable.get(); }

    // ===== SETTERS =====
    public void setIdTable(int id) { this.idTable.set(id); }
    public void setNumeroTable(int numero) { this.numeroTable.set(numero); }
    public void setEtatTable(String etat) { this.etatTable.set(etat); }

    // ===== PROPERTIES (pour TableView) =====
    public IntegerProperty idTableProperty() { return idTable; }
    public IntegerProperty numeroTableProperty() { return numeroTable; }
    public StringProperty etatTableProperty() { return etatTable; }
}
