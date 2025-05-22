package org.example.projetjavahbmcm.model;

public class Administrateur extends Utilisateur {


    private int idAdmin;

    public Administrateur(int id, String nom, String prenom, String email, String telephone, String motDePasse, int idAdmin) {
        super(id, "Administrateur", nom, prenom, email, telephone, motDePasse);
        this.idAdmin = idAdmin;
    }

    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }

    public void creerEmploiDuTemps(EmploiDuTemps emploi) {
        System.out.println("Création de l'emploi du temps : " + emploi);
    }

    public void modifierEmploiDuTemps(EmploiDuTemps emploi) {
        System.out.println("Modification de l'emploi du temps : " + emploi);
    }

    public void gererSalle(Salle salle) {
        System.out.println("Gestion de la salle : " + salle);
    }

    public void affecterEnseignant(Cours cours, Enseignant enseignant) {
        System.out.println("Affectation de l'enseignant " + enseignant.getNom() + " au cours " + cours.getNom());
    }

    public void genererStatistiques() {
        System.out.println("Génération des statistiques...");
    }

    @Override
    public String toString() {
        return super.toString() + " (Administrateur)";
    }
}
