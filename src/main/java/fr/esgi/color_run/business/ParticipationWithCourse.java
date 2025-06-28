package fr.esgi.color_run.business;

import java.sql.Timestamp;
import lombok.*;

/**
 * DTO pour représenter une participation avec les informations de la course associée
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationWithCourse {
    // Informations de la participation
    private int idParticipation;
    private int idUtilisateur;
    private int idCourse;
    private int numeroDossard;
    private Timestamp dateInscription;
    private boolean dossardTelecharge;
    
    // Informations de la course associée
    private Course course;
    
    /**
     * Constructeur à partir d'une Participation et d'une Course
     */
    public static ParticipationWithCourse from(Participation participation, Course course) {
        return ParticipationWithCourse.builder()
                .idParticipation(participation.getIdParticipation())
                .idUtilisateur(participation.getIdUtilisateur())
                .idCourse(participation.getIdCourse())
                .numeroDossard(participation.getNumeroDossard())
                .dateInscription(participation.getDateInscription())
                .dossardTelecharge(participation.isDossardTelecharge())
                .course(course)
                .build();
    }
}