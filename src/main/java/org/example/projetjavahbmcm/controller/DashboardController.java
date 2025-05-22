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
import org.example.projetjavahbmcm.model.Cours;
import org.example.projetjavahbmcm.model.Enseignant;
import org.example.projetjavahbmcm.model.Salle;

import java.io.IOException;


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
        Enseignant enseignant1 = new Enseignant(1, "Sensei", "Kakashi", "kakashi.konoha@gmail.com", "0654321987", "prof123", "Informatique");
        Enseignant enseignant2 = new Enseignant(2, "Sama", "Tsunade", "hokage.tsunade@gmail.com", "0654321989", "prof124", "Informatique");
        Enseignant enseignant3 = new Enseignant(2, "Sensei", "Jiraya", "grenouille93@gmail.com", "0654321981", "grenouille93", "Maitrise de soi");
        Salle salle1 = new Salle(1, "211", 30, true);
        Salle salle2 = new Salle(1, "206", 30, true);

        Cours cours1 = new Cours(1, "JavaFX", "Lundi 9h", enseignant1, salle1);
        Cours cours2 = new Cours(2, "Jens-jutsu", "Mardi 8h30", enseignant2, salle2);
        Cours cours3 = new Cours(3, "Energie", "Vendredi 13h30", enseignant3, salle2);

        // Ajouter les cours à la Liste
        listeCours.add(cours1);
        listeCours.add(cours2);
        listeCours.add(cours3);

        // Afficher dans le tableau
        tableEmploiDuTemps.setItems(listeCours);
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
            Enseignant enseignant = new Enseignant(1, nomEnseignant, "", "email@test.com", "0654321987", "prof123", "Informatique");
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

    @FXML
    private void handleLogout() {
        System.out.println("Déconnexion en cours...");

        try {
            // Charger le fichier FXML du login
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/login.fxml"));
            Parent root = fxmlLoader.load();

            // Récupérer la scène actuelle et la remplacer par le login
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion - Gestion des Emplois du Temps");
            stage.show();

            System.out.println("Retour à l'écran de connexion réussi !");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du retour à l'écran de connexion.");
        }
    }
    @FXML
    private Button btnLogout;

    @FXML
    private void handleOpenAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/AddUserForm.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un utilisateur");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCreateSchedule() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/ScheduleCreator.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Créateur d'Emploi du Temps");
            stage.setScene(new Scene(root, 800, 700));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur lors de l'ouverture du créateur d'emploi du temps.");
            alert.showAndWait();
        }
    }

}
