package fr.esgi.color_run.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseConfig {
    // Configuration de la base de données H2
    private static final String DB_NAME = "colorrun";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    
    // Construire l'URL avec le chemin absolu dans le répertoire temporaire
    private static final String JDBC_URL = buildJdbcUrl();
    
    private static boolean initialized = false;

    /**
     * Construit l'URL JDBC avec un chemin sûr
     */
    private static String buildJdbcUrl() {
        // Option 1: Base de données en mémoire (recommandée pour les tests)
        // return "jdbc:h2:mem:colorrun;DB_CLOSE_DELAY=-1";
        
        // Option 2: Base de données fichier dans le répertoire temporaire
        String tempDir = System.getProperty("java.io.tmpdir");
        String dbPath = new File(tempDir, DB_NAME).getAbsolutePath();
        System.out.println("Chemin de la base de données H2: " + dbPath);
        return "jdbc:h2:file:" + dbPath + ";AUTO_SERVER=TRUE";
    }

    static {
        try {
            // Charger le driver H2
            Class.forName("org.h2.Driver");
            System.out.println("Driver H2 chargé avec succès");
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
            
            System.out.println("Tentative de connexion à H2 avec URL: " + JDBC_URL);
            
            // Vérifier si la base de données est déjà initialisée
            if (isDatabaseInitialized(stmt)) {
                System.out.println("Base de données déjà initialisée");
                return;
            }
            
            // Charger le script SQL depuis les ressources
            InputStream is = DatabaseConfig.class.getClassLoader().getResourceAsStream("database/schema.sql");
            if (is == null) {
                System.err.println("ATTENTION: Le fichier schema.sql est introuvable");
                return;
            }
            
            String sql = new BufferedReader(new InputStreamReader(is))
                    .lines()
                    .collect(Collectors.joining("\n"));
            
            // Diviser le script en commandes individuelles et les exécuter une par une
            String[] commands = sql.split(";");
            for (String command : commands) {
                command = command.trim();
                if (!command.isEmpty()) {
                    try {
                        stmt.execute(command);
                    } catch (SQLException e) {
                        // Ignorer les erreurs de contraintes uniques (données déjà présentes)
                        if (!e.getMessage().contains("Unique index or primary key violation")) {
                            System.err.println("Erreur lors de l'exécution de la commande: " + command);
                            throw e;
                        }
                    }
                }
            }
            
            System.out.println("Base de données initialisée avec succès");
            
        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'initialisation de la base de données: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Erreur générale: " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'initialisation de la base de données", e);
        }
    }
    
    /**
     * Vérifie si la base de données est déjà initialisée en cherchant la table Role
     */
    private static boolean isDatabaseInitialized(Statement stmt) {
        try {
            // Vérifier si la table Role existe et contient des données
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Role");
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            // Si la table n'existe pas, elle n'est pas initialisée
            return false;
        }
        return false;
    }
    
    /**
     * Ferme une connexion, un statement et un resultset de manière sécurisée
     */
    public static void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    // Log l'erreur mais continue
                    e.printStackTrace();
                }
            }
        }
    }
}