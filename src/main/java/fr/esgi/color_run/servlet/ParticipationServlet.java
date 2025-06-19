package fr.esgi.color_run.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.esgi.color_run.business.Course;
import fr.esgi.color_run.business.Participation;
import fr.esgi.color_run.business.ParticipationWithCourse;
import fr.esgi.color_run.config.ServiceFactory;
import fr.esgi.color_run.service.CourseService;
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

/**
 * Servlet pour gérer les participations aux courses
 */
@WebServlet(name = "participationServlet", urlPatterns = {
    "/participations", 
    "/participations/register/*", 
    "/participations/cancel/*",
    "/participations/bib/*"
})
public class ParticipationServlet extends HttpServlet {

    private ParticipationService participationService;
    private CourseService courseService;
    private UtilisateurService utilisateurService;

    @Override
    public void init() throws ServletException {
        // Initialiser les services avec ServiceFactory
        ServiceFactory serviceFactory = ServiceFactory.getInstance();
        participationService = serviceFactory.getParticipationService();
        courseService = serviceFactory.getCourseService();
        utilisateurService = serviceFactory.getUtilisateurService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Vérifier si l'utilisateur est connecté
        if (!SessionUtil.isUserLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String pathInfo = request.getRequestURI();
        
        try {
            if (pathInfo.endsWith("/participations")) {
                // Liste des participations de l'utilisateur
                listUserParticipations(request, response);
            } else if (pathInfo.matches(".*/participations/bib/\\d+")) {
                // Téléchargement du dossard
                downloadBib(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (ServiceException e) {
            // Gérer l'erreur
            request.setAttribute("error", "Une erreur est survenue: " + e.getMessage());
            Map<String, Object> variables = new HashMap<>();
                variables.put("error", "Une erreur est survenue : " + e.getMessage());
                ThymeleafUtil.processTemplate("error", variables, request, response);
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
        
        String pathInfo = request.getRequestURI();
        
        try {
            if (pathInfo.matches(".*/participations/register/\\d+")) {
                // Inscription à une course
                registerToCourse(request, response);
            } else if (pathInfo.matches(".*/participations/cancel/\\d+")) {
                // Annulation d'une inscription
                cancelRegistration(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (ServiceException e) {
            // Gérer l'erreur
            request.setAttribute("error", "Une erreur est survenue: " + e.getMessage());
            Map<String, Object> variables = new HashMap<>();
                variables.put("error", "Une erreur est survenue : " + e.getMessage());
                ThymeleafUtil.processTemplate("error", variables, request, response);
        }
    }

    /**
     * Affiche la liste des participations de l'utilisateur
     */
    private void listUserParticipations(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ServiceException {
        
        Integer userId = SessionUtil.getUserIdFromSession(request);
        
        // Récupérer les participations de l'utilisateur avec les informations des courses
        try {
            List<ParticipationWithCourse> participations = participationService.trouverParUtilisateurAvecCourse(userId);

            Map<String, Object> variables = new HashMap<>();
            variables.put("participations", participations);
            ThymeleafUtil.processTemplate("participations/list", variables, request, response);
        } catch (ServiceException e) {
            throw new ServiceException("Erreur lors de la récupération des participations", e);
        }
    }

    /**
     * Inscrit l'utilisateur à une course
     */
    private void registerToCourse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ServiceException {
        
        Integer userId = SessionUtil.getUserIdFromSession(request);
        
        // Extraire l'ID de la course
        String pathInfo = request.getRequestURI();
        int courseId = Integer.parseInt(pathInfo.substring(pathInfo.lastIndexOf('/') + 1));
        
        try {
            // Vérifier si la course existe
            Optional<Course> course = courseService.trouverParId(courseId);
            if (!course.isPresent()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Course non trouvée");
                return;
            }
            
            // Vérifier si l'utilisateur est déjà inscrit
            if (participationService.trouverParUtilisateurEtCourse(userId, courseId).isPresent()) {
                response.sendRedirect(request.getContextPath() + "/courses/" + courseId + "?error=Vous êtes déjà inscrit à cette course");
                return;
            }
            
            // Vérifier si la course est complète
            if (participationService.courseEstComplete(courseId)) {
                response.sendRedirect(request.getContextPath() + "/courses/" + courseId + "?error=Cette course est complète");
                return;
            }
            
            // Inscrire l'utilisateur
            participationService.inscrire(userId, courseId);
            
            // Rediriger vers la course avec un message de succès
            response.sendRedirect(request.getContextPath() + "/courses/" + courseId + "?success=Inscription réussie ! Vous pouvez maintenant télécharger votre dossard.");
            
        } catch (ServiceException e) {
            response.sendRedirect(request.getContextPath() + "/courses/" + courseId + "?error=Erreur lors de l'inscription : " + e.getMessage());
        }
    }

    /**
     * Annule l'inscription de l'utilisateur à une course
     */
    private void cancelRegistration(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ServiceException {
        
        Integer userId = SessionUtil.getUserIdFromSession(request);
        
        // Extraire l'ID de la course
        String pathInfo = request.getRequestURI();
        int courseId = Integer.parseInt(pathInfo.substring(pathInfo.lastIndexOf('/') + 1));
        
        try {
            // Vérifier si l'utilisateur est inscrit
            if (!participationService.trouverParUtilisateurEtCourse(userId, courseId).isPresent()) {
                response.sendRedirect(request.getContextPath() + "/participations?error=Vous n'êtes pas inscrit à cette course");
                return;
            }
            
            // Annuler l'inscription
            participationService.annulerInscription(userId, courseId);
            
            // Rediriger vers la liste des participations avec message de succès
            response.sendRedirect(request.getContextPath() + "/participations?success=Inscription annulée avec succès");
            
        } catch (ServiceException e) {
            response.sendRedirect(request.getContextPath() + "/participations?error=Erreur lors de l'annulation : " + e.getMessage());
        }
    }

    /**
     * Télécharge le dossard de l'utilisateur
     */
    private void downloadBib(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ServiceException {
        
        // Extraire l'ID de la participation
        String pathInfo = request.getRequestURI();
        int participationId = Integer.parseInt(pathInfo.substring(pathInfo.lastIndexOf('/') + 1));
        
        try {
            // Récupérer la participation
            Optional<Participation> optParticipation = participationService.trouverParId(participationId);
            
            if (!optParticipation.isPresent()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Participation non trouvée");
                return;
            }
            
            Participation participation = optParticipation.get();
            
            // Vérifier si l'utilisateur a le droit de télécharger ce dossard
            Integer userId = SessionUtil.getUserIdFromSession(request);
            if (participation.getIdUtilisateur() != userId) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous n'avez pas accès à ce dossard");
                return;
            }
            
            // Générer le PDF du dossard
            byte[] pdfContent = participationService.genererDossardPDF(participationId);
            
            // Configurer la réponse HTTP pour télécharger le PDF
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"dossard_" + participation.getNumeroDossard() + ".pdf\"");
            response.setContentLength(pdfContent.length);
            
            // Écrire le contenu du PDF dans la réponse
            try (OutputStream out = response.getOutputStream()) {
                out.write(pdfContent);
            }
            
            // Marquer le dossard comme téléchargé
            participationService.marquerDossardTelecharge(participationId);
            
        } catch (ServiceException e) {
            throw new ServiceException("Erreur lors du téléchargement du dossard", e);
        }
    }
}