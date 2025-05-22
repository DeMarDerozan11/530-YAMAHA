package org.example.projetjavahbmcm.controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.projetjavahbmcm.database.DatabaseManager;
import org.example.projetjavahbmcm.model.CoursEmploiDuTemps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleViewController {

    @FXML private Label labelTitre;
    @FXML private GridPane gridEmploiDuTemps;

    private String classeActuelle = null;
    private String enseignantActuel = null;

    public void afficherEmploiDuTempsClasse(String classe) {
        this.classeActuelle = classe;
        this.enseignantActuel = null;
        labelTitre.setText("Emploi du temps - Classe " + classe);
        chargerEmploiDuTemps();
    }

    public void afficherEmploiDuTempsEnseignant(String enseignantEmail, String nomEnseignant) {
        this.enseignantActuel = enseignantEmail;
        this.classeActuelle = null;
        labelTitre.setText("Emploi du temps - " + nomEnseignant);
        chargerEmploiDuTemps();
    }

    private void chargerEmploiDuTemps() {

        nettoyerGrille();

        List<CoursEmploiDuTemps> cours;

        if (classeActuelle != null) {
            cours = DatabaseManager.getEmploiDuTempsParClasse(classeActuelle);
        } else if (enseignantActuel != null) {
            cours = DatabaseManager.getEmploiDuTempsParEnseignant(enseignantActuel);
        } else {
            return;
        }

        Map<String, Integer> joursColonnes = new HashMap<>();
        joursColonnes.put("LUNDI", 1);
        joursColonnes.put("MARDI", 2);
        joursColonnes.put("MERCREDI", 3);
        joursColonnes.put("JEUDI", 4);
        joursColonnes.put("VENDREDI", 5);

        Map<String, Integer> creneauxLignes = new HashMap<>();
        creneauxLignes.put("08:30", 1);
        creneauxLignes.put("10:45", 2);
        creneauxLignes.put("13:45", 4);
        creneauxLignes.put("16:00", 5);

        for (CoursEmploiDuTemps coursEdt : cours) {
            String jour = coursEdt.getCreneau().getJourSemaine();
            String heureDebut = coursEdt.getCreneau().getHeureDebut();

            Integer colonne = joursColonnes.get(jour);
            Integer ligne = creneauxLignes.get(heureDebut);

            if (colonne != null && ligne != null) {
                StackPane celluleCours = creerCelluleCours(coursEdt);
                gridEmploiDuTemps.add(celluleCours, colonne, ligne);
            }
        }

        ajouterLignePauseDejeuer();
    }

    private void ajouterLignePauseDejeuer() {
        for (int col = 1; col <= 5; col++) {
            Label pauseLabel = new Label();
            pauseLabel.setPrefWidth(180);
            pauseLabel.setPrefHeight(20);
            pauseLabel.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #e0e0e0; -fx-border-width: 0.5px;");
            gridEmploiDuTemps.add(pauseLabel, col, 3);
        }
    }

    private StackPane creerCelluleCours(CoursEmploiDuTemps cours) {
        StackPane cellule = new StackPane();
        cellule.setAlignment(Pos.CENTER);
        cellule.setStyle(
                "-fx-background-color: " + cours.getCouleur() + ";" +
                        "-fx-border-color: #333333;" +
                        "-fx-border-width: 1px;" +
                        "-fx-background-radius: 5px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-padding: 5px;"
        );

        VBox contenu = new VBox(2);
        contenu.setAlignment(Pos.CENTER);

        Label labelCours = new Label(cours.getNomCours());
        labelCours.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #333333;");
        labelCours.setWrapText(true);

        Label labelSalle = new Label("Salle: " + cours.getSalle());
        labelSalle.setStyle("-fx-font-size: 10px; -fx-text-fill: #555555;");

        Label labelInfo;
        if (classeActuelle != null) {
            labelInfo = new Label(cours.getEnseignantNom());
            labelInfo.setStyle("-fx-font-size: 10px; -fx-text-fill: #555555;");
        } else {
            labelInfo = new Label("Classe: " + cours.getClasse());
            labelInfo.setStyle("-fx-font-size: 10px; -fx-text-fill: #555555;");
        }

        Label labelHoraire = new Label(cours.getCreneau().getPlageHoraire());
        labelHoraire.setStyle("-fx-font-size: 9px; -fx-text-fill: #666666;");

        contenu.getChildren().addAll(labelCours, labelSalle, labelInfo, labelHoraire);
        cellule.getChildren().add(contenu);

        cellule.setOnMouseEntered(e -> cellule.setStyle(cellule.getStyle() + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 0);"));
        cellule.setOnMouseExited(e -> cellule.setStyle(cellule.getStyle().replace("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 0);", "")));

        return cellule;
    }

    private void nettoyerGrille() {
        gridEmploiDuTemps.getChildren().removeIf(node -> {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);

            if (colIndex == null) colIndex = 0;
            if (rowIndex == null) rowIndex = 0;

            return !(rowIndex == 0 || colIndex == 0 || rowIndex == 3);
        });
    }

    @FXML
    private void handleActualiser() {
        chargerEmploiDuTemps();
    }

    @FXML
    private void handleFermer() {
        Stage stage = (Stage) gridEmploiDuTemps.getScene().getWindow();
        stage.close();
    }
}