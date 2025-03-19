package fr.esgi.color_run.business;

import java.sql.Timestamp;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utilisateur {
    private int idUtilisateur;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String photoProfile;
    private boolean compteVerifie;
    private Timestamp dateCreation;

    // public Utilisateur() {
    // }

    // public Utilisateur(int idUtilisateur, String nom, String prenom, String
    // email, String motDePasse,
    // String photoProfile, boolean compteVerifie, Timestamp dateCreation) {
    // this.idUtilisateur = idUtilisateur;
    // this.nom = nom;
    // this.prenom = prenom;
    // this.email = email;
    // this.motDePasse = motDePasse;
    // this.photoProfile = photoProfile;
    // this.compteVerifie = compteVerifie;
    // this.dateCreation = dateCreation;
    // }

    // // Getters
    // public int getIdUtilisateur() {
    // return idUtilisateur;
    // }

    // public String getNom() {
    // return nom;
    // }

    // public String getPrenom() {
    // return prenom;
    // }

    // public String getEmail() {
    // return email;
    // }

    // public String getMotDePasse() {
    // return motDePasse;
    // }

    // public String getPhotoProfile() {
    // return photoProfile;
    // }

    // public boolean isCompteVerifie() {
    // return compteVerifie;
    // }

    // public Timestamp getDateCreation() {
    // return dateCreation;
    // }

    // // Setters
    // public void setIdUtilisateur(int idUtilisateur) {
    // this.idUtilisateur = idUtilisateur;
    // }

    // public void setNom(String nom) {
    // this.nom = nom;
    // }

    // public void setPrenom(String prenom) {
    // this.prenom = prenom;
    // }

    // public void setEmail(String email) {
    // this.email = email;
    // }

    // public void setMotDePasse(String motDePasse) {
    // this.motDePasse = motDePasse;
    // }

    // public void setPhotoProfile(String photoProfile) {
    // this.photoProfile = photoProfile;
    // }

    // public void setCompteVerifie(boolean compteVerifie) {
    // this.compteVerifie = compteVerifie;
    // }

    // public void setDateCreation(Timestamp dateCreation) {
    // this.dateCreation = dateCreation;
    // }

    // @Override
    // public String toString() {
    // return "Utilisateur{" +
    // "idUtilisateur=" + idUtilisateur +
    // ", nom='" + nom + '\'' +
    // ", prenom='" + prenom + '\'' +
    // ", email='" + email + '\'' +
    // ", compteVerifie=" + compteVerifie +
    // ", dateCreation=" + dateCreation +
    // '}';
    // }
}