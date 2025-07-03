package fr.esgi.color_run.service;

import fr.esgi.color_run.config.EmailConfig;
import fr.esgi.color_run.service.EmailService;
import fr.esgi.color_run.service.ServiceException;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailServiceImpl implements EmailService {
    
    private final String smtpHost = EmailConfig.Env.SMTP_HOST != null ? 
        EmailConfig.Env.SMTP_HOST : EmailConfig.GMAIL_SMTP_HOST;
    private final String smtpPort = EmailConfig.Env.SMTP_PORT != null ? 
        EmailConfig.Env.SMTP_PORT : EmailConfig.GMAIL_SMTP_PORT;
    private final String emailFrom = EmailConfig.Env.EMAIL_FROM != null ? 
        EmailConfig.Env.EMAIL_FROM : "noreply@colorrun.fr";
    private final String emailPassword = EmailConfig.Env.EMAIL_PASSWORD != null ? 
        EmailConfig.Env.EMAIL_PASSWORD : "votre_mot_de_passe";
    
    @Override
    public void envoyerEmailResetMotDePasse(String email, String token) throws ServiceException {
        String subject = "Réinitialisation de votre mot de passe - Color Run";
        String resetUrl = EmailConfig.BASE_URL + "/reset-password?token=" + token;
        
        String body = createResetPasswordTemplate(resetUrl);
        envoyerEmail(email, subject, body);
    }

    @Override
    public void envoyerEmailConfirmationInscription(String email, String nom, String prenom) throws ServiceException {
        String subject = "Bienvenue sur Color Run !";
        String body = createWelcomeTemplate(prenom, nom);
        envoyerEmail(email, subject, body);
    }
    
    private String createResetPasswordTemplate(String resetUrl) {
        return String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <div style="background: linear-gradient(135deg, #FF5A5F, #6C757D); padding: 20px; text-align: center;">
                    <h1 style="color: white; margin: 0;">Color Run</h1>
                </div>
                <div style="padding: 20px;">
                    <h2>Réinitialisation de mot de passe</h2>
                    <p>Cliquez sur le bouton ci-dessous pour réinitialiser votre mot de passe :</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background-color: #FF5A5F; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">Réinitialiser</a>
                    </div>
                    <p><strong>Ce lien expire dans 24 heures.</strong></p>
                    <p>Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.</p>
                </div>
            </body>
            </html>
            """, resetUrl);
    }
    
    private String createWelcomeTemplate(String prenom, String nom) {
        return String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <div style="background: linear-gradient(135deg, #FF5A5F, #6C757D); padding: 20px; text-align: center;">
                    <h1 style="color: white; margin: 0;">Color Run</h1>
                </div>
                <div style="padding: 20px;">
                    <h2>Bienvenue %s !</h2>
                    <p>Votre inscription a été confirmée avec succès.</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s/dashboard" style="background-color: #FF5A5F; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">Mon tableau de bord</a>
                    </div>
                    <p>Prêt pour une explosion de couleurs ? 🎨</p>
                </div>
            </body>
            </html>
            """, prenom, EmailConfig.BASE_URL);
    }
    
    private void envoyerEmail(String toEmail, String subject, String body) throws ServiceException {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            
            Authenticator auth = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailFrom, emailPassword);
                }
            };
            
            Session session = Session.getInstance(props, auth);
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailFrom, "Color Run"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");
            
            Transport.send(message);
            System.out.println("✅ Email envoyé à: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur email: " + e.getMessage());
            throw new ServiceException("Erreur lors de l'envoi de l'email", e);
        }
    }
}