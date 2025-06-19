package fr.esgi.color_run.util;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Utilitaire pour gérer Thymeleaf
 */
public class ThymeleafUtil {

    private static TemplateEngine templateEngine;
    private static boolean initialized = false;

    /**
     * Initialise le moteur de templates Thymeleaf
     */
    public static void init(ServletContext servletContext) {
        if (!initialized) {
            // Création du resolver de templates
            ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
            templateResolver.setTemplateMode(TemplateMode.HTML);
            templateResolver.setPrefix("/WEB-INF/templates/");
            templateResolver.setSuffix(".html");
            templateResolver.setCacheTTLMs(3600000L); // 1 heure de cache
            templateResolver.setCacheable(true);

            // Création du moteur de templates
            templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolver(templateResolver);

            initialized = true;
        }
    }

    /**
     * Traite un template et envoie la réponse
     */
    public static void processTemplate(String templateName, Map<String, Object> variables,
                                       HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Vérifier que le moteur est initialisé
        if (!initialized) {
            throw new IllegalStateException("ThymeleafUtil n'est pas initialisé. Appelez init() d'abord.");
        }

        // Créer le contexte Thymeleaf
        Context context = new Context();

        // Ajouter les variables
        if (variables != null) {
            context.setVariables(variables);
        }

        // Ajouter des variables utiles
        context.setVariable("request", request);
        context.setVariable("session", request.getSession());

        // Configurer la réponse
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            // Traiter le template et écrire dans la réponse
            templateEngine.process(templateName, context, response.getWriter());
        } catch (Exception e) {
            // En cas d'erreur, afficher une page d'erreur simple
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("<h1>Erreur de template</h1>");
            response.getWriter().write("<p>Impossible de charger le template: " + templateName + "</p>");
            response.getWriter().write("<p>Erreur: " + e.getMessage() + "</p>");
            e.printStackTrace();
        }
    }

    /**
     * Traite un template et retourne le HTML comme String
     */
    public static String processTemplateToString(String templateName, Map<String, Object> variables) {
        if (!initialized) {
            throw new IllegalStateException("ThymeleafUtil n'est pas initialisé. Appelez init() d'abord.");
        }

        Context context = new Context();
        if (variables != null) {
            context.setVariables(variables);
        }

        return templateEngine.process(templateName, context);
    }

    /**
     * Vérifie si le moteur est initialisé
     */
    public static boolean isInitialized() {
        return initialized;
    }
}