package org.example.projetjavahbmcm.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.projetjavahbmcm.database.DatabaseManager;
import org.example.projetjavahbmcm.util.NotificationSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AnomalyReportController {

    @FXML private ComboBox<String> comboTypeAnomalie;
    @FXML private ComboBox<String> comboPriorite;
    @FXML private TextArea textAreaDescription;
    @FXML private TextField fieldCoursConcerne;

    @FXML private TableView<Anomalie> tableAnomalies;
    @FXML private TableColumn<Anomalie, String> colDate;
    @FXML private TableColumn<Anomalie, String> colType;
    @FXML private TableColumn<Anomalie, String> colPriorite;
    @FXML private TableColumn<Anomalie, String> colStatut;
    @FXML private TableColumn<Anomalie, String> colDescription;

    private String emailUtilisateur;
    private ObservableList<Anomalie> listeAnomalies = FXCollections.observableArrayList();

    public static class Anomalie {
        private int id;
        private String date;
        private String type;
        private String priorite;
        private String statut;
        private String description;
        private String coursConcerne;
        private String rapporteur;

        public Anomalie(int id, String date, String type, String priorite, String statut,
                        String description, String coursConcerne, String rapporteur) {
            this.id = id;
            this.date = date;
            this.type = type;
            this.priorite = priorite;
            this.statut = statut;
            this.description = description;
            this.coursConcerne = coursConcerne;
            this.rapporteur = rapporteur;
        }

        public int getId() { return id; }
        public String getDate() { return date; }
        public String getType() { return type; }
        public String getPriorite() { return priorite; }
        public String getStatut() { return statut; }
        public String getDescription() { return description; }
        public String getCoursConcerne() { return coursConcerne; }
        public String getRapporteur() { return rapporteur; }
    }

    @FXML
    public void initialize() {
        initTableAnomalies();

        creerTableAnomalies();

        comboPriorite.getSelectionModel().select(2);
    }

    private void initTableAnomalies() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colPriorite.setCellValueFactory(new PropertyValueFactory<>("priorite"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        colStatut.setCellFactory(column -> new TableCell<Anomalie, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "En attente":
                            setStyle("-fx-text-fill: orange;");
                            break;
                        case "En cours":
                            setStyle("-fx-text-fill: blue;");
                            break;
                        case "Résolu":
                            setStyle("-fx-text-fill: green;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });

        tableAnomalies.setItems(listeAnomalies);
    }

    private void creerTableAnomalies() {
        String sql = """
            CREATE TABLE IF NOT EXISTS anomalies (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date_creation TEXT NOT NULL,
                type TEXT NOT NULL,
                priorite TEXT NOT NULL,
                statut TEXT DEFAULT 'En attente',
                description TEXT NOT NULL,
                cours_concerne TEXT,
                rapporteur_email TEXT NOT NULL,
                FOREIGN KEY (rapporteur_email) REFERENCES utilisateur(email)
            );
        """;

        try (Connection conn = DatabaseManager.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Erreur création table anomalies : " + e.getMessage());
        }
    }

    public void setUtilisateurConnecte(String email) {
        this.emailUtilisateur = email;
        chargerMesAnomalies();
    }

    private void chargerMesAnomalies() {
        listeAnomalies.clear();
        String sql = "SELECT * FROM anomalies WHERE rapporteur_email = ? ORDER BY date_creation DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, emailUtilisateur);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Anomalie anomalie = new Anomalie(
                        rs.getInt("id"),
                        rs.getString("date_creation"),
                        rs.getString("type"),
                        rs.getString("priorite"),
                        rs.getString("statut"),
                        rs.getString("description"),
                        rs.getString("cours_concerne"),
                        rs.getString("rapporteur_email")
                );
                listeAnomalies.add(anomalie);
            }

        } catch (SQLException e) {
            System.err.println("Erreur chargement anomalies : " + e.getMessage());
        }
    }

    @FXML
    private void handleEnvoyerAnomalie() {
        String type = comboTypeAnomalie.getValue();
        String priorite = comboPriorite.getValue();
        String description = textAreaDescription.getText().trim();
        String coursConcerne = fieldCoursConcerne.getText().trim();

        // Validation
        if (type == null || priorite == null || description.isEmpty()) {
            afficherErreur("Veuillez remplir tous les champs obligatoires.");
            return;
        }

        String sql = """
            INSERT INTO anomalies (date_creation, type, priorite, description, cours_concerne, rapporteur_email)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            pstmt.setString(2, type);
            pstmt.setString(3, priorite);
            pstmt.setString(4, description);
            pstmt.setString(5, coursConcerne.isEmpty() ? null : coursConcerne);
            pstmt.setString(6, emailUtilisateur);

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                String messagePriorite = priorite.contains("Urgente") ? "🔴 URGENT : " : "";
                String messageNotif = messagePriorite + "Nouvelle anomalie signalée - " + type + " : " +
                        description.substring(0, Math.min(description.length(), 50)) + "...";

                notifierAdmins(messageNotif);

                afficherSucces("Anomalie signalée avec succès ! Les administrateurs ont été notifiés.");
                clearFields();
                chargerMesAnomalies();
            }

        } catch (SQLException e) {
            afficherErreur("Erreur lors de l'envoi : " + e.getMessage());
        }
    }

    private void notifierAdmins(String message) {
        String sql = "SELECT email FROM utilisateur WHERE type = 'admin'";

        try (Connection conn = DatabaseManager.getConnection();
             var stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                NotificationSystem.envoyerNotification(rs.getString("email"), message, "WARNING");
            }

        } catch (SQLException e) {
            System.err.println("Erreur notification admins : " + e.getMessage());
        }
    }

    @FXML
    private void handleAnnuler() {
        clearFields();
    }

    private void clearFields() {
        comboTypeAnomalie.getSelectionModel().clearSelection();
        comboPriorite.getSelectionModel().select(2); // Moyenne par défaut
        textAreaDescription.clear();
        fieldCoursConcerne.clear();
    }

    private void afficherSucces(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
