package fr.esgi.color_run.servlet;

import fr.esgi.color_run.business.Course;
import fr.esgi.color_run.config.ServiceFactory;
import fr.esgi.color_run.service.CourseService;
import fr.esgi.color_run.service.ServiceException;
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

/**
 * Servlet pour gérer l'affichage de la page d'accueil
 */
@WebServlet(name = "homeServlet", urlPatterns = { "", "/" })
public class HomeServlet extends HttpServlet {

    private CourseService courseService;

    @Override
    public void init() throws ServletException {
        // Initialiser les services
        courseService = ServiceFactory.getInstance().getCourseService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Récupérer les prochaines courses
            List<Course> upcomingCourses = courseService.trouverCoursesAVenir();

            // Limiter à 3 courses pour la page d'accueil
            if (upcomingCourses.size() > 3) {
                upcomingCourses = upcomingCourses.subList(0, 3);
            }

            // Préparer les données pour la vue
            Map<String, Object> variables = new HashMap<>();
            variables.put("upcomingCourses", upcomingCourses);

            // Traiter le template
            ThymeleafUtil.processTemplate("home", variables, request, response);

        } catch (ServiceException e) {
            // Gérer l'erreur
            Map<String, Object> variables = new HashMap<>();
            variables.put("error", "Une erreur est survenue: " + e.getMessage());
            ThymeleafUtil.processTemplate("error", variables, request, response);
        }
    }
}
