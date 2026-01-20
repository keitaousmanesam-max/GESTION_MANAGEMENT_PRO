package com.restaurant.model;

public class Stock {

    private int idStock;
    private String nomProduit;
    private int quantite;
    private int seuilMin;

    public Stock() {}

    public Stock(int idStock, String nomProduit, int quantite, int seuilMin) {
        this.idStock = idStock;
        this.nomProduit = nomProduit;
        this.quantite = quantite;
        this.seuilMin = seuilMin;
    }

    public int getIdStock() {
        return idStock;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public int getQuantite() {
        return quantite;
    }

    public int getSeuilMin() {
        return seuilMin;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}
