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
import org.example.projetjavahbmcm.model.Cours;

import java.io.IOException;
import java.util.List;

public class StudentDashboardController {

    @FXML
    private GridPane gridEmploiDuTemps;

    // Méthode pour afficher les cours d'un étudiant
    public void afficherCours(List<Cours> coursList) {
        for (Cours cours : coursList) {
            StackPane cell = new StackPane();

            Rectangle fond = new Rectangle(100, 80);
            fond.setFill(Color.LIGHTCORAL);
            fond.setArcWidth(10);
            fond.setArcHeight(10);

            Text texte = new Text(cours.getNom() + "\n" + cours.getHoraire());

            cell.getChildren().addAll(fond, texte);

            int col = getColonneDepuisJour(cours.getHoraire());
            int row = getLigneDepuisHoraire(cours.getHoraire());

            gridEmploiDuTemps.add(cell, col, row);
        }
    }

    // Méthodes simples pour parser l’horaire (à adapter selon ton format)
    private int getColonneDepuisJour(String horaire) {
        if (horaire.contains("Lundi")) return 0;
        if (horaire.contains("Mardi")) return 1;
        if (horaire.contains("Mercredi")) return 2;
        if (horaire.contains("Jeudi")) return 3;
        if (horaire.contains("Vendredi")) return 4;
        return 0; // par défaut
    }

    private int getLigneDepuisHoraire(String horaire) {
        if (horaire.contains("9h")) return 0;
        if (horaire.contains("10h")) return 1;
        if (horaire.contains("11h")) return 2;
        if (horaire.contains("12h")) return 3;
        if (horaire.contains("13h")) return 4;
        if (horaire.contains("14h")) return 5;
        if (horaire.contains("15h")) return 6;
        if (horaire.contains("16h")) return 7;
        return 8; // par défaut
    }

    @FXML
    private void handleLogout() {
        System.out.println("Déconnexion en cours...");

        try {
            // Charger le fichier FXML du login
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/login.fxml"));
            Parent root = fxmlLoader.load();

            // Récupérer la fenêtre actuelle et remplacer la scène
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
}
