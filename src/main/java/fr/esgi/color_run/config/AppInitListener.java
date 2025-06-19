package fr.esgi.color_run.config;

import fr.esgi.color_run.util.ThymeleafUtil;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Listener pour initialiser l'application au démarrage
 */
@WebListener
public class AppInitListener implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        
        // Initialiser Thymeleaf
        ThymeleafUtil.init(servletContext);
        
        // Initialiser d'autres composants si nécessaire
        // ...
        
        // Afficher un message au démarrage
        servletContext.log("Application Color Run démarrée avec succès !");
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nettoyage des ressources si nécessaire
        sce.getServletContext().log("Application Color Run arrêtée.");
    }
}