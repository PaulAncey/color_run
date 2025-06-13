package fr.esgi.color_run.business;

import java.sql.Timestamp;
import lombok.*;

/**
 * DTO pour représenter un message avec les informations de l'utilisateur associé
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageWithUser {
    // Informations du message
    private int idMessage;
    private int idCourse;
    private int idUtilisateur;
    private String contenu;
    private Timestamp dateEnvoi;
    
    // Informations de l'utilisateur
    private String utilisateurNom;
    private String utilisateurPrenom;
    private String utilisateurEmail;
    
    /**
     * Constructeur à partir d'un Message et d'un Utilisateur
     */
    public static MessageWithUser from(Message message, Utilisateur utilisateur) {
        return MessageWithUser.builder()
                .idMessage(message.getIdMessage())
                .idCourse(message.getIdCourse())
                .idUtilisateur(message.getIdUtilisateur())
                .contenu(message.getContenu())
                .dateEnvoi(message.getDateEnvoi())
                .utilisateurNom(utilisateur.getNom())
                .utilisateurPrenom(utilisateur.getPrenom())
                .utilisateurEmail(utilisateur.getEmail())
                .build();
    }
    
    /**
     * Retourne le nom complet de l'utilisateur
     */
    public String getUtilisateurNomComplet() {
        return utilisateurPrenom + " " + utilisateurNom;
    }
}