package com.restaurant.model;

import javafx.beans.property.*;

public class Plat {

    private IntegerProperty idPlat = new SimpleIntegerProperty();
    private StringProperty nom = new SimpleStringProperty();
    private StringProperty categorie = new SimpleStringProperty();
    private DoubleProperty prix = new SimpleDoubleProperty();
    private StringProperty disponibilite = new SimpleStringProperty(); // "DISPONIBLE" / "INDISPONIBLE"
    private IntegerProperty idMenu = new SimpleIntegerProperty();

    public Plat() {}

    public Plat(int id, String nom, String categorie, double prix, String disponibilite, int idMenu) {
        this.idPlat.set(id);
        this.nom.set(nom);
        this.categorie.set(categorie);
        this.prix.set(prix);
        this.disponibilite.set(disponibilite);
        this.idMenu.set(idMenu);
    }

    // ===== GETTERS =====
    public int getIdPlat() { return idPlat.get(); }
    public String getNom() { return nom.get(); }
    public String getCategorie() { return categorie.get(); }
    public double getPrix() { return prix.get(); }
    public String getDisponibilite() { return disponibilite.get(); }
    public int getIdMenu() { return idMenu.get(); }

    // ===== SETTERS =====
    public void setIdPlat(int id) { this.idPlat.set(id); }
    public void setNom(String nom) { this.nom.set(nom); }
    public void setCategorie(String categorie) { this.categorie.set(categorie); }
    public void setPrix(double prix) { this.prix.set(prix); }
    public void setDisponibilite(String dispo) { this.disponibilite.set(dispo); }
    public void setIdMenu(int idMenu) { this.idMenu.set(idMenu); }

    // ===== PROPERTIES (pour TableView) =====
    public IntegerProperty idPlatProperty() { return idPlat; }
    public StringProperty nomProperty() { return nom; }
    public StringProperty categorieProperty() { return categorie; }
    public DoubleProperty prixProperty() { return prix; }
    public StringProperty disponibiliteProperty() { return disponibilite; }
    public IntegerProperty idMenuProperty() { return idMenu; }
}
