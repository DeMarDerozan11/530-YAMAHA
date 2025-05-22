package org.example.projetjavahbmcm.model;
import java.util.ArrayList;
import java.util.List;

public class EmploiDuTemps {


    private int idEmploiDuTemps;
    private String periode;
    private List<Cours> listeCours;

    public EmploiDuTemps(int idEmploiDuTemps, String periode) {
        this.idEmploiDuTemps = idEmploiDuTemps;
        this.periode = periode;
        this.listeCours = new ArrayList<>();
    }

    public int getIdEmploiDuTemps() {
        return idEmploiDuTemps;
    }

    public void setIdEmploiDuTemps(int idEmploiDuTemps) {
        this.idEmploiDuTemps = idEmploiDuTemps;
    }

    public String getPeriode() {
        return periode;
    }

    public void setPeriode(String periode) {
        this.periode = periode;
    }

    public List<Cours> getListeCours() {
        return listeCours;
    }

    public void ajouterCours(Cours cours) {
        listeCours.add(cours);
        System.out.println("Cours ajouté : " + cours.getNom());
    }

    public void supprimerCours(Cours cours) {
        listeCours.remove(cours);
        System.out.println("Cours supprimé : " + cours.getNom());
    }

    public void afficherEmploiDuTemps() {
        System.out.println("Emploi du Temps pour la période : " + periode);
        for (Cours cours : listeCours) {
            System.out.println("- " + cours.obtenirDetails());
        }
    }

    @Override
    public String toString() {
        return "EmploiDuTemps{" +
                "idEmploiDuTemps=" + idEmploiDuTemps +
                ", periode='" + periode + '\'' +
                ", listeCours=" + listeCours.size() +
                '}';
    }
}
