package org.example.projetjavahbmcm.database;

import java.sql.*;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:utilisateurs.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = """
                    CREATE TABLE IF NOT EXISTS utilisateur (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nom TEXT NOT NULL,
                        prenom TEXT NOT NULL,
                        email TEXT UNIQUE NOT NULL,
                        mot_de_passe TEXT NOT NULL,
                        type TEXT NOT NULL -- 'admin', 'enseignant', 'etudiant'
                    );
                    """;

            // Créer la table utilisateur si elle n'existe pas
            String createUtilisateur = """
                CREATE TABLE IF NOT EXISTS utilisateur (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL,
                    prenom TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    mot_de_passe TEXT NOT NULL,
                    type TEXT NOT NULL,
                    classe TEXT -- Null sauf pour les élèves
                );
            """;

            // Créer la table cours
            String createCours = """
                CREATE TABLE IF NOT EXISTS cours (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL,
                    horaire TEXT NOT NULL,
                    salle TEXT NOT NULL,
                    enseignant_email TEXT,
                    classe TEXT,
                    FOREIGN KEY (enseignant_email) REFERENCES utilisateur(email)
                );
            """;

            String insertEleves = """
    INSERT OR IGNORE INTO utilisateur (nom, prenom, email, mot_de_passe, type, classe)
    VALUES 
        ('Defoe', 'Patrick', 'patrick@eleve.isep.fr', 'patrick123', 'etudiant', '3eD'),
        ('Durant', 'Marine', 'marine@eleve.isep.fr', 'marine123', 'etudiant', '3eD');
""";
            stmt.execute(insertEleves);

            stmt.execute(sql);
            System.out.println("Base de données initialisée avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'initialisation de la base : " + e.getMessage());
        }
    }

    public static boolean ajouterUtilisateur(String nom, String prenom, String email, String motDePasse, String type) {
        String sql = "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, type) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nom);
            pstmt.setString(2, prenom);
            pstmt.setString(3, email);
            pstmt.setString(4, motDePasse);
            pstmt.setString(5, type);

            pstmt.executeUpdate();
            System.out.println("Utilisateur ajouté !");
            return true;
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
            return false;
        }
    }




}
