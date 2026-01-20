package com.restaurant.model;

import javafx.beans.property.*;

public class Utilisateur {

    private IntegerProperty idUtilisateur = new SimpleIntegerProperty();
    private StringProperty nom = new SimpleStringProperty();
    private StringProperty prenom = new SimpleStringProperty();
    private StringProperty login = new SimpleStringProperty();
    private StringProperty motDePasse = new SimpleStringProperty();

    // 🔥 AJOUT IMPORTANT
    private StringProperty etatCompte = new SimpleStringProperty();

    private ObjectProperty<Role> role = new SimpleObjectProperty<>();

    // Constructeur vide
    public Utilisateur() {}

    // Constructeur complet (DAO)
    public Utilisateur(int id, String nom, String prenom,
                       String login, String etatCompte, Role role) {
        this.idUtilisateur.set(id);
        this.nom.set(nom);
        this.prenom.set(prenom);
        this.login.set(login);
        this.etatCompte.set(etatCompte);
        this.role.set(role);
    }

    /* ===== Getters ===== */

    public int getIdUtilisateur() { return idUtilisateur.get(); }
    public String getNom() { return nom.get(); }
    public String getPrenom() { return prenom.get(); }
    public String getLogin() { return login.get(); }
    public String getMotDePasse() { return motDePasse.get(); }
    public String getEtatCompte() { return etatCompte.get(); }
    public Role getRole() { return role.get(); }

    /* ===== Setters ===== */

    public void setIdUtilisateur(int id) { this.idUtilisateur.set(id); }
    public void setNom(String nom) { this.nom.set(nom); }
    public void setPrenom(String prenom) { this.prenom.set(prenom); }
    public void setLogin(String login) { this.login.set(login); }
    public void setMotDePasse(String mdp) { this.motDePasse.set(mdp); }
    public void setEtatCompte(String etat) { this.etatCompte.set(etat); }
    public void setRole(Role role) { this.role.set(role); }

    /* ===== JavaFX Properties ===== */

    public IntegerProperty idUtilisateurProperty() { return idUtilisateur; }
    public StringProperty nomProperty() { return nom; }
    public StringProperty prenomProperty() { return prenom; }
    public StringProperty etatCompteProperty() { return etatCompte; }

    public StringProperty roleNomProperty() {
        return new SimpleStringProperty(
                role.get() != null ? role.get().getNomRole() : ""
        );
    }
}
