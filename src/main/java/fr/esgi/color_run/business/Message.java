package fr.esgi.color_run.business;

import java.sql.Timestamp;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    private int idMessage;
    private int idCourse;
    private int idUtilisateur;
    private String contenu;
    private Timestamp dateEnvoi;

    // public Message() {
    // }

    // public Message(int idMessage, int idCourse, int idUtilisateur, String
    // contenu, Timestamp dateEnvoi) {
    // this.idMessage = idMessage;
    // this.idCourse = idCourse;
    // this.idUtilisateur = idUtilisateur;
    // this.contenu = contenu;
    // this.dateEnvoi = dateEnvoi;
    // }

    // // Getters
    // public int getIdMessage() {
    // return idMessage;
    // }

    // public int getIdCourse() {
    // return idCourse;
    // }

    // public int getIdUtilisateur() {
    // return idUtilisateur;
    // }

    // public String getContenu() {
    // return contenu;
    // }

    // public Timestamp getDateEnvoi() {
    // return dateEnvoi;
    // }

    // // Setters
    // public void setIdMessage(int idMessage) {
    // this.idMessage = idMessage;
    // }

    // public void setIdCourse(int idCourse) {
    // this.idCourse = idCourse;
    // }

    // public void setIdUtilisateur(int idUtilisateur) {
    // this.idUtilisateur = idUtilisateur;
    // }

    // public void setContenu(String contenu) {
    // this.contenu = contenu;
    // }

    // public void setDateEnvoi(Timestamp dateEnvoi) {
    // this.dateEnvoi = dateEnvoi;
    // }

    // @Override
    // public String toString() {
    // return "Message{" +
    // "idMessage=" + idMessage +
    // ", idCourse=" + idCourse +
    // ", idUtilisateur=" + idUtilisateur +
    // ", contenu='" + contenu + '\'' +
    // ", dateEnvoi=" + dateEnvoi +
    // '}';
    // }
}