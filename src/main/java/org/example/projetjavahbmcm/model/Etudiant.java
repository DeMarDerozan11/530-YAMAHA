package org.example.projetjavahbmcm.model;

public class Etudiant extends Utilisateur {

    private String niveau;

    public Etudiant(int id, String nom, String prenom, String email, String telephone, String motDePasse, String niveau) {
        super(id, "Etudiant", nom, prenom, email, telephone, motDePasse);
        this.niveau = niveau;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public void recevoirNotification(String message) {
        System.out.println("Nouvelle notification pour " + getNom() + " : " + message);
    }

    @Override
    public String toString() {
        return super.toString() + " (Étudiant)";
    }
}
