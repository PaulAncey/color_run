package fr.esgi.color_run.business;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    private int idCourse;
    private String nom;
    private String description;
    private Date dateCourse;
    private Time heureDebut;
    private String ville;
    private String adresse;
    private float latitude;
    private float longitude;
    private float distanceKm;
    private int nbMaxParticipants;
    private float prix;
    private boolean avecObstacles;
    private String causeSoutenue;
    private int idOrganisateur;
    private Timestamp dateCreation;

    // public Course() {
    // }

    // public Course(int idCourse, String nom, String description, Date dateCourse,
    // Time heureDebut,
    // String ville, String adresse, float latitude, float longitude, float
    // distanceKm,
    // int nbMaxParticipants, float prix, boolean avecObstacles, String
    // causeSoutenue,
    // int idOrganisateur, Timestamp dateCreation) {
    // this.idCourse = idCourse;
    // this.nom = nom;
    // this.description = description;
    // this.dateCourse = dateCourse;
    // this.heureDebut = heureDebut;
    // this.ville = ville;
    // this.adresse = adresse;
    // this.latitude = latitude;
    // this.longitude = longitude;
    // this.distanceKm = distanceKm;
    // this.nbMaxParticipants = nbMaxParticipants;
    // this.prix = prix;
    // this.avecObstacles = avecObstacles;
    // this.causeSoutenue = causeSoutenue;
    // this.idOrganisateur = idOrganisateur;
    // this.dateCreation = dateCreation;
    // }

    // // Getters
    // public int getIdCourse() {
    // return idCourse;
    // }

    // public String getNom() {
    // return nom;
    // }

    // public String getDescription() {
    // return description;
    // }

    // public Date getDateCourse() {
    // return dateCourse;
    // }

    // public Time getHeureDebut() {
    // return heureDebut;
    // }

    // public String getVille() {
    // return ville;
    // }

    // public String getAdresse() {
    // return adresse;
    // }

    // public float getLatitude() {
    // return latitude;
    // }

    // public float getLongitude() {
    // return longitude;
    // }

    // public float getDistanceKm() {
    // return distanceKm;
    // }

    // public int getNbMaxParticipants() {
    // return nbMaxParticipants;
    // }

    // public float getPrix() {
    // return prix;
    // }

    // public boolean isAvecObstacles() {
    // return avecObstacles;
    // }

    // public String getCauseSoutenue() {
    // return causeSoutenue;
    // }

    // public int getIdOrganisateur() {
    // return idOrganisateur;
    // }

    // public Timestamp getDateCreation() {
    // return dateCreation;
    // }

    // // Setters
    // public void setIdCourse(int idCourse) {
    // this.idCourse = idCourse;
    // }

    // public void setNom(String nom) {
    // this.nom = nom;
    // }

    // public void setDescription(String description) {
    // this.description = description;
    // }

    // public void setDateCourse(Date dateCourse) {
    // this.dateCourse = dateCourse;
    // }

    // public void setHeureDebut(Time heureDebut) {
    // this.heureDebut = heureDebut;
    // }

    // public void setVille(String ville) {
    // this.ville = ville;
    // }

    // public void setAdresse(String adresse) {
    // this.adresse = adresse;
    // }

    // public void setLatitude(float latitude) {
    // this.latitude = latitude;
    // }

    // public void setLongitude(float longitude) {
    // this.longitude = longitude;
    // }

    // public void setDistanceKm(float distanceKm) {
    // this.distanceKm = distanceKm;
    // }

    // public void setNbMaxParticipants(int nbMaxParticipants) {
    // this.nbMaxParticipants = nbMaxParticipants;
    // }

    // public void setPrix(float prix) {
    // this.prix = prix;
    // }

    // public void setAvecObstacles(boolean avecObstacles) {
    // this.avecObstacles = avecObstacles;
    // }

    // public void setCauseSoutenue(String causeSoutenue) {
    // this.causeSoutenue = causeSoutenue;
    // }

    // public void setIdOrganisateur(int idOrganisateur) {
    // this.idOrganisateur = idOrganisateur;
    // }

    // public void setDateCreation(Timestamp dateCreation) {
    // this.dateCreation = dateCreation;
    // }

    // @Override
    // public String toString() {
    // return "Course{" +
    // "idCourse=" + idCourse +
    // ", nom='" + nom + '\'' +
    // ", description='" + description + '\'' +
    // ", dateCourse=" + dateCourse +
    // ", heureDebut=" + heureDebut +
    // ", ville='" + ville + '\'' +
    // ", distanceKm=" + distanceKm +
    // ", nbMaxParticipants=" + nbMaxParticipants +
    // ", prix=" + prix +
    // ", avecObstacles=" + avecObstacles +
    // ", causeSoutenue='" + causeSoutenue + '\'' +
    // ", idOrganisateur=" + idOrganisateur +
    // '}';
    // }
}