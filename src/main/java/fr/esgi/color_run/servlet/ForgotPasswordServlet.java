package fr.esgi.color_run.servlet;

import fr.esgi.color_run.config.ServiceFactory;
import fr.esgi.color_run.service.EmailService;
import fr.esgi.color_run.service.ServiceException;
import fr.esgi.color_run.service.UtilisateurService;
import fr.esgi.color_run.util.ThymeleafUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "forgotPasswordServlet", urlPatterns = {"/forgot-password", "/reset-password"})
public class ForgotPasswordServlet extends HttpServlet {

    private UtilisateurService utilisateurService;
    private EmailService emailService;

    @Override
    public void init() throws ServletException {
        ServiceFactory serviceFactory = ServiceFactory.getInstance();
        utilisateurService = serviceFactory.getUtilisateurService();
        emailService = serviceFactory.getEmailService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String servletPath = request.getServletPath();
        
        if ("/forgot-password".equals(servletPath)) {
            showForgotPasswordForm(request, response);
        } else if ("/reset-password".equals(servletPath)) {
            showResetPasswordForm(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String servletPath = request.getServletPath();
        
        try {
            if ("/forgot-password".equals(servletPath)) {
                processForgotPassword(request, response);
            } else if ("/reset-password".equals(servletPath)) {
                processResetPassword(request, response);
            }
        } catch (ServiceException e) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("error", "Une erreur est survenue: " + e.getMessage());
            ThymeleafUtil.processTemplate("error", variables, request, response);
        }
    }

    private void showForgotPasswordForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, Object> variables = new HashMap<>();
        ThymeleafUtil.processTemplate("forgot-password", variables, request, response);
    }

    private void showResetPasswordForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("token", token);
        ThymeleafUtil.processTemplate("reset-password", variables, request, response);
    }

    private void processForgotPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ServiceException {
        
        String email = request.getParameter("email");
        
        if (email == null || email.trim().isEmpty()) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("error", "Veuillez saisir votre adresse email");
            ThymeleafUtil.processTemplate("forgot-password", variables, request, response);
            return;
        }

        // Générer un token de reset et envoyer l'email
        utilisateurService.demanderResetMotDePasse(email);
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("success", "Un email de récupération a été envoyé si cette adresse existe");
        ThymeleafUtil.processTemplate("forgot-password", variables, request, response);
    }

    private void processResetPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ServiceException {
        
        String token = request.getParameter("token");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        if (!newPassword.equals(confirmPassword)) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("error", "Les mots de passe ne correspondent pas");
            variables.put("token", token);
            ThymeleafUtil.processTemplate("reset-password", variables, request, response);
            return;
        }

        utilisateurService.resetMotDePasse(token, newPassword);
        
        response.sendRedirect(request.getContextPath() + "/login?success=Mot de passe modifié avec succès");
    }
}