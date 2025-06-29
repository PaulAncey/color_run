package fr.esgi.color_run.business;

import java.sql.Timestamp;
import lombok.*;

/**
 * DTO pour représenter une participation avec les informations de l'utilisateur
 * associé
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationWithUser {
	// Informations de la participation
	private int idParticipation;
	private int idUtilisateur;
	private int idCourse;
	private int numeroDossard;
	private Timestamp dateInscription;
	private boolean dossardTelecharge;

	// Informations de l'utilisateur associé
	private String utilisateurNom;
	private String utilisateurPrenom;
	private String utilisateurEmail;

	/**
	 * Constructeur à partir d'une Participation et d'un Utilisateur
	 */
	public static ParticipationWithUser from(Participation participation, Utilisateur utilisateur) {
		return ParticipationWithUser.builder()
				.idParticipation(participation.getIdParticipation())
				.idUtilisateur(participation.getIdUtilisateur())
				.idCourse(participation.getIdCourse())
				.numeroDossard(participation.getNumeroDossard())
				.dateInscription(participation.getDateInscription())
				.dossardTelecharge(participation.isDossardTelecharge())
				.utilisateurNom(utilisateur.getNom())
				.utilisateurPrenom(utilisateur.getPrenom())
				.utilisateurEmail(utilisateur.getEmail())
				.build();
	}
}