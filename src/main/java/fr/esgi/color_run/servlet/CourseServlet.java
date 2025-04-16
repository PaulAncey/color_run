package fr.esgi.color_run.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.esgi.color_run.business.Course;
import fr.esgi.color_run.service.CourseService;
import fr.esgi.color_run.service.ServiceException;
import fr.esgi.color_run.util.SessionUtil;
import fr.esgi.color_run.util.ThymeleafUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet pour gérer les courses (listing, détails, recherche)
 */
@WebServlet(name = "courseServlet", urlPatterns = {"/courses", "/courses/*"})
public class CourseServlet extends HttpServlet {

    private CourseService courseService;

    @Override
    public void init() throws ServletException {
        // Initialiser le service course
        // Dans une application réelle, utiliser l'injection de dépendances
        // courseService = new CourseServiceImpl(new CourseDAOImpl());
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
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (ServiceException e) {
            // Gérer l'erreur
            request.setAttribute("error", "Une erreur est survenue: " + e.getMessage());
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
        
        // Passer les données à la vue
        request.setAttribute("courses", courses);
        Map<String, Object> variables = new HashMap<>();
        variables.put("error", "Une erreur est survenue");
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
        Optional<Course> course = courseService.trouverParId(courseId);
        
        if (course.isPresent()) {
            request.setAttribute("course", course.get());
            
            // Vérifier si l'utilisateur est connecté
            Integer userId = SessionUtil.getUserIdFromSession(request);
            if (userId != null) {
                // Vérifier si l'utilisateur est l'organisateur
                boolean isOrganisateur = courseService.estOrganisateur(userId, courseId);
                request.setAttribute("isOrganisateur", isOrganisateur);
                
                // D'autres vérifications pourraient être ajoutées ici
            }
            
            Map<String, Object> variables = new HashMap<>();
            variables.put("error", "Une erreur est survenue");
            ThymeleafUtil.processTemplate("courses/details", variables, request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Course non trouvée");
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