package fr.esgi.color_run.service;

import java.util.List;
import java.util.Optional;

import fr.esgi.color_run.business.Participation;
import fr.esgi.color_run.business.ParticipationWithCourse;

public interface ParticipationService {
    Participation inscrire(int idUtilisateur, int idCourse) throws ServiceException;
    Optional<Participation> trouverParId(int id) throws ServiceException;
    List<Participation> trouverParUtilisateur(int idUtilisateur) throws ServiceException;
    List<ParticipationWithCourse> trouverParUtilisateurAvecCourse(int idUtilisateur) throws ServiceException;
    List<Participation> trouverParCourse(int idCourse) throws ServiceException;
    Optional<Participation> trouverParUtilisateurEtCourse(int idUtilisateur, int idCourse) throws ServiceException;
    Participation mettreAJour(Participation participation) throws ServiceException;
    void annulerInscription(int idUtilisateur, int idCourse) throws ServiceException;
    void marquerDossardTelecharge(int idParticipation) throws ServiceException;
    int compterParticipants(int idCourse) throws ServiceException;
    boolean courseEstComplete(int idCourse) throws ServiceException;
    byte[] genererDossardPDF(int idParticipation) throws ServiceException;
}