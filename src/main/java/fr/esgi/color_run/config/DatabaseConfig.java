package fr.esgi.color_run.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseConfig {
    // Configuration de la base de données H2
    private static final String DB_NAME = "colorrun";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    
    // Construire l'URL avec le chemin absolu
    private static final String JDBC_URL = "jdbc:h2:" + getTempDbPath() + ";AUTO_SERVER=TRUE";
    
    private static boolean initialized = false;

    /**
     * Obtient le chemin absolu pour la base de données dans le répertoire temporaire
     */
    private static String getTempDbPath() {
        String tempDir = System.getProperty("java.io.tmpdir");
        String dbPath = new File(tempDir, DB_NAME).getAbsolutePath();
        System.out.println("Chemin de la base de données: " + dbPath);
        return dbPath;
    }

    static {
        try {
            // Charger le driver H2
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Impossible de charger le driver H2", e);
        }
    }

    /**
     * Récupère une connexion à la base de données
     */
    public static Connection getConnection() throws SQLException {
        // Initialiser la base de données si ce n'est pas déjà fait
        if (!initialized) {
            initializeDatabase();
            initialized = true;
        }
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    /**
     * Initialise la base de données avec le schéma et les données initiales
     */
    private static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // Charger le script SQL depuis les ressources
            InputStream is = DatabaseConfig.class.getClassLoader().getResourceAsStream("database/schema.sql");
            if (is == null) {
                throw new RuntimeException("Le fichier schema.sql est introuvable à: database/schema.sql");
            }
            
            String sql = new BufferedReader(new InputStreamReader(is))
                    .lines()
                    .collect(Collectors.joining("\n"));
            
            stmt.execute(sql);
            System.out.println("Base de données initialisée avec succès");
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'initialisation de la base de données: " + e.getMessage(), e);
        }
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