package com.restaurant.model;

import javafx.beans.property.*;

public class CommandePlat {

    // ===== ATTRIBUTS =====
    private IntegerProperty idCommande = new SimpleIntegerProperty();
    private IntegerProperty idPlat = new SimpleIntegerProperty();

    // 🔥 NOUVEAU : nom du plat (pour affichage dans la TableView)
    private StringProperty nomPlat = new SimpleStringProperty();

    private IntegerProperty quantite = new SimpleIntegerProperty();

    // ===== CONSTRUCTEURS =====

    // Constructeur vide (obligatoire pour JavaFX)
    public CommandePlat() {}

    // 🔥 CONSTRUCTEUR COMPLET (AVEC NOM DU PLAT)
    public CommandePlat(int idCommande, int idPlat, String nomPlat, int quantite) {
        this.idCommande.set(idCommande);
        this.idPlat.set(idPlat);
        this.nomPlat.set(nomPlat);
        this.quantite.set(quantite);
    }

    // Ancien constructeur (compatibilité si utilisé ailleurs)
    public CommandePlat(int idCommande, int idPlat, int quantite) {
        this.idCommande.set(idCommande);
        this.idPlat.set(idPlat);
        this.nomPlat.set(""); // valeur vide par défaut
        this.quantite.set(quantite);
    }

    // ===== GETTERS =====
    public int getIdCommande() {
        return idCommande.get();
    }

    public int getIdPlat() {
        return idPlat.get();
    }

    public String getNomPlat() {
        return nomPlat.get();
    }

    public int getQuantite() {
        return quantite.get();
    }

    // ===== SETTERS =====
    public void setIdCommande(int idCommande) {
        this.idCommande.set(idCommande);
    }

    public void setIdPlat(int idPlat) {
        this.idPlat.set(idPlat);
    }

    public void setNomPlat(String nomPlat) {
        this.nomPlat.set(nomPlat);
    }

    public void setQuantite(int quantite) {
        this.quantite.set(quantite);
    }

    // ===== PROPERTIES (POUR TABLEVIEW) =====
    public IntegerProperty idCommandeProperty() {
        return idCommande;
    }

    public IntegerProperty idPlatProperty() {
        return idPlat;
    }

    // 🔥 PROPERTY POUR AFFICHER LE NOM DU PLAT
    public StringProperty nomPlatProperty() {
        return nomPlat;
    }

    public IntegerProperty quantiteProperty() {
        return quantite;
    }
}
