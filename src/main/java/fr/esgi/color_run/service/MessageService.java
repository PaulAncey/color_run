package fr.esgi.color_run.service;

import java.util.List;
import java.util.Optional;

import fr.esgi.color_run.business.Message;
import fr.esgi.color_run.business.MessageWithUser;

public interface MessageService {
    Message creerMessage(Message message) throws ServiceException;
    Optional<Message> trouverParId(int id) throws ServiceException;
    List<Message> trouverParCourse(int idCourse) throws ServiceException;
    List<MessageWithUser> trouverParCourseAvecUtilisateur(int idCourse) throws ServiceException;
    List<Message> trouverParUtilisateur(int idUtilisateur) throws ServiceException;
    Message mettreAJour(Message message) throws ServiceException;
    void supprimer(int id) throws ServiceException;
}