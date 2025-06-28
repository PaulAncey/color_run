package fr.esgi.color_run.servlet;

import fr.esgi.color_run.business.Role;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servlet pour gérer la connexion des utilisateurs
 */
@WebServlet(name = "loginServlet", urlPatterns = { "/login" })
public class LoginServlet extends HttpServlet {

    private UtilisateurService utilisateurService;

    @Override
    public void init() throws ServletException {
        // Initialiser le service utilisateur depuis la factory
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

        // Afficher le formulaire de connexion
        Map<String, Object> variables = new HashMap<>();
        ThymeleafUtil.processTemplate("login", variables, request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validation des données
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("error", "Email et mot de passe requis");
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

                    // Récupérer les rôles de l'utilisateur
                    List<Role> userRoles = utilisateurService.obtenirRolesUtilisateur(user.getIdUtilisateur());

                    // Créer la session utilisateur avec les rôles
                    SessionUtil.createUserSession(request, user, userRoles);

                    // Rediriger vers le tableau de bord
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                } else {
                    throw new ServiceException("Utilisateur introuvable malgré une authentification réussie");
                }
            } else {
                // Identifiants incorrects
                Map<String, Object> variables = new HashMap<>();
                variables.put("error", "Email ou mot de passe incorrect");
                ThymeleafUtil.processTemplate("login", variables, request, response);
            }

        } catch (ServiceException e) {
            // Gérer l'erreur
            Map<String, Object> variables = new HashMap<>();
            variables.put("error", "Une erreur est survenue lors de la connexion: " + e.getMessage());
            ThymeleafUtil.processTemplate("login", variables, request, response);
        }
    }
}