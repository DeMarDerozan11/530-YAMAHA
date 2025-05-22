package org.example.projetjavahbmcm.model;

public class Salle {


    private int idSalle;
    private String nom;
    private int capacite;
    private boolean disponible;
    private String equipement;

    public Salle(int idSalle, String nom, int capacite, boolean disponible) {
        this.idSalle = idSalle;
        this.nom = nom;
        this.capacite = capacite;
        this.disponible = disponible;
    }

    public int getIdSalle() {
        return idSalle;
    }

    public void setIdSalle(int idSalle) {
        this.idSalle = idSalle;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public void verifierDisponibilite() {
        System.out.println("Salle " + nom + " disponible : " + disponible);
    }

    public void reserverSalle() {
        if (disponible) {
            disponible = false;
            System.out.println("Salle " + nom + " réservée.");
        } else {
            System.out.println("La salle " + nom + " n'est pas disponible.");
        }
    }

    @Override
    public String toString() {
        return this.getNom();
    }

    public String getEquipement() {
        return equipement;
    }

    public void setEquipement(String equipement) {
        this.equipement = equipement;
    }

}
