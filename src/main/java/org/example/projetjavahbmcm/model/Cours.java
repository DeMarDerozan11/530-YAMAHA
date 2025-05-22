package org.example.projetjavahbmcm.model;

public class Cours {

    private int idCours;
    private String nom;
    private String horaire;
    private Enseignant enseignant;
    private Salle salle;

    public Cours(int idCours, String nom, String horaire, Enseignant enseignant, Salle salle) {
        this.idCours = idCours;
        this.nom = nom;
        this.horaire = horaire;
        this.enseignant = enseignant;
        this.salle = salle;
    }

    public int getIdCours() {
        return idCours;
    }

    public void setIdCours(int idCours) {
        this.idCours = idCours;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getHoraire() {
        return horaire;
    }

    public void setHoraire(String horaire) {
        this.horaire = horaire;
    }

    public Enseignant getEnseignant() {
        return enseignant;
    }

    public void setEnseignant(Enseignant enseignant) {
        this.enseignant = enseignant;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

    public String obtenirDetails() {
        return "Cours: " + nom +
                ", Enseignant: " + (enseignant != null ? enseignant.getNom() : "Non défini") +
                ", Horaire: " + horaire +
                ", Salle: " + (salle != null ? salle.getNom() : "Non défini");
    }

    @Override
    public String toString() {
        return nom;
    }
}
