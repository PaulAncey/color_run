package fr.esgi.color_run.config;

import fr.esgi.color_run.dao.*;
import fr.esgi.color_run.dao.impl.*;

/**
 * Factory pour créer des instances de DAO
 * Cette classe facilite l'injection de dépendances sans utiliser de framework
 */
public class DAOFactory {
    
    private static DAOFactory instance;
    
    // Instances des DAO (singleton)
    private UtilisateurDAO utilisateurDAO;
    private RoleDAO roleDAO;
    private UtilisateurRoleDAO utilisateurRoleDAO;
    private CourseDAO courseDAO;
    private ParticipationDAO participationDAO;
    private MessageDAO messageDAO;
    private DemandeOrganisateurDAO demandeOrganisateurDAO;
    
    private DAOFactory() {
        // Constructeur privé pour le pattern Singleton
    }
    
    /**
     * Récupère l'instance unique de la factory
     */
    public static synchronized DAOFactory getInstance() {
        if (instance == null) {
            instance = new DAOFactory();
        }
        return instance;
    }
    
    /**
     * Récupère une instance de UtilisateurDAO
     */
    public synchronized UtilisateurDAO getUtilisateurDAO() {
        if (utilisateurDAO == null) {
            utilisateurDAO = new UtilisateurDAOImpl();
        }
        return utilisateurDAO;
    }
    
    /**
     * Récupère une instance de RoleDAO
     */
    public synchronized RoleDAO getRoleDAO() {
        if (roleDAO == null) {
            roleDAO = new RoleDAOImpl();
        }
        return roleDAO;
    }
    
    /**
     * Récupère une instance de UtilisateurRoleDAO
     */
    public synchronized UtilisateurRoleDAO getUtilisateurRoleDAO() {
        if (utilisateurRoleDAO == null) {
            utilisateurRoleDAO = new UtilisateurRoleDAOImpl();
        }
        return utilisateurRoleDAO;
    }
    
    /**
     * Récupère une instance de CourseDAO
     */
    public synchronized CourseDAO getCourseDAO() {
        if (courseDAO == null) {
            courseDAO = new CourseDAOImpl();
        }
        return courseDAO;
    }
    
    /**
     * Récupère une instance de ParticipationDAO
     */
    public synchronized ParticipationDAO getParticipationDAO() {
        if (participationDAO == null) {
            participationDAO = new ParticipationDAOImpl();
        }
        return participationDAO;
    }
    
    /**
     * Récupère une instance de MessageDAO
     */
    public synchronized MessageDAO getMessageDAO() {
        if (messageDAO == null) {
            messageDAO = new MessageDAOImpl();
        }
        return messageDAO;
    }
    
    /**
     * Récupère une instance de DemandeOrganisateurDAO
     */
    public synchronized DemandeOrganisateurDAO getDemandeOrganisateurDAO() {
        if (demandeOrganisateurDAO == null) {
            demandeOrganisateurDAO = new DemandeOrganisateurDAOImpl();
        }
        return demandeOrganisateurDAO;
    }
}