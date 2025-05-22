package org.example.projetjavahbmcm.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.projetjavahbmcm.database.DatabaseManager;
import org.example.projetjavahbmcm.model.Salle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomManagementController {

    @FXML private TextField fieldNomSalle;
    @FXML private Spinner<Integer> spinnerCapacite;
    @FXML private TextArea textAreaEquipement;
    @FXML private CheckBox checkBoxDisponible;

    @FXML private TableView<Salle> tableSalles;
    @FXML private TableColumn<Salle, Integer> colId;
    @FXML private TableColumn<Salle, String> colNom;
    @FXML private TableColumn<Salle, Integer> colCapacite;
    @FXML private TableColumn<Salle, String> colEquipement;
    @FXML private TableColumn<Salle, Boolean> colDisponible;

    @FXML private Label labelTauxOccupation;
    @FXML private Label labelSallesPlusUtilisees;

    private ObservableList<Salle> listeSalles = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 200, 30);
        spinnerCapacite.setValueFactory(valueFactory);

        colId.setCellValueFactory(new PropertyValueFactory<>("idSalle"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        colEquipement.setCellValueFactory(new PropertyValueFactory<>("equipement"));
        colDisponible.setCellValueFactory(new PropertyValueFactory<>("disponible"));

        colDisponible.setCellFactory(column -> new TableCell<Salle, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "✅ Disponible" : "❌ Occupée");
                    setStyle(item ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
                }
            }
        });

        tableSalles.setItems(listeSalles);

        tableSalles.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                afficherDetailsSalle(newSelection);
            }
        });

        chargerSalles();
        chargerStatistiques();
    }

    private void chargerSalles() {
        listeSalles.clear();
        String sql = "SELECT * FROM salle ORDER BY nom";

        try (Connection conn = DatabaseManager.getConnection();
             var stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Salle salle = new Salle(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("capacite"),
                        rs.getBoolean("disponible")
                );
                salle.setEquipement(rs.getString("equipement"));
                listeSalles.add(salle);
            }

        } catch (SQLException e) {
            afficherErreur("Erreur lors du chargement des salles : " + e.getMessage());
        }
    }

    private void chargerStatistiques() {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sqlTaux = """
                SELECT 
                    COUNT(DISTINCT c.salle) as salles_occupees,
                    (SELECT COUNT(*) FROM salle) as total_salles
                FROM cours_emploi_temps c
            """;

            try (var stmt = conn.prepareStatement(sqlTaux);
                 ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    int occupees = rs.getInt("salles_occupees");
                    int total = rs.getInt("total_salles");
                    double taux = total > 0 ? (occupees * 100.0 / total) : 0;
                    labelTauxOccupation.setText(String.format("Taux d'occupation : %.1f%%", taux));
                }
            }

            String sqlTop = """
                SELECT s.nom, COUNT(c.id) as nb_cours
                FROM salle s
                LEFT JOIN cours_emploi_temps c ON s.nom = c.salle
                GROUP BY s.nom
                ORDER BY nb_cours DESC
                LIMIT 3
            """;

            try (var stmt = conn.prepareStatement(sqlTop);
                 ResultSet rs = stmt.executeQuery()) {

                StringBuilder top = new StringBuilder("Top 3 : ");
                int position = 1;
                while (rs.next()) {
                    if (position > 1) top.append(", ");
                    top.append(position++).append(". ").append(rs.getString("nom"))
                            .append(" (").append(rs.getInt("nb_cours")).append(" cours)");
                }
                labelSallesPlusUtilisees.setText(top.toString());
            }

        } catch (SQLException e) {
            afficherErreur("Erreur lors du chargement des statistiques : " + e.getMessage());
        }
    }

    @FXML
    private void handleAjouterSalle() {
        String nom = fieldNomSalle.getText().trim();
        int capacite = spinnerCapacite.getValue();
        String equipement = textAreaEquipement.getText().trim();
        boolean disponible = checkBoxDisponible.isSelected();

        if (nom.isEmpty()) {
            afficherErreur("Le nom de la salle est obligatoire.");
            return;
        }

        String sql = "INSERT INTO salle (nom, capacite, disponible, equipement) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nom);
            pstmt.setInt(2, capacite);
            pstmt.setBoolean(3, disponible);
            pstmt.setString(4, equipement.isEmpty() ? null : equipement);

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                afficherSucces("Salle ajoutée avec succès !");
                clearFields();
                chargerSalles();
                chargerStatistiques();
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                afficherErreur("Une salle avec ce nom existe déjà.");
            } else {
                afficherErreur("Erreur lors de l'ajout : " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleModifierSalle() {
        Salle salleSelectionnee = tableSalles.getSelectionModel().getSelectedItem();
        if (salleSelectionnee == null) {
            afficherErreur("Veuillez sélectionner une salle à modifier.");
            return;
        }

        String nom = fieldNomSalle.getText().trim();
        int capacite = spinnerCapacite.getValue();
        String equipement = textAreaEquipement.getText().trim();
        boolean disponible = checkBoxDisponible.isSelected();

        if (nom.isEmpty()) {
            afficherErreur("Le nom de la salle est obligatoire.");
            return;
        }

        String sql = "UPDATE salle SET nom = ?, capacite = ?, disponible = ?, equipement = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nom);
            pstmt.setInt(2, capacite);
            pstmt.setBoolean(3, disponible);
            pstmt.setString(4, equipement.isEmpty() ? null : equipement);
            pstmt.setInt(5, salleSelectionnee.getIdSalle());

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                afficherSucces("Salle modifiée avec succès !");
                clearFields();
                chargerSalles();
                chargerStatistiques();
            }

        } catch (SQLException e) {
            afficherErreur("Erreur lors de la modification : " + e.getMessage());
        }
    }

    @FXML
    private void handleSupprimerSalle() {
        Salle salleSelectionnee = tableSalles.getSelectionModel().getSelectedItem();
        if (salleSelectionnee == null) {
            afficherErreur("Veuillez sélectionner une salle à supprimer.");
            return;
        }

        String checkSql = "SELECT COUNT(*) FROM cours_emploi_temps WHERE salle = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, salleSelectionnee.getNom());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                afficherErreur("Cette salle est utilisée dans " + rs.getInt(1) + " cours. Suppression impossible.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setHeaderText("Supprimer la salle " + salleSelectionnee.getNom() + " ?");
            confirm.setContentText("Cette action est irréversible.");

            if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                String deleteSql = "DELETE FROM salle WHERE id = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                    deleteStmt.setInt(1, salleSelectionnee.getIdSalle());
                    deleteStmt.executeUpdate();
                    afficherSucces("Salle supprimée avec succès !");
                    clearFields();
                    chargerSalles();
                    chargerStatistiques();
                }
            }

        } catch (SQLException e) {
            afficherErreur("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @FXML
    private void handleRechercherSallesLibres() {
        TextInputDialog dialog = new TextInputDialog("30");
        dialog.setTitle("Recherche de salles");
        dialog.setHeaderText("Rechercher des salles libres");
        dialog.setContentText("Capacité minimale requise :");

        dialog.showAndWait().ifPresent(capaciteStr -> {
            try {
                int capaciteMin = Integer.parseInt(capaciteStr);

                String sql = """
                    SELECT s.*, 
                        (SELECT COUNT(*) FROM cours_emploi_temps c WHERE c.salle = s.nom) as nb_cours
                    FROM salle s
                    WHERE s.disponible = TRUE AND s.capacite >= ?
                    ORDER BY nb_cours ASC, s.capacite ASC
                """;

                try (Connection conn = DatabaseManager.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, capaciteMin);
                    ResultSet rs = pstmt.executeQuery();

                    StringBuilder result = new StringBuilder("Salles disponibles (capacité >= " + capaciteMin + ") :\n\n");
                    boolean found = false;

                    while (rs.next()) {
                        found = true;
                        result.append("• ").append(rs.getString("nom"))
                                .append(" - Capacité: ").append(rs.getInt("capacite"))
                                .append(" - Utilisation: ").append(rs.getInt("nb_cours")).append(" cours")
                                .append("\n  Équipement: ").append(rs.getString("equipement") != null ? rs.getString("equipement") : "Aucun")
                                .append("\n\n");
                    }

                    if (!found) {
                        result.append("Aucune salle disponible avec cette capacité.");
                    }

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Résultats de recherche");
                    alert.setHeaderText("Salles libres trouvées");
                    alert.setContentText(result.toString());
                    alert.getDialogPane().setMinHeight(300);
                    alert.showAndWait();

                } catch (SQLException e) {
                    afficherErreur("Erreur lors de la recherche : " + e.getMessage());
                }

            } catch (NumberFormatException e) {
                afficherErreur("Veuillez entrer un nombre valide.");
            }
        });
    }

    private void afficherDetailsSalle(Salle salle) {
        fieldNomSalle.setText(salle.getNom());
        spinnerCapacite.getValueFactory().setValue(salle.getCapacite());
        textAreaEquipement.setText(salle.getEquipement() != null ? salle.getEquipement() : "");
        checkBoxDisponible.setSelected(salle.isDisponible());
    }

    private void clearFields() {
        fieldNomSalle.clear();
        spinnerCapacite.getValueFactory().setValue(30);
        textAreaEquipement.clear();
        checkBoxDisponible.setSelected(true);
        tableSalles.getSelectionModel().clearSelection();
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