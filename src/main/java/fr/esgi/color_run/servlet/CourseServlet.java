package fr.esgi.color_run.servlet;

import fr.esgi.color_run.business.Course;
import fr.esgi.color_run.business.Message;
import fr.esgi.color_run.business.Participation;
import fr.esgi.color_run.config.ServiceFactory;
import fr.esgi.color_run.service.CourseService;
import fr.esgi.color_run.service.MessageService;
import fr.esgi.color_run.service.ParticipationService;
import fr.esgi.color_run.service.ServiceException;
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
 * Servlet pour gérer les courses (listing, détails, recherche)
 */
@WebServlet(name = "courseServlet", urlPatterns = {"/courses", "/courses/*"})
public class CourseServlet extends HttpServlet {

    private CourseService courseService;
    private ParticipationService participationService;
    private MessageService messageService;

    @Override
    public void init() throws ServletException {
        // Initialiser les services
        ServiceFactory serviceFactory = ServiceFactory.getInstance();
        courseService = serviceFactory.getCourseService();
        participationService = serviceFactory.getParticipationService();
        messageService = serviceFactory.getMessageService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Liste des courses avec filtres éventuels
                listCourses(request, response);
            } else if (pathInfo.matches("/\\d+")) {
                // Détails d'une course spécifique
                showCourseDetails(request, response);
            } else {
                // Gérer l'erreur 404
                Map<String, Object> variables = new HashMap<>();
                variables.put("error", "Page non trouvée");
                ThymeleafUtil.processTemplate("error", variables, request, response);
            }
        } catch (ServiceException e) {
            // Gérer l'erreur
            Map<String, Object> variables = new HashMap<>();
            variables.put("error", "Une erreur est survenue: " + e.getMessage());
            ThymeleafUtil.processTemplate("error", variables, request, response);
        }
    }

    /**
     * Affiche la liste des courses avec filtres éventuels
     */
    private void listCourses(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ServiceException {
        
        // Récupérer les paramètres de filtre
        String ville = request.getParameter("ville");
        Float minDistance = parseFloatParam(request.getParameter("minDistance"));
        Float maxDistance = parseFloatParam(request.getParameter("maxDistance"));
        Float maxPrix = parseFloatParam(request.getParameter("maxPrix"));
        Boolean avecObstacles = parseBooleanParam(request.getParameter("avecObstacles"));
        
        // Récupérer les courses selon les filtres
        List<Course> courses;
        if (ville != null || minDistance != null || maxDistance != null || maxPrix != null || avecObstacles != null) {
            courses = courseService.rechercherAvecFiltres(ville, minDistance, maxDistance, maxPrix, avecObstacles);
        } else {
            courses = courseService.trouverCoursesAVenir();
        }
        
        // Préparer les données pour la vue
        Map<String, Object> variables = new HashMap<>();
        variables.put("courses", courses);
        
        // Traiter le template
        ThymeleafUtil.processTemplate("courses/list", variables, request, response);
    }

    /**
     * Affiche les détails d'une course spécifique
     */
    private void showCourseDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ServiceException {
        
        // Extraire l'ID de la course
        String pathInfo = request.getPathInfo();
        int courseId = Integer.parseInt(pathInfo.substring(1));
        
        // Récupérer les détails de la course
        Optional<Course> optCourse = courseService.trouverParId(courseId);
        
        if (optCourse.isPresent()) {
            Course course = optCourse.get();
            
            // Récupérer le nombre d'inscrits
            int nbInscrits = participationService.compterParticipants(courseId);
            boolean estComplete = participationService.courseEstComplete(courseId);
            
            // Récupérer les messages
            List<Message> messages = messageService.trouverParCourse(courseId);
            
            // Vérifier si l'utilisateur est connecté
            boolean isOrganisateur = false;
            boolean isInscrit = false;
            Participation participation = null;
            
            Integer userId = SessionUtil.getUserIdFromSession(request);
            if (userId != null) {
                // Vérifier si l'utilisateur est l'organisateur
                isOrganisateur = courseService.estOrganisateur(userId, courseId);
                
                // Vérifier si l'utilisateur est inscrit
                Optional<Participation> optParticipation = participationService.trouverParUtilisateurEtCourse(userId, courseId);
                isInscrit = optParticipation.isPresent();
                if (isInscrit) {
                    participation = optParticipation.get();
                }
            }
            
            // Préparer les données pour la vue
            Map<String, Object> variables = new HashMap<>();
            variables.put("course", course);
            variables.put("nbInscrits", nbInscrits);
            variables.put("estComplete", estComplete);
            variables.put("messages", messages);
            variables.put("isOrganisateur", isOrganisateur);
            variables.put("isInscrit", isInscrit);
            variables.put("participation", participation);
            variables.put("error", request.getParameter("error"));
            variables.put("success", request.getParameter("success"));
            
            // Traiter le template
            ThymeleafUtil.processTemplate("courses/details", variables, request, response);
        } else {
            // Course non trouvée
            Map<String, Object> variables = new HashMap<>();
            variables.put("error", "Course non trouvée");
            ThymeleafUtil.processTemplate("error", variables, request, response);
        }
    }

    /**
     * Convertit un paramètre en Float
     */
    private Float parseFloatParam(String param) {
        if (param == null || param.trim().isEmpty()) {
            return null;
        }
        try {
            return Float.parseFloat(param);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Convertit un paramètre en Boolean
     */
    private Boolean parseBooleanParam(String param) {
        if (param == null || param.trim().isEmpty()) {
            return null;
        }
        return Boolean.parseBoolean(param);
    }
}