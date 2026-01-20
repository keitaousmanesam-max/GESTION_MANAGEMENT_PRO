package com.restaurant.model;

import javafx.beans.property.*;

public class Journal {

    private IntegerProperty idJournal = new SimpleIntegerProperty();
    private StringProperty utilisateur = new SimpleStringProperty();
    private StringProperty role = new SimpleStringProperty();
    private StringProperty action = new SimpleStringProperty();
    private StringProperty date = new SimpleStringProperty();

    /* =========================
       CONSTRUCTEUR
       ========================= */
    public Journal(int id, String utilisateur,
                   String role, String action, String date) {

        this.idJournal.set(id);
        this.utilisateur.set(utilisateur);
        this.role.set(role);
        this.action.set(action);
        this.date.set(date);
    }

    /* =========================
       GETTERS CLASSIQUES (IMPORTANT)
       ========================= */
    public int getIdJournal() {
        return idJournal.get();
    }

    public String getUtilisateur() {
        return utilisateur.get();
    }

    public String getRole() {
        return role.get();
    }

    public String getAction() {
        return action.get();
    }

    public String getDate() {
        return date.get();
    }

    /* =========================
       PROPERTIES JAVAFX
       ========================= */
    public IntegerProperty idJournalProperty() {
        return idJournal;
    }

    public StringProperty utilisateurProperty() {
        return utilisateur;
    }

    public StringProperty roleProperty() {
        return role;
    }

    public StringProperty actionProperty() {
        return action;
    }

    public StringProperty dateProperty() {
        return date;
    }
}
