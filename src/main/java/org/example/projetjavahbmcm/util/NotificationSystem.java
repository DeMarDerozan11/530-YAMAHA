package org.example.projetjavahbmcm.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.example.projetjavahbmcm.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotificationSystem {

    public static class Notification {
        private int id;
        private String destinataire;
        private String message;
        private String type; // INFO, WARNING, ERROR
        private LocalDateTime dateCreation;
        private boolean lue;

        public Notification(int id, String destinataire, String message, String type, LocalDateTime dateCreation, boolean lue) {
            this.id = id;
            this.destinataire = destinataire;
            this.message = message;
            this.type = type;
            this.dateCreation = dateCreation;
            this.lue = lue;
        }

        // Getters
        public int getId() { return id; }
        public String getDestinataire() { return destinataire; }
        public String getMessage() { return message; }
        public String getType() { return type; }
        public LocalDateTime getDateCreation() { return dateCreation; }
        public boolean isLue() { return lue; }

        public String getFormattedDate() {
            return dateCreation.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
    }

    // Créer la table notifications si elle n'existe pas
    public static void initNotificationsTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS notifications (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                destinataire TEXT NOT NULL,
                message TEXT NOT NULL,
                type TEXT DEFAULT 'INFO',
                date_creation TEXT NOT NULL,
                lue BOOLEAN DEFAULT FALSE
            );
        """;

        try (Connection conn = DatabaseManager.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Erreur création table notifications : " + e.getMessage());
        }
    }

    // Envoyer une notification
    public static boolean envoyerNotification(String destinataire, String message, String type) {
        String sql = "INSERT INTO notifications (destinataire, message, type, date_creation) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, destinataire);
            pstmt.setString(2, message);
            pstmt.setString(3, type);
            pstmt.setString(4, LocalDateTime.now().toString());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur envoi notification : " + e.getMessage());
            return false;
        }
    }

    // Récupérer les notifications d'un utilisateur
    public static List<Notification> getNotifications(String utilisateur) {
        String sql = "SELECT * FROM notifications WHERE destinataire = ? OR destinataire = 'ALL' ORDER BY date_creation DESC LIMIT 10";
        List<Notification> notifications = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, utilisateur);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notifications.add(new Notification(
                        rs.getInt("id"),
                        rs.getString("destinataire"),
                        rs.getString("message"),
                        rs.getString("type"),
                        LocalDateTime.parse(rs.getString("date_creation")),
                        rs.getBoolean("lue")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Erreur récupération notifications : " + e.getMessage());
        }

        return notifications;
    }

    // Marquer une notification comme lue
    public static void marquerCommeLue(int notificationId) {
        String sql = "UPDATE notifications SET lue = TRUE WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, notificationId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur marquage notification : " + e.getMessage());
        }
    }

    // Compter les notifications non lues
    public static int compterNotificationsNonLues(String utilisateur) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE (destinataire = ? OR destinataire = 'ALL') AND lue = FALSE";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, utilisateur);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Erreur comptage notifications : " + e.getMessage());
        }

        return 0;
    }

    // Notifications automatiques pour changements d'emploi du temps
    public static void notifierChangementEmploiDuTemps(String classe, String message) {
        // Récupérer tous les étudiants de la classe
        String sql = "SELECT email FROM utilisateur WHERE type = 'etudiant' AND classe = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, classe);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                envoyerNotification(rs.getString("email"), message, "WARNING");
            }

            System.out.println("Notifications envoyées à la classe " + classe);

        } catch (SQLException e) {
            System.err.println("Erreur notification classe : " + e.getMessage());
        }
    }

    // Afficher les notifications dans une popup
    public static void afficherNotifications(String utilisateur) {
        List<Notification> notifications = getNotifications(utilisateur);

        if (notifications.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Notifications");
            alert.setHeaderText("Aucune notification");
            alert.setContentText("Vous n'avez aucune notification.");
            alert.showAndWait();
            return;
        }

        StringBuilder message = new StringBuilder("📢 Vos notifications :\n\n");

        for (Notification notif : notifications) {
            String icon = switch(notif.getType()) {
                case "WARNING" -> "⚠️";
                case "ERROR" -> "❌";
                default -> "ℹ️";
            };

            message.append(icon)
                    .append(" ")
                    .append(notif.getMessage())
                    .append("\n📅 ")
                    .append(notif.getFormattedDate())
                    .append("\n\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notifications");
        alert.setHeaderText("Vos dernières notifications");
        alert.setContentText(message.toString());
        alert.getDialogPane().setPrefWidth(500);
        alert.showAndWait();

        // Marquer toutes comme lues
        for (Notification notif : notifications) {
            if (!notif.isLue()) {
                marquerCommeLue(notif.getId());
            }
        }
    }
}