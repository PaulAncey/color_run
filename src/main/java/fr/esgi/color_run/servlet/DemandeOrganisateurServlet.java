package fr.esgi.color_run.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.esgi.color_run.business.DemandeOrganisateur;
import fr.esgi.color_run.config.ServiceFactory;
import fr.esgi.color_run.service.DemandeOrganisateurService;
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
 * Servlet pour gérer les demandes de rôle organisateur
 */
@WebServlet(name = "demandeOrganisateurServlet", urlPatterns = {
    "/user/request-organizer", 
    "/admin/organizer-requests",
    "/admin/organizer-requests/*"
})
public class DemandeOrganisateurServlet extends HttpServlet {

    private DemandeOrganisateurService demandeOrganisateurService;
    private UtilisateurService utilisateurService;

    @Override
    public void init() throws ServletException {
        // Initialiser les services avec ServiceFactory
        ServiceFactory serviceFactory = ServiceFactory.getInstance();
        demandeOrganisateurService = serviceFactory.getDemandeOrganisateurService();
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
            if (pathInfo.endsWith("/user/request-organizer")) {
                // Afficher le formulaire de demande pour devenir organisateur
                showRequestForm(request, response);
            } else if (pathInfo.endsWith("/admin/organizer-requests")) {
                // Afficher la liste des demandes pour l'administrateur
                listRequests(request, response);
            } else if (pathInfo.matches(".*/admin/organizer-requests/\\d+")) {
                // Afficher les détails d'une demande spécifique
                showRequestDetails(request, response);
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
            if (pathInfo.endsWith("/user/request-organizer")) {
                // Soumettre une demande pour devenir organisateur
                submitRequest(request, response);
            } else if (pathInfo.matches(".*/admin/organizer-requests/\\d+")) {
                // Traiter une demande (accepter/refuser)
                processRequest(request, response);
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
     * Affiche le formulaire de demande pour devenir organisateur
     */
    private void showRequestForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ServiceException {
        
        Integer userId = SessionUtil.getUserIdFromSession(request);
        
        // Vérifier si l'utilisateur peut demander le rôle d'organisateur
        if (!demandeOrganisateurService.peutDemanderRole(userId)) {
            request.setAttribute("error", "Vous ne pouvez pas demander le rôle d'organisateur (demande en cours ou déjà organisateur)");

            Map<String, Object> variables = new HashMap<>();
            variables.put("error", "Une erreur est survenue");
            ThymeleafUtil.processTemplate("user/profile", variables, request, response);
            return;
        }
        
        Map<String, Object> variables = new HashMap<>();
        ThymeleafUtil.processTemplate("user/request-organizer", variables, request, response);
    }

    /**
     * Soumet une demande pour devenir organisateur
     */
    private void submitRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ServiceException {
        
        Integer userId = SessionUtil.getUserIdFromSession(request);
        String motif = request.getParameter("motif");
        
        // Validation des données
        if (motif == null || motif.trim().isEmpty()) {
            request.setAttribute("error", "Le motif de la demande est obligatoire");

            Map<String, Object> variables = new HashMap<>();
            variables.put("error", "Le motif de la demande est obligatoire");
            ThymeleafUtil.processTemplate("user/request-organizer", variables, request, response);
            return;
        }
        
        // Vérifier si l'utilisateur peut demander le rôle d'organisateur
        if (!demandeOrganisateurService.peutDemanderRole(userId)) {
            request.setAttribute("error", "Vous ne pouvez pas demander le rôle d'organisateur (demande en cours ou déjà organisateur)");
            Map<String, Object> variables = new HashMap<>();
            variables.put("error", "Vous ne pouvez pas demander le rôle d'organisateur");
            ThymeleafUtil.processTemplate("user/profile", variables, request, response);
            return;
        }
        
        // Créer la demande
        DemandeOrganisateur demande = DemandeOrganisateur.builder()
                .idUtilisateur(userId)
                .motif(motif)
                .statut("EN_ATTENTE")
                .build();
        
        demandeOrganisateurService.creerDemande(demande);
        
        // Rediriger vers le profil avec un message de succès
        response.sendRedirect(request.getContextPath() + "/dashboard?success=Votre demande a été soumise avec succès");
    }

    /**
     * Affiche la liste des demandes pour l'administrateur
     */
    private void listRequests(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ServiceException {
        
        // Récupérer les demandes en attente par défaut
        String statut = request.getParameter("statut");
        List<DemandeOrganisateur> demandes;
        
        if (statut != null && !statut.trim().isEmpty()) {
            demandes = demandeOrganisateurService.trouverParStatut(statut);
        } else {
            demandes = demandeOrganisateurService.trouverParStatut("EN_ATTENTE");
        }
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("demandes", demandes);
        ThymeleafUtil.processTemplate("admin/organizer-requests", variables, request, response);
    }

    /**
     * Affiche les détails d'une demande spécifique
     */
    private void showRequestDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ServiceException {
        
        // Extraire l'ID de la demande
        String pathInfo = request.getRequestURI();
        int demandeId = Integer.parseInt(pathInfo.substring(pathInfo.lastIndexOf('/') + 1));
        
        // Récupérer les détails de la demande
        Optional<DemandeOrganisateur> demande = demandeOrganisateurService.trouverParId(demandeId);
        
        if (demande.isPresent()) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("demande", demande.get());
            ThymeleafUtil.processTemplate("admin/organizer-request-details", variables, request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Demande non trouvée");
        }
    }

    /**
     * Traite une demande (accepter/refuser)
     */
    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ServiceException {
        
        // Extraire l'ID de la demande
        String pathInfo = request.getRequestURI();
        int demandeId = Integer.parseInt(pathInfo.substring(pathInfo.lastIndexOf('/') + 1));
        
        String action = request.getParameter("action");
        String commentaire = request.getParameter("commentaire");
        
        // Validation des données
        if (action == null || (!action.equals("ACCEPTEE") && !action.equals("REFUSEE"))) {
            request.setAttribute("error", "Action invalide");
            showRequestDetails(request, response);
            return;
        }
        
        // Traiter la demande
        demandeOrganisateurService.traiterDemande(demandeId, action, commentaire);
        
        // Si la demande est acceptée, attribuer le rôle d'organisateur
        if (action.equals("ACCEPTEE")) {
            Optional<DemandeOrganisateur> demande = demandeOrganisateurService.trouverParId(demandeId);
            if (demande.isPresent()) {
                utilisateurService.attribuerRole(demande.get().getIdUtilisateur(), "ORGANISATEUR");
            }
        }
        
        // Rediriger vers la liste des demandes avec un message de succès
        response.sendRedirect(request.getContextPath() + "/admin/organizer-requests?success=La demande a été traitée avec succès");
    }
}