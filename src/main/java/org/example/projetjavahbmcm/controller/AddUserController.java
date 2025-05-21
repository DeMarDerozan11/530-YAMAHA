package org.example.projetjavahbmcm.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.projetjavahbmcm.database.DatabaseManager;

import java.io.IOException;

public class AddUserController {

    @FXML
    private TextField fieldNom;

    @FXML
    private TextField fieldPrenom;

    @FXML
    private TextField fieldEmail;

    @FXML
    private PasswordField fieldMotDePasse;

    @FXML
    private ComboBox<String> comboType;

    @FXML
    private void handleAjouterUtilisateur() {
        String nom = fieldNom.getText().trim();
        String prenom = fieldPrenom.getText().trim();
        String email = fieldEmail.getText().trim();
        String motDePasse = fieldMotDePasse.getText();
        String type = comboType.getValue();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || motDePasse.isEmpty() || type == null) {
            afficherAlerte("Tous les champs doivent être remplis.");
            return;
        }

        boolean success = DatabaseManager.ajouterUtilisateur(nom, prenom, email, motDePasse, type);
        if (success) {
            afficherAlerte("Utilisateur ajouté avec succès !");
            fieldNom.clear();
            fieldPrenom.clear();
            fieldEmail.clear();
            fieldMotDePasse.clear();
            comboType.getSelectionModel().clearSelection();
        } else {
            afficherAlerte("Erreur : impossible d'ajouter l'utilisateur. L'email est peut-être déjà utilisé.");
        }
    }

    private void afficherAlerte(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleRetour() {
        try {
            // Charger le tableau de bord administrateur
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/Dashboard.fxml"));
            Parent root = fxmlLoader.load();

            // Récupérer la fenêtre actuelle et y afficher le tableau de bord
            Stage stage = (Stage) fieldNom.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tableau de Bord - Admin");
            stage.show();

            System.out.println("Retour au tableau de bord admin !");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du retour au tableau de bord.");
        }
    }

    @FXML
    private TextField fieldClasse;


}
