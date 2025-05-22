package org.example.projetjavahbmcm.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.projetjavahbmcm.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StatisticsController {

    @FXML private PieChart pieChartCoursParClasse;
    @FXML private BarChart<String, Number> barChartHeuresEnseignant;
    @FXML private LineChart<String, Number> lineChartOccupationSalles;
    @FXML private VBox vboxTopEnseignants;
    @FXML private Label labelTotalCours;
    @FXML private Label labelTotalHeures;
    @FXML private Label labelTauxRemplissage;

    @FXML
    public void initialize() {
        chargerStatistiques();
    }

    private void chargerStatistiques() {
        chargerStatsGenerales();
        chargerRepartitionCoursParClasse();
        chargerHeuresParEnseignant();
        chargerTauxOccupationSalles();
        chargerTopEnseignants();
    }

    private void chargerStatsGenerales() {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sqlCours = "SELECT COUNT(*) as total FROM cours_emploi_temps";
            try (var stmt = conn.prepareStatement(sqlCours);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    labelTotalCours.setText("Total des cours : " + rs.getInt("total"));
                }
            }

            String sqlHeures = "SELECT COUNT(*) * 2 as total_heures FROM cours_emploi_temps";
            try (var stmt = conn.prepareStatement(sqlHeures);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    labelTotalHeures.setText("Total heures dispensées : " + rs.getInt("total_heures") + "h");
                }
            }

            String sqlTaux = """
                SELECT AVG(taux) as taux_moyen FROM (
                    SELECT classe, 
                        (COUNT(*) * 100.0 / 20) as taux  -- 20 créneaux max par semaine
                    FROM cours_emploi_temps 
                    GROUP BY classe
                )
            """;
            try (var stmt = conn.prepareStatement(sqlTaux);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double taux = rs.getDouble("taux_moyen");
                    labelTauxRemplissage.setText(String.format("Taux de remplissage moyen : %.1f%%", taux));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur stats générales : " + e.getMessage());
        }
    }

    private void chargerRepartitionCoursParClasse() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        String sql = """
            SELECT classe, COUNT(*) as nb_cours 
            FROM cours_emploi_temps 
            GROUP BY classe 
            ORDER BY nb_cours DESC
        """;

        try (Connection conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String classe = rs.getString("classe");
                int nbCours = rs.getInt("nb_cours");
                pieChartData.add(new PieChart.Data(classe + " (" + nbCours + " cours)", nbCours));
            }

            pieChartCoursParClasse.setData(pieChartData);
            pieChartCoursParClasse.setTitle("Répartition des cours par classe");

        } catch (SQLException e) {
            System.err.println("Erreur pie chart : " + e.getMessage());
        }
    }

    private void chargerHeuresParEnseignant() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Heures par enseignant");

        String sql = """
            SELECT u.nom || ' ' || u.prenom as enseignant, 
                   COUNT(c.id) * 2 as heures 
            FROM utilisateur u
            LEFT JOIN cours_emploi_temps c ON u.email = c.enseignant_email
            WHERE u.type = 'enseignant'
            GROUP BY u.email, u.nom, u.prenom
            ORDER BY heures DESC
            LIMIT 10
        """;

        try (Connection conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String enseignant = rs.getString("enseignant");
                int heures = rs.getInt("heures");
                series.getData().add(new XYChart.Data<>(enseignant, heures));
            }

            barChartHeuresEnseignant.getData().clear();
            barChartHeuresEnseignant.getData().add(series);
            barChartHeuresEnseignant.setTitle("Heures par enseignant (Top 10)");

            barChartHeuresEnseignant.getXAxis().setTickLabelRotation(45);

        } catch (SQLException e) {
            System.err.println("Erreur bar chart : " + e.getMessage());
        }
    }

    private void chargerTauxOccupationSalles() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Taux d'occupation");

        String sql = """
            SELECT s.nom as salle,
                   COUNT(c.id) * 100.0 / 20 as taux_occupation
            FROM salle s
            LEFT JOIN cours_emploi_temps c ON s.nom = c.salle
            GROUP BY s.nom
            ORDER BY s.nom
        """;

        try (Connection conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String salle = rs.getString("salle");
                double taux = rs.getDouble("taux_occupation");
                series.getData().add(new XYChart.Data<>(salle, taux));
            }

            lineChartOccupationSalles.getData().clear();
            lineChartOccupationSalles.getData().add(series);
            lineChartOccupationSalles.setTitle("Taux d'occupation des salles (%)");
            lineChartOccupationSalles.setCreateSymbols(true);

        } catch (SQLException e) {
            System.err.println("Erreur line chart : " + e.getMessage());
        }
    }

    private void chargerTopEnseignants() {
        vboxTopEnseignants.getChildren().clear();

        String sql = """
            SELECT u.nom || ' ' || u.prenom as enseignant,
                   COUNT(DISTINCT c.classe) as nb_classes,
                   COUNT(c.id) as nb_cours,
                   COUNT(c.id) * 2 as heures
            FROM utilisateur u
            JOIN cours_emploi_temps c ON u.email = c.enseignant_email
            WHERE u.type = 'enseignant'
            GROUP BY u.email, u.nom, u.prenom
            ORDER BY nb_cours DESC
            LIMIT 5
        """;

        try (Connection conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            int position = 1;
            while (rs.next()) {
                String enseignant = rs.getString("enseignant");
                int nbClasses = rs.getInt("nb_classes");
                int nbCours = rs.getInt("nb_cours");
                int heures = rs.getInt("heures");

                Label label = new Label(String.format("%d. %s - %d cours, %d classes, %dh/semaine",
                        position++, enseignant, nbCours, nbClasses, heures));
                label.setStyle("-fx-font-size: 12px; -fx-padding: 5px;");

                vboxTopEnseignants.getChildren().add(label);
            }

        } catch (SQLException e) {
            System.err.println("Erreur top enseignants : " + e.getMessage());
        }
    }

    @FXML
    private void handleActualiser() {
        chargerStatistiques();
    }

    @FXML
    private void handleExporterStats() {
        javafx.stage.Stage stage = (javafx.stage.Stage) pieChartCoursParClasse.getScene().getWindow();
        org.example.projetjavahbmcm.util.ExportSystem.exporterStatistiquesCSV(stage);
    }
}