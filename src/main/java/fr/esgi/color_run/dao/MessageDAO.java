package fr.esgi.color_run.dao;

import java.util.List;

import fr.esgi.color_run.business.Message;

public interface MessageDAO extends GenericDAO<Message, Integer> {
    List<Message> findByCourseId(int idCourse) throws DAOException;
    List<Message> findByUserId(int idUtilisateur) throws DAOException;
}