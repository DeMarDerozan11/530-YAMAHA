package org.example.projetjavahbmcm;
import org.example.projetjavahbmcm.database.DatabaseManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.projetjavahbmcm.model.*;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        DatabaseManager.initDatabase();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/org/example/projetjavahbmcm/view/login.fxml"));
        Parent root = fxmlLoader.load();

        System.out.println("FXMLLoader chargé : " + (root != null));

        Scene scene = new Scene(root, 400, 230);
        stage.setTitle("Connexion - Gestion des Emplois du Temps");
        stage.setScene(scene);
        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }
}