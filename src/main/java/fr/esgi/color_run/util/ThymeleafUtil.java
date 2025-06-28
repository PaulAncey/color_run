package fr.esgi.color_run.util;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * Utilitaire pour gérer Thymeleaf
 */
public class ThymeleafUtil {

    private static TemplateEngine templateEngine;
    private static boolean initialized = false;
    private static JakartaServletWebApplication jakartaServletWebApplication;

    /**
     * Initialise le moteur de templates Thymeleaf
     */
    public static void init(ServletContext servletContext) {
        if (!initialized) {
            jakartaServletWebApplication = JakartaServletWebApplication.buildApplication(servletContext);

            WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(
                    jakartaServletWebApplication);
            templateResolver.setTemplateMode(TemplateMode.HTML);
            templateResolver.setPrefix("/WEB-INF/templates/");
            templateResolver.setSuffix(".html");
            templateResolver.setCacheTTLMs(3600000L); // 1 hour cache

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

        // Ajouter des variables utiles pour les templates
        context.setVariable("request", request);
        context.setVariable("contextPath", request.getContextPath());

        // Ajouter les paramètres de requête pour OGNL (convertir String[] en String)
        Map<String, String> simpleParams = new java.util.HashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String[] values = entry.getValue();
            if (values != null && values.length > 0) {
                simpleParams.put(entry.getKey(), values[0]);
            }
        }
        context.setVariable("param", simpleParams);

        // Ajouter un helper pour les URLs relatives au contexte
        String contextPath = request.getContextPath();
        context.setVariable("contextUrl", (contextPath != null && !contextPath.isEmpty()) ? contextPath : "");

        // Extraire les attributs de session et les passer comme variables
        HttpSession session = request.getSession();
        context.setVariable("session", session);

        // Ajouter les attributs de session comme variables directes
        Object user = session.getAttribute("user");
        Object userRoles = session.getAttribute("userRoles");

        context.setVariable("sessionUser", user);
        context.setVariable("sessionUserRoles", userRoles);

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