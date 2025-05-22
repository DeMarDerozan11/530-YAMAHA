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
    private TextField fieldClasse;

    @FXML
    private void initialize() {
        // Listener pour afficher/masquer le champ classe selon le type
        comboType.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if ("etudiant".equals(newVal)) {
                fieldClasse.setVisible(true);
                fieldClasse.setPromptText("Classe (ex: 3eD)");
            } else {
                fieldClasse.setVisible(false);
                fieldClasse.clear();
            }
        });

        // Par défaut, masquer le champ classe
        fieldClasse.setVisible(false);
    }

    @FXML
    private void handleAjouterUtilisateur() {
        String nom = fieldNom.getText().trim();
        String prenom = fieldPrenom.getText().trim();
        String email = fieldEmail.getText().trim();
        String motDePasse = fieldMotDePasse.getText();
        String type = comboType.getValue();
        String classe = fieldClasse.getText().trim();

        // Validation des champs obligatoires
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || motDePasse.isEmpty() || type == null) {
            afficherAlerte("Erreur", "Tous les champs obligatoires doivent être remplis.", Alert.AlertType.ERROR);
            return;
        }

        // Validation de l'email
        if (!isValidEmail(email)) {
            afficherAlerte("Erreur", "Format d'email invalide.", Alert.AlertType.ERROR);
            return;
        }

        // Validation du mot de passe
        if (motDePasse.length() < 6) {
            afficherAlerte("Erreur", "Le mot de passe doit contenir au moins 6 caractères.", Alert.AlertType.ERROR);
            return;
        }

        // Pour les étudiants, la classe est optionnelle mais recommandée
        if ("etudiant".equals(type) && classe.isEmpty()) {
            classe = null; // Peut être null en base
        }

        // Tentative d'ajout en base
        boolean success = DatabaseManager.ajouterUtilisateur(nom, prenom, email, motDePasse, type, classe);

        if (success) {
            afficherAlerte("Succès", "Utilisateur ajouté avec succès !", Alert.AlertType.INFORMATION);
            clearFields();
        } else {
            afficherAlerte("Erreur", "Impossible d'ajouter l'utilisateur. L'email est peut-être déjà utilisé.", Alert.AlertType.ERROR);
        }
    }

    private void clearFields() {
        fieldNom.clear();
        fieldPrenom.clear();
        fieldEmail.clear();
        fieldMotDePasse.clear();
        fieldClasse.clear();
        comboType.getSelectionModel().clearSelection();
        fieldClasse.setVisible(false);
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    private void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/Dashboard.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = (Stage) fieldNom.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tableau de Bord - Admin");
            stage.show();

            System.out.println("Retour au tableau de bord admin !");
        } catch (IOException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Erreur lors du retour au tableau de bord.", Alert.AlertType.ERROR);
        }
    }
}