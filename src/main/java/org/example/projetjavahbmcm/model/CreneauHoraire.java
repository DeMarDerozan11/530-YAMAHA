
package org.example.projetjavahbmcm.model;


public class CreneauHoraire {
    private int id;
    private String jourSemaine;
    private String heureDebut;
    private String heureFin;
    private int numeroSlot;

    public CreneauHoraire(int id, String jourSemaine, String heureDebut, String heureFin, int numeroSlot) {
        this.id = id;
        this.jourSemaine = jourSemaine;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.numeroSlot = numeroSlot;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getJourSemaine() { return jourSemaine; }
    public void setJourSemaine(String jourSemaine) { this.jourSemaine = jourSemaine; }

    public String getHeureDebut() { return heureDebut; }
    public void setHeureDebut(String heureDebut) { this.heureDebut = heureDebut; }

    public String getHeureFin() { return heureFin; }
    public void setHeureFin(String heureFin) { this.heureFin = heureFin; }

    public int getNumeroSlot() { return numeroSlot; }
    public void setNumeroSlot(int numeroSlot) { this.numeroSlot = numeroSlot; }

    public String getPlageHoraire() {
        return heureDebut + " - " + heureFin;
    }

    @Override
    public String toString() {
        return jourSemaine + " " + heureDebut + "-" + heureFin;
    }
}


