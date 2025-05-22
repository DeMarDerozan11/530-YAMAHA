package org.example.projetjavahbmcm.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.projetjavahbmcm.database.DatabaseManager;
import org.example.projetjavahbmcm.model.CoursEmploiDuTemps;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchFilterController {

    @FXML private TextField fieldRecherche;
    @FXML private ComboBox<String> comboClasse;
    @FXML private ComboBox<String> comboEnseignant;
    @FXML private ComboBox<String> comboJour;
    @FXML private ComboBox<String> comboSalle;
    @FXML private ComboBox<String> comboPeriode;

    @FXML private TableView<CoursEmploiDuTemps> tableResultats;
    @FXML private TableColumn<CoursEmploiDuTemps, String> colCours;
    @FXML private TableColumn<CoursEmploiDuTemps, String> colEnseignant;
    @FXML private TableColumn<CoursEmploiDuTemps, String> colClasse;
    @FXML private TableColumn<CoursEmploiDuTemps, String> colSalle;
    @FXML private TableColumn<CoursEmploiDuTemps, String> colJour;
    @FXML private TableColumn<CoursEmploiDuTemps, String> colHoraire;

    @FXML private Label labelResultats;
    @FXML private Label labelCreneauxLibres;

    private ObservableList<CoursEmploiDuTemps> allCours = FXCollections.observableArrayList();
    private FilteredList<CoursEmploiDuTemps> filteredCours;

    @FXML
    public void initialize() {
        setupTableColumns();
        chargerDonneesComboBox();
        chargerTousLesCours();

        filteredCours = new FilteredList<>(allCours, p -> true);
        tableResultats.setItems(filteredCours);

        setupFilterListeners();

        updateResultCount();
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

    private void chargerDonneesComboBox() {
        List<String> classes = DatabaseManager.getToutesLesClasses();
        comboClasse.getItems().add("Toutes les classes");
        comboClasse.getItems().addAll(classes);
        comboClasse.getSelectionModel().selectFirst();

        List<String> enseignants = DatabaseManager.getTousLesEnseignants();
        comboEnseignant.getItems().add("Tous les enseignants");
        comboEnseignant.getItems().addAll(enseignants);
        comboEnseignant.getSelectionModel().selectFirst();

        comboJour.getItems().addAll("Tous les jours", "LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI");
        comboJour.getSelectionModel().selectFirst();

        List<String> salles = DatabaseManager.getToutesLesSalles();
        comboSalle.getItems().add("Toutes les salles");
        comboSalle.getItems().addAll(salles);
        comboSalle.getSelectionModel().selectFirst();

        comboPeriode.getItems().addAll("Toute la journée", "Matin (8h30-12h45)", "Après-midi (13h45-18h00)");
        comboPeriode.getSelectionModel().selectFirst();
    }

    private void chargerTousLesCours() {
        allCours.clear();
        List<String> classes = DatabaseManager.getToutesLesClasses();

        for (String classe : classes) {
            List<CoursEmploiDuTemps> coursClasse = DatabaseManager.getEmploiDuTempsParClasse(classe);
            allCours.addAll(coursClasse);
        }
    }

    private void setupFilterListeners() {
        fieldRecherche.textProperty().addListener((observable, oldValue, newValue) -> appliquerFiltres());

        comboClasse.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> appliquerFiltres());
        comboEnseignant.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> appliquerFiltres());
        comboJour.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> appliquerFiltres());
        comboSalle.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> appliquerFiltres());
        comboPeriode.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> appliquerFiltres());
    }

    private void appliquerFiltres() {
        filteredCours.setPredicate(cours -> {
            String searchText = fieldRecherche.getText().toLowerCase();
            if (!searchText.isEmpty()) {
                boolean matchesSearch = cours.getNomCours().toLowerCase().contains(searchText) ||
                        cours.getEnseignantNom().toLowerCase().contains(searchText) ||
                        cours.getSalle().toLowerCase().contains(searchText);
                if (!matchesSearch) return false;
            }

            String selectedClasse = comboClasse.getValue();
            if (selectedClasse != null && !selectedClasse.equals("Toutes les classes")) {
                if (!cours.getClasse().equals(selectedClasse)) return false;
            }

            String selectedEnseignant = comboEnseignant.getValue();
            if (selectedEnseignant != null && !selectedEnseignant.equals("Tous les enseignants")) {
                String enseignantEmail = selectedEnseignant.split(" - ")[0];
                if (!cours.getEnseignantEmail().equals(enseignantEmail)) return false;
            }

            String selectedJour = comboJour.getValue();
            if (selectedJour != null && !selectedJour.equals("Tous les jours")) {
                if (!cours.getCreneau().getJourSemaine().equals(selectedJour)) return false;
            }

            String selectedSalle = comboSalle.getValue();
            if (selectedSalle != null && !selectedSalle.equals("Toutes les salles")) {
                if (!cours.getSalle().equals(selectedSalle)) return false;
            }

            String selectedPeriode = comboPeriode.getValue();
            if (selectedPeriode != null && !selectedPeriode.equals("Toute la journée")) {
                String heureDebut = cours.getCreneau().getHeureDebut();
                boolean isMatin = heureDebut.equals("08:30") || heureDebut.equals("10:45");
                boolean isApresMidi = heureDebut.equals("13:45") || heureDebut.equals("16:00");

                if (selectedPeriode.contains("Matin") && !isMatin) return false;
                if (selectedPeriode.contains("Après-midi") && !isApresMidi) return false;
            }

            return true;
        });

        updateResultCount();
        rechercherCreneauxLibres();
    }

    private void updateResultCount() {
        int count = filteredCours.size();
        labelResultats.setText(String.format("%d cours trouvé(s)", count));
    }

    @FXML
    private void handleReinitialiserFiltres() {
        fieldRecherche.clear();
        comboClasse.getSelectionModel().selectFirst();
        comboEnseignant.getSelectionModel().selectFirst();
        comboJour.getSelectionModel().selectFirst();
        comboSalle.getSelectionModel().selectFirst();
        comboPeriode.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleRechercherCreneauxLibres() {
        rechercherCreneauxLibres();
    }

    private void rechercherCreneauxLibres() {
        String selectedClasse = comboClasse.getValue();
        String selectedSalle = comboSalle.getValue();
        String selectedJour = comboJour.getValue();

        if ((selectedClasse == null || selectedClasse.equals("Toutes les classes")) &&
                (selectedSalle == null || selectedSalle.equals("Toutes les salles"))) {
            labelCreneauxLibres.setText("Sélectionnez une classe ou une salle pour voir les créneaux libres");
            return;
        }

        List<String> creneauxLibres = new ArrayList<>();

        String[] jours = {"LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI"};
        String[] creneaux = {"08:30-10:30", "10:45-12:45", "13:45-15:45", "16:00-18:00"};

        for (String jour : jours) {
            if (selectedJour != null && !selectedJour.equals("Tous les jours") && !jour.equals(selectedJour)) {
                continue;
            }

            for (String creneau : creneaux) {
                boolean isLibre = true;

                for (CoursEmploiDuTemps cours : allCours) {
                    if (cours.getCreneau().getJourSemaine().equals(jour) &&
                            cours.getCreneau().getPlageHoraire().equals(creneau)) {

                        if (selectedClasse != null && !selectedClasse.equals("Toutes les classes") &&
                                cours.getClasse().equals(selectedClasse)) {
                            isLibre = false;
                            break;
                        }

                        if (selectedSalle != null && !selectedSalle.equals("Toutes les salles") &&
                                cours.getSalle().equals(selectedSalle)) {
                            isLibre = false;
                            break;
                        }
                    }
                }

                if (isLibre) {
                    creneauxLibres.add(jour + " " + creneau);
                }
            }
        }

        if (creneauxLibres.isEmpty()) {
            labelCreneauxLibres.setText("Aucun créneau libre trouvé");
        } else {
            labelCreneauxLibres.setText("Créneaux libres : " + String.join(", ", creneauxLibres));
        }
    }

    @FXML
    private void handleExporterResultats() {
        if (filteredCours.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setContentText("Aucun résultat à exporter.");
            alert.showAndWait();
            return;
        }

        javafx.stage.Stage stage = (javafx.stage.Stage) tableResultats.getScene().getWindow();

        StringBuilder filename = new StringBuilder("Emploi_du_temps_filtre");

        if (!comboClasse.getValue().equals("Toutes les classes")) {
            filename.append("_").append(comboClasse.getValue());
        }
        if (!comboJour.getValue().equals("Tous les jours")) {
            filename.append("_").append(comboJour.getValue());
        }

        // TODO: Adapter ExportSystem pour exporter une liste filtrée
        org.example.projetjavahbmcm.util.ExportSystem.exporterToutesLesClassesCSV(stage);
    }
}