package fr.esgi.color_run.servlet;

import fr.esgi.color_run.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet pour gérer la déconnexion des utilisateurs
 */
@WebServlet(name = "logoutServlet", urlPatterns = { "/logout" })
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Déconnecter l'utilisateur en invalidant la session
        SessionUtil.logoutUser(request);

        // Rediriger vers la page d'accueil
        response.sendRedirect(request.getContextPath() + "/");
    }
}