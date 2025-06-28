package fr.esgi.color_run.filter;

import java.io.IOException;

import fr.esgi.color_run.business.Utilisateur;
import fr.esgi.color_run.service.ServiceException;
import fr.esgi.color_run.service.UtilisateurService;
import fr.esgi.color_run.util.SessionUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtre pour vérifier l'authentification et les autorisations
 */
@WebFilter(urlPatterns = { "/secured/*" })
public class AuthenticationFilter implements Filter {

    private UtilisateurService utilisateurService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialiser le service utilisateur (dans une application réelle, utiliser
        // l'injection de dépendances)
        // utilisateurService = new UtilisateurServiceImpl(new UtilisateurDAOImpl(), new
        // RoleDAOImpl(), new UtilisateurRoleDAOImpl());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();

        // Vérifier si l'utilisateur est connecté
        if (!SessionUtil.isUserLoggedIn(httpRequest)) {
            // Rediriger vers la page de connexion
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        // Vérifier les autorisations selon l'URI
        Utilisateur user = SessionUtil.getUserFromSession(httpRequest);

        try {
            // Vérifier les rôles pour les sections admin
            if (requestURI.contains("/admin/") && !hasRole(user, "ADMIN")) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé");
                return;
            }

            // Vérifier les rôles pour les sections organisateur
            if (requestURI.contains("/organisateur/") && !hasRole(user, "ORGANISATEUR") && !hasRole(user, "ADMIN")) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé");
                return;
            }

            // Continuer le traitement si tout est OK
            chain.doFilter(request, response);

        } catch (ServiceException e) {
            throw new ServletException("Erreur lors de la vérification des autorisations", e);
        }
    }

    @Override
    public void destroy() {
        // Nettoyage des ressources si nécessaire
    }

    /**
     * Vérifie si l'utilisateur a un rôle spécifique
     */
    private boolean hasRole(Utilisateur user, String roleName) throws ServiceException {
        return utilisateurService.aRole(user.getIdUtilisateur(), roleName);
    }
}