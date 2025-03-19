package fr.esgi.color_run.business;

import lombok.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilisateurRole {
    private int idUtilisateurRole;
    private int idUtilisateur;
    private int idRole;
    private Timestamp dateAttribution;

    // public UtilisateurRole() {
    // }

    // public UtilisateurRole(int idUtilisateurRole, int idUtilisateur, int idRole, Timestamp dateAttribution) {
    //     this.idUtilisateurRole = idUtilisateurRole;
    //     this.idUtilisateur = idUtilisateur;
    //     this.idRole = idRole;
    //     this.dateAttribution = dateAttribution;
    // }

    // // Getters
    // public int getIdUtilisateurRole() {
    //     return idUtilisateurRole;
    // }

    // public int getIdUtilisateur() {
    //     return idUtilisateur;
    // }

    // public int getIdRole() {
    //     return idRole;
    // }

    // public Timestamp getDateAttribution() {
    //     return dateAttribution;
    // }

    // // Setters
    // public void setIdUtilisateurRole(int idUtilisateurRole) {
    //     this.idUtilisateurRole = idUtilisateurRole;
    // }

    // public void setIdUtilisateur(int idUtilisateur) {
    //     this.idUtilisateur = idUtilisateur;
    // }

    // public void setIdRole(int idRole) {
    //     this.idRole = idRole;
    // }

    // public void setDateAttribution(Timestamp dateAttribution) {
    //     this.dateAttribution = dateAttribution;
    // }

    // @Override
    // public String toString() {
    //     return "UtilisateurRole{" +
    //             "idUtilisateurRole=" + idUtilisateurRole +
    //             ", idUtilisateur=" + idUtilisateur +
    //             ", idRole=" + idRole +
    //             ", dateAttribution=" + dateAttribution +
    //             '}';
    // }
}