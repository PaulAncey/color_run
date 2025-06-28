package fr.esgi.color_run.servlet;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.esgi.color_run.business.Message;
import fr.esgi.color_run.config.ServiceFactory;
import fr.esgi.color_run.service.CourseService;
import fr.esgi.color_run.service.MessageService;
import fr.esgi.color_run.service.ServiceException;
import fr.esgi.color_run.util.SessionUtil;
import fr.esgi.color_run.util.ThymeleafUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet pour gérer les messages des fils de discussion des courses
 */
@WebServlet(name = "messageServlet", urlPatterns = { "/courses/*/messages" })
public class MessageServlet extends HttpServlet {

    private MessageService messageService;
    private CourseService courseService;

    @Override
    public void init() throws ServletException {
        // Initialiser les services avec ServiceFactory
        ServiceFactory serviceFactory = ServiceFactory.getInstance();
        messageService = serviceFactory.getMessageService();
        courseService = serviceFactory.getCourseService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Extraire l'ID de la course
        String pathInfo = request.getRequestURI();
        String[] pathParts = pathInfo.split("/");
        int courseId = Integer.parseInt(pathParts[pathParts.length - 2]);

        try {
            // Vérifier si la course existe
            if (!courseService.trouverParId(courseId).isPresent()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Course non trouvée");
                return;
            }

            // Récupérer les messages de la course
            List<Message> messages = messageService.trouverParCourse(courseId);

            // Si c'est une requête AJAX, renvoyer uniquement les messages
            String requestedWith = request.getHeader("X-Requested-With");
            if (requestedWith != null && requestedWith.equals("XMLHttpRequest")) {
                Map<String, Object> variables = new HashMap<>();
                variables.put("messages", messages);
                ThymeleafUtil.processTemplate("messages/messages-list", variables, request, response);
            } else {
                Map<String, Object> variables = new HashMap<>();
                variables.put("messages", messages);
                variables.put("courseId", courseId);
                ThymeleafUtil.processTemplate("messages/course-discussion", variables, request, response);
            }

        } catch (ServiceException e) {
            // Gérer l'erreur
            if (request.getHeader("X-Requested-With") != null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Erreur: " + e.getMessage());
            } else {
                Map<String, Object> variables = new HashMap<>();
                variables.put("error", "Une erreur est survenue : " + e.getMessage());
                ThymeleafUtil.processTemplate("error", variables, request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Vérifier si l'utilisateur est connecté
        if (!SessionUtil.isUserLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Extraire l'ID de la course
        String pathInfo = request.getRequestURI();
        String[] pathParts = pathInfo.split("/");
        int courseId = Integer.parseInt(pathParts[pathParts.length - 2]);

        // Récupérer les données du formulaire
        String contenu = request.getParameter("contenu");
        Integer userId = SessionUtil.getUserIdFromSession(request);

        try {
            // Validation des données
            if (contenu == null || contenu.trim().isEmpty()) {
                throw new ServiceException("Le contenu du message ne peut pas être vide");
            }

            // Vérifier si la course existe
            if (!courseService.trouverParId(courseId).isPresent()) {
                throw new ServiceException("Course non trouvée");
            }

            // Créer le message
            Message message = Message.builder()
                    .idCourse(courseId)
                    .idUtilisateur(userId)
                    .contenu(contenu)
                    .dateEnvoi(new Timestamp(System.currentTimeMillis()))
                    .build();

            messageService.creerMessage(message);

            // Si c'est une requête AJAX, renvoyer une réponse JSON
            String requestedWith = request.getHeader("X-Requested-With");
            if (requestedWith != null && requestedWith.equals("XMLHttpRequest")) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": true}");
            } else {
                // Sinon, rediriger vers la page de discussion
                response.sendRedirect(request.getContextPath() + "/courses/" + courseId + "/messages");
            }

        } catch (ServiceException e) {
            // Gérer l'erreur
            if (request.getHeader("X-Requested-With") != null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            } else {
                Map<String, Object> variables = new HashMap<>();
                variables.put("error", "Une erreur est survenue : " + e.getMessage());
                ThymeleafUtil.processTemplate("messages/course-discussion", variables, request, response);
            }
        }
    }
}