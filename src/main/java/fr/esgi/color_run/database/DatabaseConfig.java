package fr.esgi.color_run.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String JDBC_URL = "jdbc:h2:~/colorrun;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    static {
        try {
            // Charger le driver H2
            Class.forName("org.h2.Driver");
            System.out.println("Driver H2 chargé avec succès");
        } catch (ClassNotFoundException e) {
            System.err.println("Impossible de charger le driver H2: " + e.getMessage());
            throw new RuntimeException("Impossible de charger le driver H2", e);
        }
    }

    /**
     * Récupère une connexion à la base de données
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    public static void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
// package fr.esgi.color_run.database;

// import java.io.BufferedReader;
// import java.io.InputStream;
// import java.io.InputStreamReader;
// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.SQLException;
// import java.sql.Statement;
// import java.util.stream.Collectors;

// public class DatabaseConfig {
// // Configuration de la base de données H2
// private static final String JDBC_URL = "jdbc:h2:~/colorrun;AUTO_SERVER=TRUE";
// private static final String USER = "sa";
// private static final String PASSWORD = "";

// private static boolean initialized = false;

// static {
// try {
// // Charger le driver H2
// Class.forName("org.h2.Driver");
// } catch (ClassNotFoundException e) {
// throw new RuntimeException("Impossible de charger le driver H2", e);
// }
// }

// /**
// * Récupère une connexion à la base de données
// */
// public static Connection getConnection() throws SQLException {
// // Initialiser la base de données si ce n'est pas déjà fait
// if (!initialized) {
// initializeDatabase();
// initialized = true;
// }
// return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
// }

// /**
// * Initialise la base de données avec le schéma et les données initiales
// */
// private static void initializeDatabase() {
// try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
// Statement stmt = conn.createStatement()) {

// // Charger le script SQL depuis les ressources
// InputStream is =
// DatabaseConfig.class.getClassLoader().getResourceAsStream("database/schema.sql");
// if (is == null) {
// throw new RuntimeException("Le fichier schema.sql est introuvable");
// }

// String sql = new BufferedReader(new InputStreamReader(is))
// .lines()
// .collect(Collectors.joining("\n"));

// stmt.execute(sql);

// System.out.println("Base de données initialisée avec succès");

// } catch (SQLException e) {
// throw new RuntimeException("Erreur lors de l'initialisation de la base de
// données", e);
// }
// }

// public static void closeResources(AutoCloseable... resources) {
// for (AutoCloseable resource : resources) {
// if (resource != null) {
// try {
// resource.close();
// } catch (Exception e) {
// e.printStackTrace();
// }
// }
// }
// }
// }