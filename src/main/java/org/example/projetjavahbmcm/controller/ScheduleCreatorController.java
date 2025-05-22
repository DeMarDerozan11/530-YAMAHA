package org.example.projetjavahbmcm.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.projetjavahbmcm.database.DatabaseManager;
import org.example.projetjavahbmcm.model.CoursEmploiDuTemps;
import org.example.projetjavahbmcm.model.CreneauHoraire;

import java.io.IOException;
import java.util.List;

public class ScheduleCreatorController {

    @FXML private TextField fieldNomCours;
    @FXML private ComboBox<String> comboEnseignant;
    @FXML private ComboBox<String> comboClasse;
    @FXML private ComboBox<String> comboSalle;
    @FXML private ComboBox<String> comboJour;
    @FXML private ComboBox<String> comboCreneau;

    @FXML private TableView<CoursEmploiDuTemps> tableEmploiDuTemps;
    @FXML private TableColumn<CoursEmploiDuTemps, String> colCours;
    @FXML private TableColumn<CoursEmploiDuTemps, String> colEnseignant;
    @FXML private TableColumn<CoursEmploiDuTemps, String> colClasse;
    @FXML private TableColumn<CoursEmploiDuTemps, String> colSalle;
    @FXML private TableColumn<CoursEmploiDuTemps, String> colJour;
    @FXML private TableColumn<CoursEmploiDuTemps, String> colHoraire;

    private final ObservableList<CoursEmploiDuTemps> listeCours = FXCollections.observableArrayList();
    private List<CreneauHoraire> tousLesCreneaux;

    @FXML
    public void initialize() {
        setupTableColumns();
        chargerDonnees();
        setupComboBoxListeners();
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

        tableEmploiDuTemps.setItems(listeCours);
    }

    private void setupComboBoxListeners() {
        comboJour.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                chargerCreneauxPourJour(newVal);
            }
        });
    }

    private void chargerDonnees() {
        List<String> enseignants = DatabaseManager.getTousLesEnseignants();
        comboEnseignant.setItems(FXCollections.observableArrayList(enseignants));

        List<String> classes = DatabaseManager.getToutesLesClasses();
        comboClasse.setItems(FXCollections.observableArrayList(classes));

        List<String> salles = DatabaseManager.getToutesLesSalles();
        comboSalle.setItems(FXCollections.observableArrayList(salles));

        tousLesCreneaux = DatabaseManager.getTousLesCreneaux();

        chargerCoursExistants();
    }

    private void chargerCreneauxPourJour(String jour) {
        ObservableList<String> creneauxJour = FXCollections.observableArrayList();

        for (CreneauHoraire creneau : tousLesCreneaux) {
            if (creneau.getJourSemaine().equals(jour)) {
                creneauxJour.add(creneau.getPlageHoraire() + " (ID: " + creneau.getId() + ")");
            }
        }

        comboCreneau.setItems(creneauxJour);
    }

    private void chargerCoursExistants() {
        listeCours.clear();

        List<String> classes = DatabaseManager.getToutesLesClasses();
        for (String classe : classes) {
            List<CoursEmploiDuTemps> coursClasse = DatabaseManager.getEmploiDuTempsParClasse(classe);
            listeCours.addAll(coursClasse);
        }
    }

    @FXML
    private void handleAjouterCours() {
        if (!validerChamps()) {
            return;
        }

        String nomCours = fieldNomCours.getText().trim();
        String enseignantComplet = comboEnseignant.getValue();
        String enseignantEmail = enseignantComplet.split(" - ")[0]; // Extraire l'email
        String classe = comboClasse.getValue();
        String salle = comboSalle.getValue();
        String creneauComplet = comboCreneau.getValue();

        int creneauId = Integer.parseInt(creneauComplet.substring(creneauComplet.lastIndexOf("(ID: ") + 5, creneauComplet.lastIndexOf(")")));

        boolean success = DatabaseManager.ajouterCoursEmploiDuTemps(nomCours, enseignantEmail, classe, salle, creneauId);

        if (success) {
            afficherAlerte("Succès", "Cours ajouté avec succès à l'emploi du temps !", Alert.AlertType.INFORMATION);
            clearFields();
            chargerCoursExistants(); // Recharger le tableau
        } else {
            afficherAlerte("Erreur", "Impossible d'ajouter le cours. Vérifiez les conflits d'horaire.", Alert.AlertType.ERROR);
        }
    }

    private boolean validerChamps() {
        if (fieldNomCours.getText().trim().isEmpty()) {
            afficherAlerte("Erreur", "Le nom du cours est obligatoire.", Alert.AlertType.ERROR);
            return false;
        }

        if (comboEnseignant.getValue() == null) {
            afficherAlerte("Erreur", "Veuillez sélectionner un enseignant.", Alert.AlertType.ERROR);
            return false;
        }

        if (comboClasse.getValue() == null) {
            afficherAlerte("Erreur", "Veuillez sélectionner une classe.", Alert.AlertType.ERROR);
            return false;
        }

        if (comboSalle.getValue() == null) {
            afficherAlerte("Erreur", "Veuillez sélectionner une salle.", Alert.AlertType.ERROR);
            return false;
        }

        if (comboJour.getValue() == null) {
            afficherAlerte("Erreur", "Veuillez sélectionner un jour.", Alert.AlertType.ERROR);
            return false;
        }

        if (comboCreneau.getValue() == null) {
            afficherAlerte("Erreur", "Veuillez sélectionner un créneau horaire.", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void clearFields() {
        fieldNomCours.clear();
        comboEnseignant.getSelectionModel().clearSelection();
        comboClasse.getSelectionModel().clearSelection();
        comboSalle.getSelectionModel().clearSelection();
        comboJour.getSelectionModel().clearSelection();
        comboCreneau.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleSupprimerCours() {
        CoursEmploiDuTemps coursSelectionne = tableEmploiDuTemps.getSelectionModel().getSelectedItem();

        if (coursSelectionne == null) {
            afficherAlerte("Erreur", "Veuillez sélectionner un cours à supprimer.", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer le cours \"" + coursSelectionne.getNomCours() + "\" ?",
                ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait();

        if (confirmation.getResult() == ButtonType.YES) {
            // TODO: Implémenter la suppression en base de données
            listeCours.remove(coursSelectionne);
            afficherAlerte("Succès", "Cours supprimé avec succès.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleVoirEmploiDuTemps() {
        String classeSelectionnee = comboClasse.getValue();
        if (classeSelectionnee == null) {
            afficherAlerte("Erreur", "Veuillez sélectionner une classe pour voir son emploi du temps.", Alert.AlertType.ERROR);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/ScheduleView.fxml"));
            Parent root = loader.load();

            ScheduleViewController controller = loader.getController();
            controller.afficherEmploiDuTempsClasse(classeSelectionnee);

            Stage stage = new Stage();
            stage.setTitle("Emploi du temps - " + classeSelectionnee);
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Erreur lors de l'ouverture de l'emploi du temps.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleActualiser() {
        chargerCoursExistants();
        afficherAlerte("Information", "Données actualisées.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/Dashboard.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = (Stage) fieldNomCours.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tableau de Bord - Admin");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Erreur lors du retour au tableau de bord.", Alert.AlertType.ERROR);
        }
    }

    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}