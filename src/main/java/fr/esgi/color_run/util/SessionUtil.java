package fr.esgi.color_run.util;

import fr.esgi.color_run.business.Utilisateur;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.List;

/**
 * Utilitaire pour gérer les données de session
 */
public class SessionUtil {

    private static final String USER_KEY = "user";
    private static final String USER_ID_KEY = "userId";
    private static final String USER_ROLES_KEY = "userRoles";
    private static final int SESSION_TIMEOUT = 30 * 60; // 30 minutes en secondes

    /**
     * Crée une session pour l'utilisateur connecté avec ses rôles
     */
    public static void createUserSession(HttpServletRequest request, Utilisateur user, List<Role> roles) {
        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(SESSION_TIMEOUT);
        session.setAttribute(USER_KEY, user);
        session.setAttribute(USER_ID_KEY, user.getIdUtilisateur());

        // Stocker les noms des rôles
        List<String> roleNames = roles.stream().map(Role::getNomRole).toList();
        session.setAttribute(USER_ROLES_KEY, roleNames);
    }

    /**
     * Crée une session pour l'utilisateur connecté (sans rôles)
     */
    public static void createUserSession(HttpServletRequest request, Utilisateur user) {
        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(SESSION_TIMEOUT);
        session.setAttribute(USER_KEY, user);
        session.setAttribute(USER_ID_KEY, user.getIdUtilisateur());
    }

    /**
     * Récupère l'utilisateur de la session
     */
    public static Utilisateur getUserFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (Utilisateur) session.getAttribute(USER_KEY);
        }
        return null;
    }

    /**
     * Récupère l'ID de l'utilisateur de la session
     */
    public static Integer getUserIdFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (Integer) session.getAttribute(USER_ID_KEY);
        }
        return null;
    }

    /**
     * Récupère les rôles de l'utilisateur de la session
     */
    @SuppressWarnings("unchecked")
    public static List<String> getUserRolesFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (List<String>) session.getAttribute(USER_ROLES_KEY);
        }
        return List.of(); // Retourner une liste vide au lieu de null
    }

    /**
     * Vérifie si un utilisateur est connecté
     */
    public static boolean isUserLoggedIn(HttpServletRequest request) {
        return getUserFromSession(request) != null;
    }

    /**
     * Déconnecte l'utilisateur en invalidant la session
     */
    public static void logoutUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}