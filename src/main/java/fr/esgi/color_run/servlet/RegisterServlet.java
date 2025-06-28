package fr.esgi.color_run.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fr.esgi.color_run.business.Utilisateur;
import fr.esgi.color_run.dao.impl.RoleDAOImpl;
import fr.esgi.color_run.dao.impl.UtilisateurDAOImpl;
import fr.esgi.color_run.dao.impl.UtilisateurRoleDAOImpl;
import fr.esgi.color_run.service.ServiceException;
import fr.esgi.color_run.service.UtilisateurService;
import fr.esgi.color_run.service.impl.UtilisateurServiceImpl;
import fr.esgi.color_run.util.SessionUtil;
import fr.esgi.color_run.util.ThymeleafUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet pour gérer l'inscription des utilisateurs
 */
@WebServlet(name = "registerServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private UtilisateurService utilisateurService;

    @Override
    public void init() throws ServletException {
        // Initialiser le service utilisateur
        utilisateurService = new UtilisateurServiceImpl(
                new UtilisateurDAOImpl(),
                new RoleDAOImpl(),
                new UtilisateurRoleDAOImpl()
        );
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Vérifier si l'utilisateur est déjà connecté
        if (SessionUtil.isUserLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        // Afficher le formulaire d'inscription
        Map<String, Object> variables = new HashMap<>();
        variables.put("error", "Une erreur est survenue");
        ThymeleafUtil.processTemplate("register", variables, request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Récupérer les données du formulaire
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Validation des données
        if (nom == null || nom.trim().isEmpty() ||
            prenom == null || prenom.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            
            request.setAttribute("error", "Tous les champs sont obligatoires");
    Map<String, Object> variables = new HashMap<>();
        variables.put("error", "Une erreur est survenue");
        ThymeleafUtil.processTemplate("register", variables, request, response);            return;
        }
        
        // Vérifier que les mots de passe correspondent
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Les mots de passe ne correspondent pas");
    Map<String, Object> variables = new HashMap<>();
        variables.put("error", "Une erreur est survenue");
        ThymeleafUtil.processTemplate("register", variables, request, response);            return;
        }
        
        try {
            // Vérifier si l'email existe déjà
            if (utilisateurService.trouverParEmail(email).isPresent()) {
                request.setAttribute("error", "Cet email est déjà utilisé");
        Map<String, Object> variables = new HashMap<>();
        variables.put("error", "Une erreur est survenue");
        ThymeleafUtil.processTemplate("register", variables, request, response);                return;
            }
            
            // Créer le nouvel utilisateur
            Utilisateur newUser = Utilisateur.builder()
                    .nom(nom)
                    .prenom(prenom)
                    .email(email)
                    .motDePasse(password) // Le service se chargera de sécuriser le mot de passe
                    .compteVerifie(false) // Par défaut, le compte n'est pas vérifié
                    .build();
            
            // Enregistrer l'utilisateur
            Utilisateur createdUser = utilisateurService.creerUtilisateur(newUser);
            
            // Créer la session utilisateur
            SessionUtil.createUserSession(request, createdUser);
            
            // Rediriger vers le tableau de bord
            response.sendRedirect(request.getContextPath() + "/dashboard");
            
        } catch (ServiceException e) {
            // Gérer l'erreur
            request.setAttribute("error", "Une erreur est survenue lors de l'inscription: " + e.getMessage());
    Map<String, Object> variables = new HashMap<>();
        variables.put("error", "Une erreur est survenue");
        ThymeleafUtil.processTemplate("register", variables, request, response);        }
    }
}