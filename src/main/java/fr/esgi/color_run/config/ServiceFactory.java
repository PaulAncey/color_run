package fr.esgi.color_run.config;

import fr.esgi.color_run.service.*;
import fr.esgi.color_run.service.impl.*;

/**
 * Factory pour créer des instances de Service
 * Cette classe facilite l'injection de dépendances sans utiliser de framework
 */
public class ServiceFactory {
    
    private static ServiceFactory instance;
    
    // Instances des services (singleton)
    private UtilisateurService utilisateurService;
    private RoleService roleService;
    private CourseService courseService;
    private ParticipationService participationService;
    private MessageService messageService;
    private DemandeOrganisateurService demandeOrganisateurService;
    
    private ServiceFactory() {
        // Constructeur privé pour le pattern Singleton
    }
    
    /**
     * Récupère l'instance unique de la factory
     */
    public static synchronized ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }
    
    /**
     * Récupère une instance de UtilisateurService
     */
    public synchronized UtilisateurService getUtilisateurService() {
        if (utilisateurService == null) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            utilisateurService = new UtilisateurServiceImpl(
                    daoFactory.getUtilisateurDAO(),
                    daoFactory.getRoleDAO(),
                    daoFactory.getUtilisateurRoleDAO()
            );
        }
        return utilisateurService;
    }
    
    /**
     * Récupère une instance de RoleService
     */
    public synchronized RoleService getRoleService() {
        if (roleService == null) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            roleService = new RoleServiceImpl(daoFactory.getRoleDAO());
        }
        return roleService;
    }
    
    /**
     * Récupère une instance de CourseService
     */
    public synchronized CourseService getCourseService() {
        if (courseService == null) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            courseService = new CourseServiceImpl(daoFactory.getCourseDAO());
        }
        return courseService;
    }
    
    /**
     * Récupère une instance de ParticipationService
     */
    public synchronized ParticipationService getParticipationService() {
        if (participationService == null) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            participationService = new ParticipationServiceImpl(
                    daoFactory.getParticipationDAO(),
                    daoFactory.getCourseDAO(),
                    daoFactory.getUtilisateurDAO()
            );
        }
        return participationService;
    }
    
    /**
     * Récupère une instance de MessageService
     */
    public synchronized MessageService getMessageService() {
        if (messageService == null) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            messageService = new MessageServiceImpl(daoFactory.getMessageDAO());
        }
        return messageService;
    }
    
    /**
     * Récupère une instance de DemandeOrganisateurService
     */
    public synchronized DemandeOrganisateurService getDemandeOrganisateurService() {
        if (demandeOrganisateurService == null) {
            DAOFactory daoFactory = DAOFactory.getInstance();
            demandeOrganisateurService = new DemandeOrganisateurServiceImpl(
                    daoFactory.getDemandeOrganisateurDAO(),
                    daoFactory.getUtilisateurDAO(),
                    daoFactory.getRoleDAO()
            );
        }
        return demandeOrganisateurService;
    }
}