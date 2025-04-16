package fr.esgi.color_run.servlet;

import fr.esgi.color_run.business.Utilisateur;
import fr.esgi.color_run.config.ServiceFactory;
import fr.esgi.color_run.service.ServiceException;
import fr.esgi.color_run.service.UtilisateurService;
import fr.esgi.color_run.util.SessionUtil;
import fr.esgi.color_run.util.ThymeleafUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Servlet pour gérer la connexion des utilisateurs (avec Thymeleaf)
 */
@WebServlet(name = "loginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private UtilisateurService utilisateurService;

    @Override
    public void init() throws ServletException {
        // Initialiser le service utilisateur
        utilisateurService = ServiceFactory.getInstance().getUtilisateurService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Vérifier si l'utilisateur est déjà connecté
        if (SessionUtil.isUserLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Préparer les variables pour le template
        Map<String, Object> variables = new HashMap<>();
        variables.put("pageTitle", "Connexion - Color Run");
        
        // Traiter le template et envoyer la réponse
        ThymeleafUtil.processTemplate("login", variables, request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        // Validation des données
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            // Préparer les variables pour le template
            Map<String, Object> variables = new HashMap<>();
            variables.put("pageTitle", "Connexion - Color Run");
            variables.put("error", "Email et mot de passe requis");
            
            // Traiter le template et envoyer la réponse
            ThymeleafUtil.processTemplate("login", variables, request, response);
            return;
        }
        
        try {
            // Vérifier les identifiants
            boolean authenticated = utilisateurService.authentifier(email, password);
            
            if (authenticated) {
                // Récupérer les informations de l'utilisateur
                Optional<Utilisateur> optUser = utilisateurService.trouverParEmail(email);
                
                if (optUser.isPresent()) {
                    Utilisateur user = optUser.get();
                    
                    // Créer la session utilisateur
                    SessionUtil.createUserSession(request, user);
                    
                    // Rediriger vers le tableau de bord
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                } else {
                    throw new ServiceException("Utilisateur introuvable malgré une authentification réussie");
                }
            } else {
                // Identifiants incorrects
                Map<String, Object> variables = new HashMap<>();
                variables.put("pageTitle", "Connexion - Color Run");
                variables.put("error", "Email ou mot de passe incorrect");
                
                // Traiter le template et envoyer la réponse
                ThymeleafUtil.processTemplate("login", variables, request, response);
            }
            
        } catch (ServiceException e) {
            // Gérer l'erreur
            Map<String, Object> variables = new HashMap<>();
            variables.put("pageTitle", "Connexion - Color Run");
            variables.put("error", "Une erreur est survenue lors de la connexion: " + e.getMessage());
            
            // Traiter le template et envoyer la réponse
            ThymeleafUtil.processTemplate("login", variables, request, response);
        }
    }
}