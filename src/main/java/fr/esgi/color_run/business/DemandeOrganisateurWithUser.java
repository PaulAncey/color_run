package fr.esgi.color_run.business;

import java.sql.Timestamp;
import lombok.*;

/**
 * DTO pour représenter une demande d'organisateur avec les informations de
 * l'utilisateur associé
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandeOrganisateurWithUser {
	// Informations de la demande
	private int idDemande;
	private int idUtilisateur;
	private String motif;
	private String statut; // "EN_ATTENTE", "ACCEPTEE", "REFUSEE"
	private Timestamp dateDemande;
	private Timestamp dateTraitement;
	private String commentaireAdmin;

	// Informations de l'utilisateur associé
	private String utilisateurNom;
	private String utilisateurPrenom;
	private String utilisateurEmail;

	/**
	 * Constructeur à partir d'une DemandeOrganisateur et d'un Utilisateur
	 */
	public static DemandeOrganisateurWithUser from(DemandeOrganisateur demande, Utilisateur utilisateur) {
		return DemandeOrganisateurWithUser.builder()
				.idDemande(demande.getIdDemande())
				.idUtilisateur(demande.getIdUtilisateur())
				.motif(demande.getMotif())
				.statut(demande.getStatut())
				.dateDemande(demande.getDateDemande())
				.dateTraitement(demande.getDateTraitement())
				.commentaireAdmin(demande.getCommentaireAdmin())
				.utilisateurNom(utilisateur.getNom())
				.utilisateurPrenom(utilisateur.getPrenom())
				.utilisateurEmail(utilisateur.getEmail())
				.build();
	}
}