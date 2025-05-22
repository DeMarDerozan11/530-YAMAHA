// CoursEmploiDuTemps.java
package org.example.projetjavahbmcm.model;

public class CoursEmploiDuTemps {
    private int id;
    private String nomCours;
    private String enseignantNom;
    private String enseignantEmail;
    private String classe;
    private String salle;
    private CreneauHoraire creneau;
    private String couleur; // Pour la couleur d'affichage

    public CoursEmploiDuTemps(int id, String nomCours, String enseignantNom, String enseignantEmail,
                              String classe, String salle, CreneauHoraire creneau) {
        this.id = id;
        this.nomCours = nomCours;
        this.enseignantNom = enseignantNom;
        this.enseignantEmail = enseignantEmail;
        this.classe = classe;
        this.salle = salle;
        this.creneau = creneau;
        this.couleur = generateColor();
    }

    private String generateColor() {
        // Génère une couleur basée sur le nom du cours
        int hash = nomCours.hashCode();
        String[] colors = {"#FFB6C1", "#87CEEB", "#98FB98", "#DDA0DD", "#F0E68C",
                "#FFA07A", "#20B2AA", "#87CEFA", "#DEB887", "#F5DEB3"};
        return colors[Math.abs(hash) % colors.length];
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNomCours() { return nomCours; }
    public void setNomCours(String nomCours) { this.nomCours = nomCours; }

    public String getEnseignantNom() { return enseignantNom; }
    public void setEnseignantNom(String enseignantNom) { this.enseignantNom = enseignantNom; }

    public String getEnseignantEmail() { return enseignantEmail; }
    public void setEnseignantEmail(String enseignantEmail) { this.enseignantEmail = enseignantEmail; }

    public String getClasse() { return classe; }
    public void setClasse(String classe) { this.classe = classe; }

    public String getSalle() { return salle; }
    public void setSalle(String salle) { this.salle = salle; }

    public CreneauHoraire getCreneau() { return creneau; }
    public void setCreneau(CreneauHoraire creneau) { this.creneau = creneau; }

    public String getCouleur() { return couleur; }
    public void setCouleur(String couleur) { this.couleur = couleur; }

    @Override
    public String toString() {
        return nomCours + " - " + salle;
    }
}
