package fr.esgi.color_run.service;

/**
 * Service pour l'envoi d'emails dans l'application Color Run
 */
public interface EmailService {
    
    /**
     * Envoie un email de réinitialisation de mot de passe
     * 
     * @param email L'adresse email du destinataire
     * @param token Le token de réinitialisation sécurisé
     * @throws ServiceException En cas d'erreur lors de l'envoi
     */
    void envoyerEmailResetMotDePasse(String email, String token) throws ServiceException;
    
    /**
     * Envoie un email de confirmation d'inscription
     * 
     * @param email L'adresse email du nouvel utilisateur
     * @param nom Le nom de famille de l'utilisateur
     * @param prenom Le prénom de l'utilisateur
     * @throws ServiceException En cas d'erreur lors de l'envoi
     */
    void envoyerEmailConfirmationInscription(String email, String nom, String prenom) throws ServiceException;
}