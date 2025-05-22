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

    // Navigation buttons
    @FXML private Button btnOverview;
    @FXML private Button btnSchedules;
    @FXML private Button btnUsers;
    @FXML private Button btnRooms;
    @FXML private Button btnStats;

    // Content area
    @FXML private StackPane contentArea;
    @FXML private VBox overviewPanel;

    // Statistics labels
    @FXML private Label labelWelcome;
    @FXML private Label labelNbCours;
    @FXML private Label labelNbClasses;
    @FXML private Label labelNbEnseignants;
    @FXML private Label labelNbEtudiants;

    // Recent courses table
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

        // Set overview as active by default
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

            // Count courses
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM cours_emploi_temps");
            if (rs.next()) {
                labelNbCours.setText(String.valueOf(rs.getInt(1)));
            }

            // Count teachers
            rs = stmt.executeQuery("SELECT COUNT(*) FROM utilisateur WHERE type = 'enseignant'");
            if (rs.next()) {
                labelNbEnseignants.setText(String.valueOf(rs.getInt(1)));
            }

            // Count students
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

    // Navigation methods
    @FXML
    private void handleShowOverview() {
        setActiveButton(btnOverview);
        loadStatistics();
        loadRecentCourses();
        // The overview panel is already visible by default
    }

    @FXML
    private void handleShowSchedules() {
        setActiveButton(btnSchedules);
        // TODO: Load schedules panel
        showComingSoon("Gestion des emplois du temps");
    }

    @FXML
    private void handleShowUsers() {
        setActiveButton(btnUsers);
        // TODO: Load users panel
        showComingSoon("Gestion des utilisateurs");
    }

    @FXML
    private void handleShowRooms() {
        setActiveButton(btnRooms);
        // TODO: Load rooms panel
        showComingSoon("Gestion des salles");
    }

    @FXML
    private void handleShowStats() {
        setActiveButton(btnStats);
        // TODO: Load statistics panel
        showComingSoon("Statistiques avancées");
    }

    private void showComingSoon(String feature) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fonctionnalité à venir");
        alert.setHeaderText(feature);
        alert.setContentText("Cette fonctionnalité sera implémentée prochainement !");
        alert.showAndWait();
    }

    private void setActiveButton(Button button) {
        // Reset previous active button
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

    // Action methods
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

    // Method to refresh data
    public void refreshData() {
        loadStatistics();
        loadRecentCourses();
    }
}