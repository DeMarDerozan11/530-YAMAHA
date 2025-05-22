package org.example.projetjavahbmcm.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.projetjavahbmcm.database.DatabaseManager;
import org.example.projetjavahbmcm.model.CoursEmploiDuTemps;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class ModernDashboardController {

    @FXML private Button btnOverview;
    @FXML private Button btnSchedules;
    @FXML private Button btnUsers;
    @FXML private Button btnRooms;
    @FXML private Button btnStats;

    @FXML private StackPane contentArea;
    @FXML private VBox overviewPanel;

    @FXML private Label labelWelcome;
    @FXML private Label labelNbCours;
    @FXML private Label labelNbClasses;
    @FXML private Label labelNbEnseignants;
    @FXML private Label labelNbEtudiants;

    @FXML private TableView<CoursEmploiDuTemps> tableRecentCourses;
    @FXML private TableColumn<CoursEmploiDuTemps, String> colCours;
    @FXML private TableColumn<CoursEmploiDuTemps, String> colEnseignant;
    @FXML private TableColumn<CoursEmploiDuTemps, String> colClasse;
    @FXML private TableColumn<CoursEmploiDuTemps, String> colSalle;
    @FXML private TableColumn<CoursEmploiDuTemps, String> colJour;
    @FXML private TableColumn<CoursEmploiDuTemps, String> colHoraire;

    private Button currentActiveButton;

    @FXML
    public void initialize() {
        System.out.println("ModernDashboard initialisé !");

        setupTableColumns();
        loadStatistics();
        loadRecentCourses();

        currentActiveButton = btnOverview;
        setActiveButton(btnOverview);
    }

    private void setupTableColumns() {
        colCours.setCellValueFactory(new PropertyValueFactory<>("nomCours"));
        colEnseignant.setCellValueFactory(new PropertyValueFactory<>("enseignantNom"));
        colClasse.setCellValueFactory(new PropertyValueFactory<>("classe"));
        colSalle.setCellValueFactory(new PropertyValueFactory<>("salle"));
        colJour.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCreneau().getJourSemaine()));
        colHoraire.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCreneau().getPlageHoraire()));
    }

    private void loadStatistics() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM cours_emploi_temps");
            if (rs.next()) {
                labelNbCours.setText(String.valueOf(rs.getInt(1)));
            }

            rs = stmt.executeQuery("SELECT COUNT(*) FROM utilisateur WHERE type = 'enseignant'");
            if (rs.next()) {
                labelNbEnseignants.setText(String.valueOf(rs.getInt(1)));
            }

            rs = stmt.executeQuery("SELECT COUNT(*) FROM utilisateur WHERE type = 'etudiant'");
            if (rs.next()) {
                labelNbEtudiants.setText(String.valueOf(rs.getInt(1)));
            }

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des statistiques : " + e.getMessage());
        }
    }

    private void loadRecentCourses() {
        try {
            List<String> classes = DatabaseManager.getToutesLesClasses();
            ObservableList<CoursEmploiDuTemps> allCourses = FXCollections.observableArrayList();

            for (String classe : classes) {
                List<CoursEmploiDuTemps> coursClasse = DatabaseManager.getEmploiDuTempsParClasse(classe);
                allCourses.addAll(coursClasse);
            }

            tableRecentCourses.setItems(allCourses);

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des cours récents : " + e.getMessage());
        }
    }

    @FXML
    private void handleShowOverview() {
        setActiveButton(btnOverview);
        loadStatistics();
        loadRecentCourses();
    }

    @FXML
    private void handleShowSchedules() {
        setActiveButton(btnSchedules);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/SearchFilter.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Recherche et filtres - Emplois du temps");
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors de l'ouverture de la recherche.");
        }
    }

    @FXML
    private void handleShowUsers() {
        setActiveButton(btnUsers);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/AddUserForm.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestion des utilisateurs");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors de l'ouverture de la gestion des utilisateurs.");
        }
    }

    @FXML
    private void handleShowRooms() {
        setActiveButton(btnRooms);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/RoomManagement.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestion des salles");
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors de l'ouverture de la gestion des salles.");
        }
    }

    @FXML
    private void handleShowStats() {
        setActiveButton(btnStats);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/Statistics.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Statistiques et analyses");
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors de l'ouverture des statistiques.");
        }
    }

    private void showComingSoon(String feature) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fonctionnalité à venir");
        alert.setHeaderText(feature);
        alert.setContentText("Cette fonctionnalité sera implémentée prochainement !");
        alert.showAndWait();
    }

    private void setActiveButton(Button button) {
        if (currentActiveButton != null) {
            currentActiveButton.setStyle(currentActiveButton.getStyle().replace(
                    "-fx-background-color: #3b82f6; -fx-text-fill: white;",
                    "-fx-background-color: transparent; -fx-text-fill: #374151;"
            ));
        }

        // Set new active button
        button.setStyle(button.getStyle().replace(
                "-fx-background-color: transparent; -fx-text-fill: #374151;",
                "-fx-background-color: #3b82f6; -fx-text-fill: white;"
        ));

        currentActiveButton = button;
    }

    @FXML
    private void handleCreateCourse() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/ScheduleCreator.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Créateur d'Emploi du Temps");
            stage.setScene(new Scene(root, 800, 700));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors de l'ouverture du créateur de cours.");
        }
    }

    @FXML
    private void handleAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/AddUserForm.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un utilisateur");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors de l'ouverture du formulaire d'ajout d'utilisateur.");
        }
    }

    @FXML
    private void handleViewAllSchedules() {
        handleCreateCourse(); // For now, redirect to course creator
    }

    @FXML
    private void handleViewCalendar() {
        // TODO: Implement calendar view
        showComingSoon("Vue calendrier globale");
    }

    @FXML
    private void handleExportData() {
        try {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            org.example.projetjavahbmcm.util.ExportSystem.exporterToutesLesClassesCSV(stage);
        } catch (Exception e) {
            showError("Erreur lors de l'export : " + e.getMessage());
        }
    }

    @FXML
    private void handleViewNotifications() {
        org.example.projetjavahbmcm.util.NotificationSystem.afficherNotifications("admin@gmail.com");
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/login.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion - Gestion des Emplois du Temps");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors de la déconnexion.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void refreshData() {
        loadStatistics();
        loadRecentCourses();
    }


}