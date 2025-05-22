package org.example.projetjavahbmcm.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.projetjavahbmcm.database.DatabaseManager;
import org.example.projetjavahbmcm.model.CoursEmploiDuTemps;

import java.io.IOException;
import java.util.List;

public class StudentDashboardController {

    @FXML
    private GridPane gridEmploiDuTemps;

    private String emailUtilisateur;
    private String classeUtilisateur;

    @FXML
    public void initialize() {
        System.out.println("StudentDashboardController initialisé !");
        setupGrid();
    }

    private void setupGrid() {
        // Ajouter les en-têtes à la grille
        String[] jours = {"", "LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI"};
        String[] heures = {"08:30-10:30", "10:45-12:45", "13:45-15:45", "16:00-18:00"};

        // En-têtes des jours
        for (int i = 0; i < jours.length; i++) {
            Label label = new Label(jours[i]);
            label.setStyle("-fx-background-color: #e3f2fd; -fx-alignment: center; -fx-font-weight: bold; -fx-padding: 5px;");
            gridEmploiDuTemps.add(label, i, 0);
        }

        // En-têtes des heures
        for (int i = 0; i < heures.length; i++) {
            Label label = new Label(heures[i]);
            label.setStyle("-fx-background-color: #f5f5f5; -fx-alignment: center; -fx-font-size: 11px; -fx-padding: 5px;");
            gridEmploiDuTemps.add(label, 0, i + 1);
        }
    }

    public void setUtilisateurConnecte(String email) {
        this.emailUtilisateur = email;
        this.classeUtilisateur = getClasseEtudiant(email);
        System.out.println("Étudiant connecté : " + email + ", Classe : " + classeUtilisateur);
        chargerEmploiDuTemps();
    }

    private String getClasseEtudiant(String email) {
        String sql = "SELECT classe FROM utilisateur WHERE email = ? AND type = 'etudiant'";
        try (var conn = DatabaseManager.getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("classe");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de la classe : " + e.getMessage());
        }
        return null;
    }

    private void chargerEmploiDuTemps() {
        if (classeUtilisateur != null) {
            List<CoursEmploiDuTemps> cours = DatabaseManager.getEmploiDuTempsParClasse(classeUtilisateur);
            System.out.println("Cours trouvés pour " + classeUtilisateur + " : " + cours.size());
            afficherCours(cours);
        } else {
            System.out.println("Aucune classe trouvée pour cet étudiant");
        }
    }

    public void afficherCours(List<CoursEmploiDuTemps> coursList) {
        for (CoursEmploiDuTemps cours : coursList) {
            StackPane cell = new StackPane();

            Rectangle fond = new Rectangle(110, 50);
            fond.setFill(Color.web(cours.getCouleur()));
            fond.setArcWidth(5);
            fond.setArcHeight(5);

            Text texte = new Text(cours.getNomCours() + "\n" + cours.getSalle());
            texte.setWrappingWidth(100);
            texte.setStyle("-fx-font-size: 10px; -fx-text-alignment: center;");

            cell.getChildren().addAll(fond, texte);

            int col = getColonneDepuisJour(cours.getCreneau().getJourSemaine());
            int row = getLigneDepuisHoraire(cours.getCreneau().getHeureDebut());

            if (col > 0 && row > 0) {
                gridEmploiDuTemps.add(cell, col, row);
            }
        }
    }

    private int getColonneDepuisJour(String jour) {
        switch (jour) {
            case "LUNDI": return 1;
            case "MARDI": return 2;
            case "MERCREDI": return 3;
            case "JEUDI": return 4;
            case "VENDREDI": return 5;
            default: return 0;
        }
    }

    private int getLigneDepuisHoraire(String heure) {
        switch (heure) {
            case "08:30": return 1;
            case "10:45": return 2;
            case "13:45": return 3;
            case "16:00": return 4;
            default: return 1;
        }
    }

    @FXML
    private void handleLogout() {
        System.out.println("Déconnexion étudiant...");

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/login.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = (Stage) gridEmploiDuTemps.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion - Gestion des Emplois du Temps");
            stage.show();

            System.out.println("Retour à l'écran de connexion réussi !");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du retour à l'écran de connexion.");
        }
    }

    @FXML
    private void handleVoirEmploiDuTempsGraphique() {
        if (classeUtilisateur == null) {
            System.out.println("Erreur : Aucune classe définie pour cet étudiant");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/ScheduleView.fxml"));
            Parent root = loader.load();

            ScheduleViewController controller = loader.getController();
            controller.afficherEmploiDuTempsClasse(classeUtilisateur);

            Stage stage = new Stage();
            stage.setTitle("Mon Emploi du Temps - " + classeUtilisateur);
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'ouverture de l'emploi du temps graphique");
        }
    }
}