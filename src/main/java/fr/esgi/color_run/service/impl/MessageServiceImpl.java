package fr.esgi.color_run.service.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import fr.esgi.color_run.business.Message;
import fr.esgi.color_run.dao.DAOException;
import fr.esgi.color_run.dao.MessageDAO;
import fr.esgi.color_run.service.MessageService;
import fr.esgi.color_run.service.ServiceException;

public class MessageServiceImpl implements MessageService {

    private final MessageDAO messageDAO;

    public MessageServiceImpl(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    @Override
    public Message creerMessage(Message message) throws ServiceException {
        try {
            // Validation des données
            if (message.getContenu() == null || message.getContenu().trim().isEmpty()) {
                throw new ServiceException("Le contenu du message est obligatoire");
            }
            
            if (message.getIdCourse() <= 0) {
                throw new ServiceException("L'ID de la course est invalide");
            }
            
            if (message.getIdUtilisateur() <= 0) {
                throw new ServiceException("L'ID de l'utilisateur est invalide");
            }
            
            // Définir la date d'envoi si elle n'est pas spécifiée
            if (message.getDateEnvoi() == null) {
                message.setDateEnvoi(new Timestamp(System.currentTimeMillis()));
            }
            
            return messageDAO.create(message);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la création du message", e);
        }
    }

    @Override
    public Optional<Message> trouverParId(int id) throws ServiceException {
        try {
            return messageDAO.findById(id);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la recherche du message par ID", e);
        }
    }

    @Override
    public List<Message> trouverParCourse(int idCourse) throws ServiceException {
        try {
            return messageDAO.findByCourseId(idCourse);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la recherche des messages par course", e);
        }
    }

    @Override
    public List<Message> trouverParUtilisateur(int idUtilisateur) throws ServiceException {
        try {
            return messageDAO.findByUserId(idUtilisateur);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la recherche des messages par utilisateur", e);
        }
    }

    @Override
    public Message mettreAJour(Message message) throws ServiceException {
        try {
            // Validation des données
            if (message.getContenu() == null || message.getContenu().trim().isEmpty()) {
                throw new ServiceException("Le contenu du message est obligatoire");
            }
            
            // Vérifier si le message existe
            if (!messageDAO.findById(message.getIdMessage()).isPresent()) {
                throw new ServiceException("Message non trouvé avec l'ID: " + message.getIdMessage());
            }
            
            return messageDAO.update(message);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la mise à jour du message", e);
        }
    }

    @Override
    public void supprimer(int id) throws ServiceException {
        try {
            // Vérifier si le message existe
            if (!messageDAO.findById(id).isPresent()) {
                throw new ServiceException("Message non trouvé avec l'ID: " + id);
            }
            
            messageDAO.delete(id);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la suppression du message", e);
        }
    }
}