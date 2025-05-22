package org.example.projetjavahbmcm.database;

import java.io.File;
import java.sql.*;

public class DatabaseManager {
    private static final String DATABASE_FILE = "utilisateurs.db";
    private static final String URL = "jdbc:sqlite:" + DATABASE_FILE;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initDatabase() {
        File dbFile = new File(DATABASE_FILE);
        if (dbFile.exists() && !isValidDatabase(dbFile)) {
            System.out.println("Base de données corrompue détectée. Suppression...");
            dbFile.delete();
        }

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String createUtilisateur = """
                CREATE TABLE IF NOT EXISTS utilisateur (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL,
                    prenom TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    mot_de_passe TEXT NOT NULL,
                    type TEXT NOT NULL CHECK (type IN ('admin', 'enseignant', 'etudiant')),
                    classe TEXT
                );
            """;

            String createCreneaux = """
                CREATE TABLE IF NOT EXISTS creneau_horaire (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    jour_semaine TEXT NOT NULL CHECK (jour_semaine IN ('LUNDI', 'MARDI', 'MERCREDI', 'JEUDI', 'VENDREDI')),
                    heure_debut TEXT NOT NULL,
                    heure_fin TEXT NOT NULL,
                    numero_slot INTEGER NOT NULL
                );
            """;

            String createCours = """
                CREATE TABLE IF NOT EXISTS cours_emploi_temps (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom_cours TEXT NOT NULL,
                    enseignant_email TEXT NOT NULL,
                    classe TEXT NOT NULL,
                    salle TEXT NOT NULL,
                    creneau_id INTEGER NOT NULL,
                    couleur TEXT DEFAULT '#87CEEB',
                    FOREIGN KEY (enseignant_email) REFERENCES utilisateur(email),
                    FOREIGN KEY (creneau_id) REFERENCES creneau_horaire(id),
                    UNIQUE(classe, creneau_id),
                    UNIQUE(enseignant_email, creneau_id),
                    UNIQUE(salle, creneau_id)
                );
            """;

            String createSalle = """
                CREATE TABLE IF NOT EXISTS salle (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL UNIQUE,
                    capacite INTEGER NOT NULL CHECK (capacite > 0),
                    disponible BOOLEAN DEFAULT TRUE,
                    equipement TEXT
                );
            """;

            stmt.execute(createUtilisateur);
            stmt.execute(createCreneaux);
            stmt.execute(createCours);
            stmt.execute(createSalle);

            insertTestDataIfNeeded(conn);

            org.example.projetjavahbmcm.util.NotificationSystem.initNotificationsTable();

            System.out.println("Base de données initialisée avec succès.");

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation de la base : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean isValidDatabase(File dbFile) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
             Statement stmt = conn.createStatement()) {

            stmt.execute("SELECT name FROM sqlite_master WHERE type='table' LIMIT 1");
            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    private static void insertTestDataIfNeeded(Connection conn) throws SQLException {
        String checkUsers = "SELECT COUNT(*) FROM utilisateur";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkUsers)) {

            if (rs.next() && rs.getInt(1) == 0) {
                insertTestData(conn);
            }
        }
    }

    private static void insertTestData(Connection conn) throws SQLException {
        String[] insertStatements = {
                "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, type, classe) " +
                        "VALUES ('Admin', 'Système', 'admin@gmail.com', 'admin123', 'admin', NULL)",

                "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, type, classe) " +
                        "VALUES ('Defoe', 'Patrick', 'patrick@eleve.isep.fr', 'patrick123', 'etudiant', 'G1')",

                "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, type, classe) " +
                        "VALUES ('Durant', 'Marine', 'marine@eleve.isep.fr', 'marine123', 'etudiant', 'G1')",

                "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, type, classe) " +
                        "VALUES ('Martin', 'Lucas', 'lucas@eleve.isep.fr', 'lucas123', 'etudiant', 'G2')",

                "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, type, classe) " +
                        "VALUES ('Bernard', 'Emma', 'emma@eleve.isep.fr', 'emma123', 'etudiant', 'G2')",

                "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, type, classe) " +
                        "VALUES ('Moreau', 'Hugo', 'hugo@eleve.isep.fr', 'hugo123', 'etudiant', 'G3')",

                "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, type, classe) " +
                        "VALUES ('Petit', 'Léa', 'lea@eleve.isep.fr', 'lea123', 'etudiant', 'G3')",

                "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, type, classe) " +
                        "VALUES ('Roux', 'Nathan', 'nathan@eleve.isep.fr', 'nathan123', 'etudiant', 'G4')",

                "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, type, classe) " +
                        "VALUES ('Fournier', 'Chloé', 'chloe@eleve.isep.fr', 'chloe123', 'etudiant', 'G4')",

                "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, type, classe) " +
                        "VALUES ('Girard', 'Antoine', 'antoine@eleve.isep.fr', 'antoine123', 'etudiant', 'G5')",

                "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, type, classe) " +
                        "VALUES ('Bonnet', 'Manon', 'manon@eleve.isep.fr', 'manon123', 'etudiant', 'G5')",

                "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, type, classe) " +
                        "VALUES ('Dupont', 'Maxime', 'maxime@eleve.isep.fr', 'maxime123', 'etudiant', 'G6')",

                "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, type, classe) " +
                        "VALUES ('Laurent', 'Sarah', 'sarah@eleve.isep.fr', 'sarah123', 'etudiant', 'G6')",

                "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, type, classe) " +
                        "VALUES ('Sensei', 'Kakashi', 'kakashi@enseignant.isep.fr', 'prof123', 'enseignant', NULL)",

                "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, type, classe) " +
                        "VALUES ('Sama', 'Tsunade', 'tsunade@enseignant.isep.fr', 'prof124', 'enseignant', NULL)",

                "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, type, classe) " +
                        "VALUES ('Professeur', 'Dumbledore', 'dumbledore@enseignant.isep.fr', 'prof125', 'enseignant', NULL)",

                "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, type, classe) " +
                        "VALUES ('Docteur', 'Strange', 'strange@enseignant.isep.fr', 'prof126', 'enseignant', NULL)"
        };

        String[] salleStatements = {
                "INSERT INTO salle (nom, capacite, disponible, equipement) " +
                        "VALUES ('211', 30, TRUE, 'Projecteur, Ordinateurs')",

                "INSERT INTO salle (nom, capacite, disponible, equipement) " +
                        "VALUES ('206', 25, TRUE, 'Tableau interactif')",

                "INSERT INTO salle (nom, capacite, disponible, equipement) " +
                        "VALUES ('305', 40, TRUE, 'Projecteur, Son')"
        };

        String[] creneauxStatements = {
                "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('LUNDI', '08:30', '10:30', 1)",
                "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('LUNDI', '10:45', '12:45', 2)",
                "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('LUNDI', '13:45', '15:45', 3)",
                "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('LUNDI', '16:00', '18:00', 4)",

                "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('MARDI', '08:30', '10:30', 1)",
                "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('MARDI', '10:45', '12:45', 2)",
                "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('MARDI', '13:45', '15:45', 3)",
                "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('MARDI', '16:00', '18:00', 4)",

                "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('MERCREDI', '08:30', '10:30', 1)",
                "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('MERCREDI', '10:45', '12:45', 2)",
                "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('MERCREDI', '13:45', '15:45', 3)",
                "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('MERCREDI', '16:00', '18:00', 4)",

                "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('JEUDI', '08:30', '10:30', 1)",
                "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('JEUDI', '10:45', '12:45', 2)",
                "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('JEUDI', '13:45', '15:45', 3)",
                "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('JEUDI', '16:00', '18:00', 4)",

                "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('VENDREDI', '08:30', '10:30', 1)",
                "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('VENDREDI', '10:45', '12:45', 2)",
                "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('VENDREDI', '13:45', '15:45', 3)",
                "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('VENDREDI', '16:00', '18:00', 4)"
        };

        try (Statement stmt = conn.createStatement()) {
            for (String sql : insertStatements) {
                try {
                    stmt.execute(sql);
                } catch (SQLException e) {
                    System.out.println("Donnée déjà présente ou erreur : " + e.getMessage());
                }
            }

            for (String sql : salleStatements) {
                try {
                    stmt.execute(sql);
                } catch (SQLException e) {
                    System.out.println("Salle déjà présente ou erreur : " + e.getMessage());
                }
            }

            for (String sql : creneauxStatements) {
                try {
                    stmt.execute(sql);
                } catch (SQLException e) {
                    System.out.println("Créneau déjà présent ou erreur : " + e.getMessage());
                }
            }

            System.out.println("Données de test insérées avec succès.");
        }
    }


    public static boolean ajouterUtilisateur(String nom, String prenom, String email, String motDePasse, String type, String classe) {
        String sql = "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, type, classe) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nom);
            pstmt.setString(2, prenom);
            pstmt.setString(3, email);
            pstmt.setString(4, motDePasse);
            pstmt.setString(5, type);
            pstmt.setString(6, classe);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Utilisateur ajouté avec succès : " + email);
                return true;
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                System.out.println("Erreur : Un utilisateur avec cet email existe déjà.");
            } else {
                System.err.println("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
            }
        }
        return false;
    }

    public static boolean authentifierUtilisateur(String email, String motDePasse) {
        String sql = "SELECT id FROM utilisateur WHERE email = ? AND mot_de_passe = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, motDePasse);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'authentification : " + e.getMessage());
        }
        return false;
    }

    public static String getTypeUtilisateur(String email) {
        String sql = "SELECT type FROM utilisateur WHERE email = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("type");
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du type : " + e.getMessage());
        }

        return null;
    }

    public static void reinitialiserCreneaux() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM creneau_horaire");
            System.out.println("Anciens créneaux supprimés.");

            String[] creneauxStatements = {

                    "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('LUNDI', '08:30', '10:30', 1)",
                    "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('LUNDI', '10:45', '12:45', 2)",
                    "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('LUNDI', '13:45', '15:45', 3)",
                    "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('LUNDI', '16:00', '18:00', 4)",

                    "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('MARDI', '08:30', '10:30', 1)",
                    "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('MARDI', '10:45', '12:45', 2)",
                    "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('MARDI', '13:45', '15:45', 3)",
                    "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('MARDI', '16:00', '18:00', 4)",

                    "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('MERCREDI', '08:30', '10:30', 1)",
                    "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('MERCREDI', '10:45', '12:45', 2)",
                    "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('MERCREDI', '13:45', '15:45', 3)",
                    "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('MERCREDI', '16:00', '18:00', 4)",

                    "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('JEUDI', '08:30', '10:30', 1)",
                    "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('JEUDI', '10:45', '12:45', 2)",
                    "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('JEUDI', '13:45', '15:45', 3)",
                    "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('JEUDI', '16:00', '18:00', 4)",

                    "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('VENDREDI', '08:30', '10:30', 1)",
                    "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('VENDREDI', '10:45', '12:45', 2)",
                    "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('VENDREDI', '13:45', '15:45', 3)",
                    "INSERT INTO creneau_horaire (jour_semaine, heure_debut, heure_fin, numero_slot) VALUES ('VENDREDI', '16:00', '18:00', 4)"
            };

            for (String sql : creneauxStatements) {
                try {
                    stmt.execute(sql);
                } catch (SQLException e) {
                    System.out.println("Erreur lors de l'insertion du créneau : " + e.getMessage());
                }
            }

            System.out.println("Nouveaux créneaux horaires réinsérés avec succès.");

        } catch (SQLException e) {
            System.err.println("Erreur lors de la réinitialisation des créneaux : " + e.getMessage());
        }
    }

    public static boolean ajouterCoursEmploiDuTemps(String nomCours, String enseignantEmail, String classe, String salle, int creneauId) {
        String sql = "INSERT INTO cours_emploi_temps (nom_cours, enseignant_email, classe, salle, creneau_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nomCours);
            pstmt.setString(2, enseignantEmail);
            pstmt.setString(3, classe);
            pstmt.setString(4, salle);
            pstmt.setInt(5, creneauId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Cours ajouté à l'emploi du temps : " + nomCours);
                return true;
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                System.out.println("Erreur : Conflit d'horaire détecté (classe, enseignant ou salle déjà occupé).");
            } else {
                System.err.println("Erreur lors de l'ajout du cours : " + e.getMessage());
            }
        }
        return false;
    }

    public static java.util.List<org.example.projetjavahbmcm.model.CoursEmploiDuTemps> getEmploiDuTempsParClasse(String classe) {
        String sql = """
            SELECT c.id, c.nom_cours, u.nom || ' ' || u.prenom as enseignant_nom, 
                   c.enseignant_email, c.classe, c.salle, c.couleur,
                   ch.id as creneau_id, ch.jour_semaine, ch.heure_debut, ch.heure_fin, ch.numero_slot
            FROM cours_emploi_temps c
            JOIN creneau_horaire ch ON c.creneau_id = ch.id
            JOIN utilisateur u ON c.enseignant_email = u.email
            WHERE c.classe = ?
            ORDER BY ch.jour_semaine, ch.numero_slot
        """;

        java.util.List<org.example.projetjavahbmcm.model.CoursEmploiDuTemps> cours = new java.util.ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, classe);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    org.example.projetjavahbmcm.model.CreneauHoraire creneau =
                            new org.example.projetjavahbmcm.model.CreneauHoraire(
                                    rs.getInt("creneau_id"),
                                    rs.getString("jour_semaine"),
                                    rs.getString("heure_debut"),
                                    rs.getString("heure_fin"),
                                    rs.getInt("numero_slot")
                            );

                    org.example.projetjavahbmcm.model.CoursEmploiDuTemps coursEdt =
                            new org.example.projetjavahbmcm.model.CoursEmploiDuTemps(
                                    rs.getInt("id"),
                                    rs.getString("nom_cours"),
                                    rs.getString("enseignant_nom"),
                                    rs.getString("enseignant_email"),
                                    rs.getString("classe"),
                                    rs.getString("salle"),
                                    creneau
                            );
                    coursEdt.setCouleur(rs.getString("couleur"));
                    cours.add(coursEdt);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'emploi du temps : " + e.getMessage());
        }

        return cours;
    }

    public static java.util.List<org.example.projetjavahbmcm.model.CoursEmploiDuTemps> getEmploiDuTempsParEnseignant(String enseignantEmail) {
        String sql = """
            SELECT c.id, c.nom_cours, u.nom || ' ' || u.prenom as enseignant_nom, 
                   c.enseignant_email, c.classe, c.salle, c.couleur,
                   ch.id as creneau_id, ch.jour_semaine, ch.heure_debut, ch.heure_fin, ch.numero_slot
            FROM cours_emploi_temps c
            JOIN creneau_horaire ch ON c.creneau_id = ch.id
            JOIN utilisateur u ON c.enseignant_email = u.email
            WHERE c.enseignant_email = ?
            ORDER BY ch.jour_semaine, ch.numero_slot
        """;

        java.util.List<org.example.projetjavahbmcm.model.CoursEmploiDuTemps> cours = new java.util.ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, enseignantEmail);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    org.example.projetjavahbmcm.model.CreneauHoraire creneau =
                            new org.example.projetjavahbmcm.model.CreneauHoraire(
                                    rs.getInt("creneau_id"),
                                    rs.getString("jour_semaine"),
                                    rs.getString("heure_debut"),
                                    rs.getString("heure_fin"),
                                    rs.getInt("numero_slot")
                            );

                    org.example.projetjavahbmcm.model.CoursEmploiDuTemps coursEdt =
                            new org.example.projetjavahbmcm.model.CoursEmploiDuTemps(
                                    rs.getInt("id"),
                                    rs.getString("nom_cours"),
                                    rs.getString("enseignant_nom"),
                                    rs.getString("enseignant_email"),
                                    rs.getString("classe"),
                                    rs.getString("salle"),
                                    creneau
                            );
                    coursEdt.setCouleur(rs.getString("couleur"));
                    cours.add(coursEdt);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'emploi du temps : " + e.getMessage());
        }

        return cours;
    }

    public static java.util.List<org.example.projetjavahbmcm.model.CreneauHoraire> getTousLesCreneaux() {
        String sql = "SELECT * FROM creneau_horaire ORDER BY jour_semaine, numero_slot";
        java.util.List<org.example.projetjavahbmcm.model.CreneauHoraire> creneaux = new java.util.ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                creneaux.add(new org.example.projetjavahbmcm.model.CreneauHoraire(
                        rs.getInt("id"),
                        rs.getString("jour_semaine"),
                        rs.getString("heure_debut"),
                        rs.getString("heure_fin"),
                        rs.getInt("numero_slot")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des créneaux : " + e.getMessage());
        }

        return creneaux;
    }

    public static java.util.List<String> getToutesLesClasses() {
        String sql = "SELECT DISTINCT classe FROM utilisateur WHERE type = 'etudiant' AND classe IS NOT NULL ORDER BY classe";
        java.util.List<String> classes = new java.util.ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                classes.add(rs.getString("classe"));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des classes : " + e.getMessage());
        }

        return classes;
    }

    public static java.util.List<String> getTousLesEnseignants() {
        String sql = "SELECT email, nom || ' ' || prenom as nom_complet FROM utilisateur WHERE type = 'enseignant' ORDER BY nom, prenom";
        java.util.List<String> enseignants = new java.util.ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                enseignants.add(rs.getString("email") + " - " + rs.getString("nom_complet"));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des enseignants : " + e.getMessage());
        }

        return enseignants;
    }

    public static java.util.List<String> getToutesLesSalles() {
        String sql = "SELECT nom FROM salle WHERE disponible = TRUE ORDER BY nom";
        java.util.List<String> salles = new java.util.ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                salles.add(rs.getString("nom"));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des salles : " + e.getMessage());
        }

        return salles;
    }
}