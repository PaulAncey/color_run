package fr.esgi.color_run.database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class DatabaseInitializer implements ServletContextListener {

        @Override
        public void contextInitialized(ServletContextEvent sce) {
                System.out.println("Initialisation de la base de données...");

                try (Connection conn = DatabaseConfig.getConnection();
                                Statement stmt = conn.createStatement()) {

                        // Charger le script SQL depuis les ressources
                        InputStream is = getClass().getClassLoader().getResourceAsStream("database/schema.sql");

                        if (is == null) {
                                System.err.println("ERREUR: Le fichier schema.sql est introuvable dans le classpath!");
                                return;
                        }

                        String sql = new BufferedReader(new InputStreamReader(is))
                                        .lines()
                                        .collect(Collectors.joining("\n"));

                        // Exécuter le script SQL
                        stmt.execute(sql);

                        System.out.println("Base de données initialisée avec succès!");

                } catch (Exception e) {
                        System.err.println("Erreur lors de l'initialisation de la base de données: " + e.getMessage());
                        e.printStackTrace();
                }
        }

        @Override
        public void contextDestroyed(ServletContextEvent sce) {
                // Rien à faire
        }
}
// package fr.esgi.color_run.database;

// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.sql.Connection;
// import java.sql.Statement;
// import jakarta.servlet.ServletContextEvent;
// import jakarta.servlet.ServletContextListener;
// import jakarta.servlet.annotation.WebListener;

// @WebListener
// public class DatabaseInitializer implements ServletContextListener {

// @Override
// public void contextInitialized(ServletContextEvent sce) {
// try (Connection conn = DatabaseConfig.getConnection();
// // Statement stmt = conn.createStatement()
// ) {

// String sqlScript =
// Files.readString(Path.of("src/main/resources/init_data.sql"));

// try (Statement stmt = conn.createStatement()) {
// stmt.execute(sqlScript);
// }

// // Création des tables
// // createTables(stmt);

// // Insertion de données initiales (si nécessaire)
// // insertInitialData(stmt);

// System.out.println("Database initialized successfully!");
// } catch (Exception e) {
// System.err.println("Error initializing database: " + e.getMessage());
// e.printStackTrace();
// }
// }

// // private void createTables(Statement stmt) throws Exception {
// // // Table Role
// // stmt.execute("CREATE TABLE IF NOT EXISTS role (" +
// // "id_role INT AUTO_INCREMENT PRIMARY KEY," +
// // "nom_role VARCHAR(50) NOT NULL," +
// // "description VARCHAR(255)" +
// // ")");
// // // Table Utilisateur
// // stmt.execute("CREATE TABLE IF NOT EXISTS utilisateur (" +
// // "id_utilisateur INT AUTO_INCREMENT PRIMARY KEY," +
// // "nom VARCHAR(100) NOT NULL," +
// // "prenom VARCHAR(100) NOT NULL," +
// // "email VARCHAR(255) NOT NULL UNIQUE," +
// // "mot_de_passe VARCHAR(255) NOT NULL," +
// // "photo_profile VARCHAR(255)," +
// // "compte_verifie BOOLEAN DEFAULT FALSE," +
// // "date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
// // ")");
// // // Table UtilisateurRole
// // stmt.execute("CREATE TABLE IF NOT EXISTS utilisateur_role (" +
// // "id_utilisateur_role INT AUTO_INCREMENT PRIMARY KEY," +
// // "id_utilisateur INT NOT NULL," +
// // "id_role INT NOT NULL," +
// // "date_attribution TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
// // "FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id_utilisateur)," +
// // "FOREIGN KEY (id_role) REFERENCES role(id_role)" +
// // ")");
// // // Table Course
// // stmt.execute("CREATE TABLE IF NOT EXISTS course (" +
// // "id_course INT AUTO_INCREMENT PRIMARY KEY," +
// // "nom VARCHAR(100) NOT NULL," +
// // "description TEXT," +
// // "date_course DATE NOT NULL," +
// // "heure_debut TIME NOT NULL," +
// // "ville VARCHAR(100) NOT NULL," +
// // "adresse VARCHAR(255) NOT NULL," +
// // "latitude FLOAT," +
// // "longitude FLOAT," +
// // "distance_km FLOAT NOT NULL," +
// // "nb_max_participants INT NOT NULL," +
// // "prix FLOAT NOT NULL," +
// // "avec_obstacles BOOLEAN DEFAULT FALSE," +
// // "cause_soutenue VARCHAR(255)," +
// // "id_organisateur INT NOT NULL," +
// // "date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
// // "FOREIGN KEY (id_organisateur) REFERENCES utilisateur(id_utilisateur)" +
// // ")");
// // // Table Participation
// // stmt.execute("CREATE TABLE IF NOT EXISTS participation (" +
// // "id_participation INT AUTO_INCREMENT PRIMARY KEY," +
// // "id_utilisateur INT NOT NULL," +
// // "id_course INT NOT NULL," +
// // "numero_dossard INT NOT NULL," +
// // "date_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
// // "dossard_telecharge BOOLEAN DEFAULT FALSE," +
// // "FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id_utilisateur)," +
// // "FOREIGN KEY (id_course) REFERENCES course(id_course)," +
// // "UNIQUE (id_course, numero_dossard)" +
// // ")");
// // // Table Message
// // stmt.execute("CREATE TABLE IF NOT EXISTS message (" +
// // "id_message INT AUTO_INCREMENT PRIMARY KEY," +
// // "id_course INT NOT NULL," +
// // "id_utilisateur INT NOT NULL," +
// // "contenu TEXT NOT NULL," +
// // "date_envoi TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
// // "FOREIGN KEY (id_course) REFERENCES course(id_course)," +
// // "FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id_utilisateur)" +
// // ")");
// // }
// // private void insertInitialData(Statement stmt) throws Exception {
// // // Vérifier si des rôles existent déjà
// // if (!stmt.executeQuery("SELECT COUNT(*) FROM role").next() ||
// // stmt.executeQuery("SELECT COUNT(*) FROM role").getInt(1) == 0) {
// // // Insertion des rôles par défaut
// // stmt.execute("INSERT INTO role (nom_role, description) VALUES " +
// // "('ADMIN', 'Administrateur du système'), " +
// // "('ORGANISATEUR', 'Organisateur des courses'), " +
// // "('PARTICIPANT', 'Participant aux courses')");
// // }
// // // Insérer un utilisateur admin par défaut (pour les tests)
// // if (!stmt.executeQuery("SELECT COUNT(*) FROM utilisateur").next() ||
// // stmt.executeQuery("SELECT COUNT(*) FROM utilisateur").getInt(1) == 0) {
// // stmt.execute("INSERT INTO utilisateur (nom, prenom, email, mot_de_passe,
// // compte_verifie) VALUES "
// // +
// // "('Admin', 'System', 'admin@colorrun.fr', 'admin123', TRUE)");
// // // Affecter le rôle ADMIN à l'utilisateur admin
// // stmt.execute("INSERT INTO utilisateur_role (id_utilisateur, id_role)
// VALUES "
// // +
// // "(1, 1)");
// // }
// // }

// @Override
// public void contextDestroyed(ServletContextEvent sce) {
// // Rien à faire lors de l'arrêt de l'application
// }
// }