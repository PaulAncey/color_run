package fr.esgi.color_run.service.impl;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import fr.esgi.color_run.business.Course;
import fr.esgi.color_run.dao.CourseDAO;
import fr.esgi.color_run.dao.DAOException;
import fr.esgi.color_run.service.CourseService;
import fr.esgi.color_run.service.ServiceException;

public class CourseServiceImpl implements CourseService {

    private final CourseDAO courseDAO;

    public CourseServiceImpl(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    @Override
    public Course creerCourse(Course course) throws ServiceException {
        try {
            // Validation des données
            validateCourse(course);
            
            return courseDAO.create(course);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la création de la course", e);
        }
    }

    @Override
    public Optional<Course> trouverParId(int id) throws ServiceException {
        try {
            return courseDAO.findById(id);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la recherche de la course par ID", e);
        }
    }

    @Override
    public List<Course> trouverTous() throws ServiceException {
        try {
            return courseDAO.findAll();
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la récupération de toutes les courses", e);
        }
    }

    @Override
    public List<Course> trouverCoursesAVenir() throws ServiceException {
        try {
            return courseDAO.findUpcomingCourses();
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la récupération des courses à venir", e);
        }
    }

    @Override
    public List<Course> trouverParOrganisateur(int idOrganisateur) throws ServiceException {
        try {
            return courseDAO.findByOrganisateur(idOrganisateur);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la recherche des courses par organisateur", e);
        }
    }

    @Override
    public List<Course> trouverParVille(String ville) throws ServiceException {
        try {
            return courseDAO.findByVille(ville);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la recherche des courses par ville", e);
        }
    }

    @Override
    public List<Course> rechercherAvecFiltres(String ville, Float distanceMin, Float distanceMax, 
                                             Float prixMax, Boolean avecObstacles) throws ServiceException {
        try {
            return courseDAO.findWithFilters(ville, distanceMin, distanceMax, prixMax, avecObstacles);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la recherche des courses avec filtres", e);
        }
    }

    @Override
    public Course mettreAJour(Course course) throws ServiceException {
        try {
            // Vérifier si la course existe
            if (!courseDAO.findById(course.getIdCourse()).isPresent()) {
                throw new ServiceException("Course non trouvée avec l'ID: " + course.getIdCourse());
            }
            
            // Validation des données
            validateCourse(course);
            
            return courseDAO.update(course);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la mise à jour de la course", e);
        }
    }

    @Override
    public void supprimer(int id) throws ServiceException {
        try {
            // Vérifier si la course existe
            if (!courseDAO.findById(id).isPresent()) {
                throw new ServiceException("Course non trouvée avec l'ID: " + id);
            }
            
            // Vérifier s'il y a des participations à cette course
            // Cette vérification devrait être faite au niveau du service des participations
            // ou directement en base avec une contrainte de clé étrangère
            
            courseDAO.delete(id);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la suppression de la course", e);
        }
    }

    @Override
    public boolean estOrganisateur(int idUtilisateur, int idCourse) throws ServiceException {
        try {
            Optional<Course> course = courseDAO.findById(idCourse);
            
            if (!course.isPresent()) {
                throw new ServiceException("Course non trouvée avec l'ID: " + idCourse);
            }
            
            return course.get().getIdOrganisateur() == idUtilisateur;
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la vérification de l'organisateur", e);
        }
    }
    
    /**
     * Valide les données d'une course
     */
    private void validateCourse(Course course) throws ServiceException {
        if (course.getNom() == null || course.getNom().trim().isEmpty()) {
            throw new ServiceException("Le nom de la course est obligatoire");
        }
        
        if (course.getDescription() == null || course.getDescription().trim().isEmpty()) {
            throw new ServiceException("La description de la course est obligatoire");
        }
        
        if (course.getDateCourse() == null) {
            throw new ServiceException("La date de la course est obligatoire");
        }
        
        // Vérifier que la date est dans le futur
        Date today = new Date(System.currentTimeMillis());
        if (course.getDateCourse().before(today)) {
            throw new ServiceException("La date de la course doit être dans le futur");
        }
        
        if (course.getHeureDebut() == null) {
            throw new ServiceException("L'heure de début est obligatoire");
        }
        
        if (course.getVille() == null || course.getVille().trim().isEmpty()) {
            throw new ServiceException("La ville est obligatoire");
        }
        
        if (course.getAdresse() == null || course.getAdresse().trim().isEmpty()) {
            throw new ServiceException("L'adresse est obligatoire");
        }
        
        if (course.getDistanceKm() <= 0) {
            throw new ServiceException("La distance doit être supérieure à 0");
        }
        
        if (course.getNbMaxParticipants() <= 0) {
            throw new ServiceException("Le nombre maximum de participants doit être supérieur à 0");
        }
        
        if (course.getPrix() < 0) {
            throw new ServiceException("Le prix ne peut pas être négatif");
        }
        
        // Vérifier l'organisateur, mais cela devrait être fait par le service appelant
        if (course.getIdOrganisateur() <= 0) {
            throw new ServiceException("L'ID de l'organisateur est invalide");
        }
    }
}