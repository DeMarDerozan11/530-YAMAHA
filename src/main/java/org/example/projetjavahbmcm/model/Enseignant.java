package org.example.projetjavahbmcm.model;

import java.util.List;

public class Enseignant extends Utilisateur {

    private String specialite;

    public Enseignant(int id, String nom, String prenom, String email, String telephone, String motDePasse, String specialite) {
        super(id, "Enseignant", nom, prenom, email, telephone, motDePasse);
        this.specialite = specialite;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public void consulterCours(List<Cours> coursList) {
        System.out.println("Cours enseignés par " + getNom() + " :");
        for (Cours cours : coursList) {
            System.out.println("- " + cours.getNom());
        }
    }

    public void envoyerNotification(String message) {
        System.out.println("Notification envoyée : " + message);
    }


    @Override
    public String toString() {
        return this.getPrenom() + " " + this.getNom();
    }

}
