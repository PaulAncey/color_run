package fr.esgi.color_run.business;

import java.sql.Timestamp;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participation {
    private int idParticipation;
    private int idUtilisateur;
    private int idCourse;
    private int numeroDossard;
    private Timestamp dateInscription;
    private boolean dossardTelecharge;

    // public Participation() {
    // }

    // public Participation(int idParticipation, int idUtilisateur, int idCourse,
    // int numeroDossard, Timestamp dateInscription, boolean dossardTelecharge) {
    // this.idParticipation = idParticipation;
    // this.idUtilisateur = idUtilisateur;
    // this.idCourse = idCourse;
    // this.numeroDossard = numeroDossard;
    // this.dateInscription = dateInscription;
    // this.dossardTelecharge = dossardTelecharge;
    // }

    // // Getters
    // public int getIdParticipation() {
    // return idParticipation;
    // }

    // public int getIdUtilisateur() {
    // return idUtilisateur;
    // }

    // public int getIdCourse() {
    // return idCourse;
    // }

    // public int getNumeroDossard() {
    // return numeroDossard;
    // }

    // public Timestamp getDateInscription() {
    // return dateInscription;
    // }

    // public boolean isDossardTelecharge() {
    // return dossardTelecharge;
    // }

    // // Setters
    // public void setIdParticipation(int idParticipation) {
    // this.idParticipation = idParticipation;
    // }

    // public void setIdUtilisateur(int idUtilisateur) {
    // this.idUtilisateur = idUtilisateur;
    // }

    // public void setIdCourse(int idCourse) {
    // this.idCourse = idCourse;
    // }

    // public void setNumeroDossard(int numeroDossard) {
    // this.numeroDossard = numeroDossard;
    // }

    // public void setDateInscription(Timestamp dateInscription) {
    // this.dateInscription = dateInscription;
    // }

    // public void setDossardTelecharge(boolean dossardTelecharge) {
    // this.dossardTelecharge = dossardTelecharge;
    // }

    // @Override
    // public String toString() {
    // return "Participation{" +
    // "idParticipation=" + idParticipation +
    // ", idUtilisateur=" + idUtilisateur +
    // ", idCourse=" + idCourse +
    // ", numeroDossard=" + numeroDossard +
    // ", dateInscription=" + dateInscription +
    // ", dossardTelecharge=" + dossardTelecharge +
    // '}';
    // }
}