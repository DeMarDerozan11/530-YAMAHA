package org.example.projetjavahbmcm.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.projetjavahbmcm.database.DatabaseManager;

import java.io.IOException;

public class TeacherDashboardController {

    @FXML private Button btnLogout;
    @FXML private Label labelBienvenue;

    private String emailUtilisateur;
    private String nomEnseignant;

    public void setUtilisateurConnecte(String email) {
        this.emailUtilisateur = email;
        this.nomEnseignant = getNomEnseignant(email);

        if (labelBienvenue != null && nomEnseignant != null) {
            labelBienvenue.setText("Bienvenue, " + nomEnseignant);
        }
    }

    private String getNomEnseignant(String email) {
        String sql = "SELECT nom || ' ' || prenom as nom_complet FROM utilisateur WHERE email = ? AND type = 'enseignant'";
        try (var conn = DatabaseManager.getConnection();
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("nom_complet");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du nom : " + e.getMessage());
        }
        return "Enseignant";
    }

    @FXML
    private void handleVoirMonEmploiDuTemps() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/ScheduleView.fxml"));
            Parent root = loader.load();

            ScheduleViewController controller = loader.getController();
            controller.afficherEmploiDuTempsEnseignant(emailUtilisateur, nomEnseignant);

            Stage stage = new Stage();
            stage.setTitle("Mon Emploi du Temps - " + nomEnseignant);
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
            stage.show();

        } catch (IOException e) {
            System.out.println("Erreur lors du retour à l'écran de connexion : " + e.getMessage());
        }
    }

    @FXML
    private void handleSignalerAnomalie() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/projetjavahbmcm/view/AnomalyReport.fxml"));
            Parent root = loader.load();

            AnomalyReportController controller = loader.getController();
            controller.setUtilisateurConnecte(emailUtilisateur);

            Stage stage = new Stage();
            stage.setTitle("Signaler une anomalie");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'ouverture du formulaire d'anomalie : " + e.getMessage());
        }
    }

    

}
