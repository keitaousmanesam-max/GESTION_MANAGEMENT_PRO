package com.restaurant.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

public class Menu {

    private final IntegerProperty idMenu = new SimpleIntegerProperty();
    private final StringProperty nomMenu = new SimpleStringProperty();
    private final StringProperty etatMenu = new SimpleStringProperty();

    public Menu() {
    }

    public Menu(int id, String nom, String etat) {
        this.idMenu.set(id);
        this.nomMenu.set(nom);
        this.etatMenu.set(etat);
    }

    /* ================= GETTERS ================= */

    public int getIdMenu() {
        return idMenu.get();
    }

    public String getNomMenu() {
        return nomMenu.get();
    }

    public String getEtatMenu() {
        return etatMenu.get();
    }

    /* ================= SETTERS ================= */

    public void setIdMenu(int id) {
        this.idMenu.set(id);
    }

    public void setNomMenu(String nom) {
        this.nomMenu.set(nom);
    }

    public void setEtatMenu(String etat) {
        this.etatMenu.set(etat);
    }

    /* ================= PROPERTIES ================= */

    public IntegerProperty idMenuProperty() {
        return idMenu;
    }

    public StringProperty nomMenuProperty() {
        return nomMenu;
    }

    public StringProperty etatMenuProperty() {
        return etatMenu;
    }

    /* ================= COMBOBOX ================= */

    @Override
    public String toString() {
        return nomMenu.get();
    }

    /* ================= IMPORTANT =================
       🔥 ÉGALITÉ LOGIQUE (OBLIGATOIRE POUR JavaFX)
       ============================================= */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Menu)) return false;
        Menu menu = (Menu) o;
        return getIdMenu() == menu.getIdMenu();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdMenu());
    }
}
