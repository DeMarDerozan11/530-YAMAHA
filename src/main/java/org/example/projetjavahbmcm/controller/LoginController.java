package org.example.projetjavahbmcm.controller;

import javafx.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.projetjavahbmcm.database.DatabaseManager;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    public void initialize() {
        System.out.println("LoginController initialisé !");
    }

    @FXML
    private void handleLogin() {
        System.out.println("Bouton Se connecter cliqué !");

        String email = emailField.getText();
        String password = passwordField.getText();

        System.out.println("Tentative de connexion : " + email);

        if (DatabaseManager.authentifierUtilisateur(email, password)) {
            System.out.println("Connexion réussie !");

            String typeUtilisateur = DatabaseManager.getTypeUtilisateur(email);

            try {
                FXMLLoader fxmlLoader;
                String titre;

                switch (typeUtilisateur) {
                    case "admin":
                        fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/ModernDashboard.fxml"));
                        titre = "Dashboard Admin - Gestion des Emplois du Temps";
                        break;
                    case "enseignant":
                        fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/TeacherDashboard.fxml"));
                        titre = "Tableau de Bord - Enseignant";
                        break;
                    case "etudiant":
                        fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/StudentDashboard.fxml"));
                        titre = "Emploi du Temps - Étudiant";
                        break;
                    default:
                        afficherAlerte("Type d'utilisateur non reconnu.");
                        return;
                }

                Parent root = fxmlLoader.load();

                if ("etudiant".equals(typeUtilisateur)) {
                    StudentDashboardController controller = fxmlLoader.getController();
                    controller.setUtilisateurConnecte(email);
                } else if ("enseignant".equals(typeUtilisateur)) {
                    TeacherDashboardController controller = fxmlLoader.getController();
                    controller.setUtilisateurConnecte(email);
                }

                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle(titre);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                afficherAlerte("Erreur lors du chargement de l'interface.");
            }
        } else if (email.equals("admin@gmail.com") && password.equals("admin123")) {
            System.out.println("Connexion admin de secours !");

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/ModernDashboard.fxml"));
                Parent root = fxmlLoader.load();

                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Dashboard Admin - Gestion des Emplois du Temps");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Échec de la connexion.");
            afficherAlerte("Email ou mot de passe incorrect.");
        }
    }

    private void afficherAlerte(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}