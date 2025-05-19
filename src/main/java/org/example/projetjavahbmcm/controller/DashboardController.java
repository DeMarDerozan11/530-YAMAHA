package org.example.projetjavahbmcm.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.projetjavahbmcm.model.Cours;
import org.example.projetjavahbmcm.model.Enseignant;
import org.example.projetjavahbmcm.model.Salle;



public class DashboardController {

    @FXML
    private TableView<Cours> tableEmploiDuTemps;

    @FXML
    private TableColumn<Cours, String> colCours;

    @FXML
    private TableColumn<Cours, String> colEnseignant;

    @FXML
    private TableColumn<Cours, String> colHoraire;

    @FXML
    private TableColumn<Cours, String> colSalle;

    // Liste observable pour le tableau
    private final ObservableList<Cours> listeCours = FXCollections.observableArrayList();

    @FXML
    private TextField fieldCours;

    @FXML
    private TextField fieldEnseignant;

    @FXML
    private TextField fieldHoraire;

    @FXML
    private TextField fieldSalle;


    @FXML
    public void initialize() {
        // Associer les colonnes aux attributs de la classe Cours
        colCours.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colEnseignant.setCellValueFactory(new PropertyValueFactory<>("enseignant"));
        colHoraire.setCellValueFactory(new PropertyValueFactory<>("horaire"));
        colSalle.setCellValueFactory(new PropertyValueFactory<>("salle"));

        // Charger les données de test
        chargerDonnees();
    }

    @FXML
    private void handleRefresh() {
        System.out.println("Actualiser les données...");
        listeCours.clear();
        chargerDonnees();
    }

    // Méthode pour charger les données
    private void chargerDonnees() {
        // Exemples de données
        Enseignant enseignant1 = new Enseignant(1, "Martin", "Paul", "paul.martin@gmail.com", "0654321987", "prof123", "Informatique");
        Salle salle1 = new Salle(1, "B203", 30, true);

        Cours cours1 = new Cours(1, "JavaFX", "Lundi 10h", enseignant1, salle1);
        Cours cours2 = new Cours(2, "Programmation Réseau", "Mardi 14h", enseignant1, salle1);

        // Ajouter les cours à la Liste
        listeCours.add(cours1);
        listeCours.add(cours2);

        // Afficher dans le tableau
        tableEmploiDuTemps.setItems(listeCours);
    }

    @FXML
    private void handleLogout() {
        System.out.println("Déconnexion...");
        // Code pour retourner à l'écran de login
    }


    @FXML
    private void handleAddCourse() {
        System.out.println("Bouton Ajouter cliqué !");
        // Récupération des données depuis les TextFields
        String nomCours = fieldCours.getText();
        String nomEnseignant = fieldEnseignant.getText();
        String horaire = fieldHoraire.getText();
        String nomSalle = fieldSalle.getText();

        // Vérification que les champs ne sont pas vides
        if (!nomCours.isEmpty() && !nomEnseignant.isEmpty() && !horaire.isEmpty() && !nomSalle.isEmpty()) {
            // On crée les objets associés
            Enseignant enseignant = new Enseignant(1, nomEnseignant, "Prenom", "email@test.com", "0654321987", "prof123", "Informatique");
            Salle salle = new Salle(1, nomSalle, 30, true);
            Cours cours = new Cours(listeCours.size() + 1, nomCours, horaire, enseignant, salle);

            // Ajouter le cours à la liste observable
            listeCours.add(cours);

            // Mettre à jour le tableau
            tableEmploiDuTemps.setItems(listeCours);

            // Effacer les champs
            fieldCours.clear();
            fieldEnseignant.clear();
            fieldHoraire.clear();
            fieldSalle.clear();
        } else {
            System.out.println("Tous les champs doivent être remplis.");
        }
    }

    @FXML
    private void handleDeleteCourse() {
        // On récupère le cours sélectionné
        Cours selectedCours = tableEmploiDuTemps.getSelectionModel().getSelectedItem();

        if (selectedCours != null) {
            // Petite boîte de confirmation
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer ce cours ?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                listeCours.remove(selectedCours);
                System.out.println("Cours supprimé : " + selectedCours.getNom());
            }
        } else {
            System.out.println("Aucun cours sélectionné !");
        }
    }

}
