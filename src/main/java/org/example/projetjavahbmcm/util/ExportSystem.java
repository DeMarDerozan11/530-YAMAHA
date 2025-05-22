package org.example.projetjavahbmcm.util;

import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.projetjavahbmcm.database.DatabaseManager;
import org.example.projetjavahbmcm.model.CoursEmploiDuTemps;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportSystem {

    // Export en CSV
    public static void exporterEmploiDuTempsCSV(String classe, Stage parentStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter l'emploi du temps en CSV");
        fileChooser.setInitialFileName("EmploiDuTemps_" + classe + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv")
        );

        File file = fileChooser.showSaveDialog(parentStage);
        if (file != null) {
            try {
                exporterCSV(classe, file);
                afficherSucces("Export réussi", "L'emploi du temps a été exporté en CSV avec succès !");
            } catch (IOException e) {
                afficherErreur("Erreur d'export", "Impossible d'exporter le fichier : " + e.getMessage());
            }
        }
    }

    private static void exporterCSV(String classe, File file) throws IOException {
        List<CoursEmploiDuTemps> cours = DatabaseManager.getEmploiDuTempsParClasse(classe);

        try (FileWriter writer = new FileWriter(file)) {
            // En-têtes CSV
            writer.append("Classe,Cours,Enseignant,Jour,Heure_Debut,Heure_Fin,Salle\n");

            // Données
            for (CoursEmploiDuTemps coursEdt : cours) {
                writer.append(classe).append(",")
                        .append(coursEdt.getNomCours()).append(",")
                        .append(coursEdt.getEnseignantNom()).append(",")
                        .append(coursEdt.getCreneau().getJourSemaine()).append(",")
                        .append(coursEdt.getCreneau().getHeureDebut()).append(",")
                        .append(coursEdt.getCreneau().getHeureFin()).append(",")
                        .append(coursEdt.getSalle()).append("\n");
            }
        }
    }

    // Export de toutes les classes en CSV
    public static void exporterToutesLesClassesCSV(Stage parentStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter tous les emplois du temps en CSV");
        fileChooser.setInitialFileName("EmploisDuTemps_Complet_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv")
        );

        File file = fileChooser.showSaveDialog(parentStage);
        if (file != null) {
            try {
                exporterToutesClassesCSV(file);
                afficherSucces("Export réussi", "Tous les emplois du temps ont été exportés en CSV avec succès !");
            } catch (IOException e) {
                afficherErreur("Erreur d'export", "Impossible d'exporter le fichier : " + e.getMessage());
            }
        }
    }

    private static void exporterToutesClassesCSV(File file) throws IOException {
        List<String> classes = DatabaseManager.getToutesLesClasses();

        try (FileWriter writer = new FileWriter(file)) {
            // En-têtes CSV
            writer.append("Classe,Cours,Enseignant,Jour,Heure_Debut,Heure_Fin,Salle\n");

            // Pour chaque classe
            for (String classe : classes) {
                List<CoursEmploiDuTemps> cours = DatabaseManager.getEmploiDuTempsParClasse(classe);

                for (CoursEmploiDuTemps coursEdt : cours) {
                    writer.append(classe).append(",")
                            .append(coursEdt.getNomCours()).append(",")
                            .append(coursEdt.getEnseignantNom()).append(",")
                            .append(coursEdt.getCreneau().getJourSemaine()).append(",")
                            .append(coursEdt.getCreneau().getHeureDebut()).append(",")
                            .append(coursEdt.getCreneau().getHeureFin()).append(",")
                            .append(coursEdt.getSalle()).append("\n");
                }
            }
        }
    }

    // Impression directe (comme PDF)
    public static void imprimerEmploiDuTemps(Node nodeToprint, String titre) {
        Printer printer = Printer.getDefaultPrinter();
        if (printer == null) {
            afficherErreur("Erreur d'impression", "Aucune imprimante disponible.");
            return;
        }

        PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT);

        PrinterJob job = PrinterJob.createPrinterJob(printer);
        if (job != null && job.showPrintDialog(nodeToprint.getScene().getWindow())) {
            boolean success = job.printPage(pageLayout, nodeToprint);
            if (success) {
                job.endJob();
                afficherSucces("Impression", "L'emploi du temps a été envoyé à l'imprimante !");
            } else {
                afficherErreur("Erreur d'impression", "Impossible d'imprimer l'emploi du temps.");
            }
        }
    }

    // Export des statistiques en CSV
    public static void exporterStatistiquesCSV(Stage parentStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les statistiques en CSV");
        fileChooser.setInitialFileName("Statistiques_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv")
        );

        File file = fileChooser.showSaveDialog(parentStage);
        if (file != null) {
            try {
                exporterStatistiques(file);
                afficherSucces("Export réussi", "Les statistiques ont été exportées en CSV avec succès !");
            } catch (IOException e) {
                afficherErreur("Erreur d'export", "Impossible d'exporter les statistiques : " + e.getMessage());
            }
        }
    }

    private static void exporterStatistiques(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.append("Type,Statistique,Valeur\n");

            // Statistiques générales
            try (var conn = DatabaseManager.getConnection();
                 var stmt = conn.createStatement()) {

                var rs = stmt.executeQuery("SELECT COUNT(*) FROM cours_emploi_temps");
                if (rs.next()) {
                    writer.append("General,Nombre de cours,").append(String.valueOf(rs.getInt(1))).append("\n");
                }

                rs = stmt.executeQuery("SELECT COUNT(*) FROM utilisateur WHERE type = 'enseignant'");
                if (rs.next()) {
                    writer.append("General,Nombre d'enseignants,").append(String.valueOf(rs.getInt(1))).append("\n");
                }

                rs = stmt.executeQuery("SELECT COUNT(*) FROM utilisateur WHERE type = 'etudiant'");
                if (rs.next()) {
                    writer.append("General,Nombre d'étudiants,").append(String.valueOf(rs.getInt(1))).append("\n");
                }

                rs = stmt.executeQuery("SELECT COUNT(DISTINCT classe) FROM utilisateur WHERE type = 'etudiant' AND classe IS NOT NULL");
                if (rs.next()) {
                    writer.append("General,Nombre de classes,").append(String.valueOf(rs.getInt(1))).append("\n");
                }

                // Statistiques par classe
                rs = stmt.executeQuery("SELECT classe, COUNT(*) as nb_cours FROM cours_emploi_temps GROUP BY classe");
                while (rs.next()) {
                    writer.append("Classe,Cours pour ").append(rs.getString("classe")).append(",")
                            .append(String.valueOf(rs.getInt("nb_cours"))).append("\n");
                }

            } catch (Exception e) {
                throw new IOException("Erreur lors de la récupération des statistiques", e);
            }
        }
    }

    private static void afficherSucces(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
}