package fr.esgi.color_run.servlet;

import fr.esgi.color_run.business.Course;
import fr.esgi.color_run.business.Participation;
import fr.esgi.color_run.config.ServiceFactory;
import fr.esgi.color_run.service.CourseService;
import fr.esgi.color_run.service.DemandeOrganisateurService;
import fr.esgi.color_run.service.ParticipationService;
import fr.esgi.color_run.service.ServiceException;
import fr.esgi.color_run.service.UtilisateurService;
import fr.esgi.color_run.util.SessionUtil;
import fr.esgi.color_run.util.ThymeleafUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet pour afficher le tableau de bord de l'utilisateur connecté
 */
@WebServlet(name = "dashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    private UtilisateurService utilisateurService;
    private CourseService courseService;
    private ParticipationService participationService;
    private DemandeOrganisateurService demandeOrganisateurService;

    @Override
    public void init() throws ServletException {
        // Initialiser les services
        ServiceFactory serviceFactory = ServiceFactory.getInstance();
        utilisateurService = serviceFactory.getUtilisateurService();
        courseService = serviceFactory.getCourseService();
        participationService = serviceFactory.getParticipationService();
        demandeOrganisateurService = serviceFactory.getDemandeOrganisateurService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Vérifier si l'utilisateur est connecté
        if (!SessionUtil.isUserLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            // Récupérer l'ID de l'utilisateur connecté
            Integer userId = SessionUtil.getUserIdFromSession(request);
            
            // Récupérer les rôles de l'utilisateur
            List<String> userRoles = utilisateurService.obtenirRolesUtilisateur(userId)
                    .stream()
                    .map(role -> role.getNomRole())
                    .toList();
            
            // Récupérer les participations de l'utilisateur
            List<Participation> participations = participationService.trouverParUtilisateur(userId);
            
            // Récupérer les courses organisées si l'utilisateur est ORGANISATEUR
            List<Course> coursesOrganisees = List.of(); // Liste vide par défaut
            if (userRoles.contains("ORGANISATEUR")) {
                coursesOrganisees = courseService.trouverParOrganisateur(userId);
            }
            
            // Vérifier si l'utilisateur peut demander à devenir organisateur
            boolean canRequestOrganizerRole = !userRoles.contains("ORGANISATEUR") && 
                    demandeOrganisateurService.peutDemanderRole(userId);
            
            // Préparer les données pour la vue
            Map<String, Object> variables = new HashMap<>();
            variables.put("userRoles", userRoles);
            variables.put("participations", participations);
            variables.put("nbParticipations", participations.size());
            variables.put("coursesOrganisees", coursesOrganisees);
            variables.put("nbCoursesOrganisees", coursesOrganisees.size());
            variables.put("canRequestOrganizerRole", canRequestOrganizerRole);
            variables.put("success", request.getParameter("success")); // Message de succès éventuel
            
            // Traiter le template
            ThymeleafUtil.processTemplate("dashboard", variables, request, response);
            
        } catch (ServiceException e) {
            // Gérer l'erreur
            Map<String, Object> variables = new HashMap<>();
            variables.put("error", "Une erreur est survenue: " + e.getMessage());
            ThymeleafUtil.processTemplate("error", variables, request, response);
        }
    }
}