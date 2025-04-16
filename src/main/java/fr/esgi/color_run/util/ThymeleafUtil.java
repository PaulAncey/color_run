package fr.esgi.color_run.util;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.util.Map;

/**
 * Utilitaire pour gérer le moteur de templates Thymeleaf
 */
public class ThymeleafUtil {
    
    private static TemplateEngine templateEngine;
    private static JakartaServletWebApplication jakartaServletWebApplication;
    
    /**
     * Initialise le moteur de templates Thymeleaf
     */
    public static void init(ServletContext servletContext) {
        jakartaServletWebApplication = JakartaServletWebApplication.buildApplication(servletContext);
        
        WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(jakartaServletWebApplication);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheTTLMs(3600000L); // 1 heure de cache
        
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }
    
    /**
     * Récupère l'instance du moteur de templates
     */
    public static TemplateEngine getTemplateEngine() {
        return templateEngine;
    }
    
    /**
     * Traite le template et envoie la réponse HTTP
     */
    public static void processTemplate(String templateName, Map<String, Object> variables, 
                                      HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        WebContext context = new WebContext(
                jakartaServletWebApplication.buildExchange(request, response),
                request.getLocale());
        
        // Ajouter les variables au contexte
        if (variables != null) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                context.setVariable(entry.getKey(), entry.getValue());
            }
        }
        
        // Définir le type de contenu
        response.setContentType("text/html;charset=UTF-8");
        
        // Traiter le template et envoyer la réponse
        templateEngine.process(templateName, context, response.getWriter());
    }
}