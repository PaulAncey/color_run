package fr.esgi.color_run.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.esgi.color_run.business.Message;
import fr.esgi.color_run.business.MessageWithUser;
import fr.esgi.color_run.business.Utilisateur;
import fr.esgi.color_run.dao.DAOException;
import fr.esgi.color_run.dao.MessageDAO;
import fr.esgi.color_run.dao.UtilisateurDAO;
import fr.esgi.color_run.service.MessageService;
import fr.esgi.color_run.service.ServiceException;

public class MessageServiceImpl implements MessageService {

    private final MessageDAO messageDAO;
    private final UtilisateurDAO utilisateurDAO;

    public MessageServiceImpl(MessageDAO messageDAO, UtilisateurDAO utilisateurDAO) {
        this.messageDAO = messageDAO;
        this.utilisateurDAO = utilisateurDAO;
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
    public List<MessageWithUser> trouverParCourseAvecUtilisateur(int idCourse) throws ServiceException {
        try {
            List<Message> messages = messageDAO.findByCourseId(idCourse);
            List<MessageWithUser> result = new ArrayList<>();
            
            for (Message message : messages) {
                Optional<Utilisateur> utilisateur = utilisateurDAO.findById(message.getIdUtilisateur());
                if (utilisateur.isPresent()) {
                    result.add(MessageWithUser.from(message, utilisateur.get()));
                } else {
                    // Si l'utilisateur n'existe plus, créer un utilisateur fictif
                    Utilisateur userFictif = Utilisateur.builder()
                            .nom("Inconnu")
                            .prenom("Utilisateur")
                            .email("inconnu@example.com")
                            .build();
                    result.add(MessageWithUser.from(message, userFictif));
                }
            }
            
            return result;
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la recherche des messages avec utilisateurs par course", e);
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