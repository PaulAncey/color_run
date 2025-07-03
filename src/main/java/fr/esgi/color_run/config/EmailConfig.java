package fr.esgi.color_run.config;

/**
 * Configuration centralisée pour les emails
 */
public class EmailConfig {
    
    // Gmail (recommandé pour le développement)
    public static final String GMAIL_SMTP_HOST = "smtp.gmail.com";
    public static final String GMAIL_SMTP_PORT = "587";
    
    // Outlook/Hotmail
    public static final String OUTLOOK_SMTP_HOST = "smtp-mail.outlook.com";
    public static final String OUTLOOK_SMTP_PORT = "587";
    
    // Mailtrap (pour les tests - recommandé)
    public static final String MAILTRAP_SMTP_HOST = "smtp.mailtrap.io";
    public static final String MAILTRAP_SMTP_PORT = "587";
    
    // URL de base de l'application
    public static final String BASE_URL = System.getProperty("app.base.url", "http://localhost:8080/color-run");
    
    /**
     * Récupère la configuration email depuis les variables d'environnement
     */
    public static class Env {
        public static final String SMTP_HOST = System.getenv("SMTP_HOST");
        public static final String SMTP_PORT = System.getenv("SMTP_PORT");
        public static final String EMAIL_FROM = System.getenv("EMAIL_FROM");
        public static final String EMAIL_PASSWORD = System.getenv("EMAIL_PASSWORD");
    }
}