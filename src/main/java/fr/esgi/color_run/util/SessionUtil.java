package fr.esgi.color_run.util;

import fr.esgi.color_run.business.Utilisateur;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Utilitaire pour gérer les données de session
 */
public class SessionUtil {

    private static final String USER_KEY = "user";
    private static final String USER_ID_KEY = "userId";
    private static final int SESSION_TIMEOUT = 30 * 60; // 30 minutes en secondes

    /**
     * Crée une session pour l'utilisateur connecté
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