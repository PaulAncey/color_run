package fr.esgi.color_run.dao;

import java.util.List;
import java.util.Optional;

import fr.esgi.color_run.business.Participation;

public interface ParticipationDAO extends GenericDAO<Participation, Integer> {
    List<Participation> findByUserId(int idUtilisateur) throws DAOException;
    List<Participation> findByCourseId(int idCourse) throws DAOException;
    Optional<Participation> findByUserAndCourse(int idUtilisateur, int idCourse) throws DAOException;
    int countParticipantsByCourse(int idCourse) throws DAOException;
    int getNextDossardNumber(int idCourse) throws DAOException;
}