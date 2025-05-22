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
import javafx.stage.Stage;
import org.example.projetjavahbmcm.database.DatabaseManager;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

        if (email.equals("admin@gmail.com") && password.equals("admin123")) {
            System.out.println("Connexion réussie !");

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/Dashboard.fxml"));
                Parent root = fxmlLoader.load();

                // On récupère la fenêtre actuelle
                Stage stage = (Stage) emailField.getScene().getWindow();

                // On charge la nouvelle scène
                stage.setScene(new Scene(root));
                stage.setTitle("Tableau de Bord - Emploi du Temps");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Échec de la connexion.");
        }
    }



    private void afficherAlerte(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    /*
    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String motDePasse = passwordField.getText().trim();

        System.out.println("Tentative de connexion : " + email);

        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM utilisateur WHERE email = ? AND mot_de_passe = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, motDePasse);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("Connexion réussie !");

                FXMLLoader loader;
                if (email.toLowerCase().endsWith("@eleve.isep.fr")) {
                    loader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/StudentDashboard.fxml"));
                } else if (email.toLowerCase().endsWith("@enseignant.isep.fr")) {
                    // Tu peux créer TeacherDashboard.fxml si besoin
                    loader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/TeacherDashboard.fxml"));
                } else {
                    // Par défaut, on redirige vers l'admin dashboard
                    loader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/Dashboard.fxml"));
                }

                Parent root = loader.load();
                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Espace utilisateur");
                stage.show();

            } else {
                afficherAlerte("Email ou mot de passe incorrect.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte("Erreur lors de la connexion : " + e.getMessage());
        }

     */

    /*
    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String motDePasse = passwordField.getText().trim();

        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM utilisateur WHERE email = ? AND mot_de_passe = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, motDePasse);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String type = rs.getString("type");
                String fxmlFile = "";

                switch (type) {
                    case "admin":
                        fxmlFile = "/org/example/projetjavahbmcm/view/Dashboard.fxml";
                        break;
                    case "enseignant":
                        fxmlFile = "/org/example/projetjavahbmcm/view/TeacherDashboard.fxml";
                        break;
                    case "etudiant":
                        fxmlFile = "/org/example/projetjavahbmcm/view/StudentDashboard.fxml";
                        break;
                    default:
                        afficherAlerte("Type d'utilisateur inconnu.");
                        return;
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                Parent root = loader.load();
                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Espace utilisateur");
                stage.show();

            } else {
                afficherAlerte("Email ou mot de passe incorrect.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte("Erreur lors de la connexion : " + e.getMessage());
        }
    }

     */
}
