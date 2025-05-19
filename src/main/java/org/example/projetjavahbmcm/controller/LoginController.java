package org.example.projetjavahbmcm.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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
}