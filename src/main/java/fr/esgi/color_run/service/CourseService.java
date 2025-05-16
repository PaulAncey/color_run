package fr.esgi.color_run.service;

import java.util.List;
import java.util.Optional;

import fr.esgi.color_run.business.Course;

public interface CourseService {
    Course creerCourse(Course course) throws ServiceException;
    Optional<Course> trouverParId(int id) throws ServiceException;
    List<Course> trouverTous() throws ServiceException;
    List<Course> trouverCoursesAVenir() throws ServiceException;
    List<Course> trouverParOrganisateur(int idOrganisateur) throws ServiceException;
    List<Course> trouverParVille(String ville) throws ServiceException;
    List<Course> rechercherAvecFiltres(String ville, Float distanceMin, Float distanceMax, 
                                     Float prixMax, Boolean avecObstacles) throws ServiceException;
    Course mettreAJour(Course course) throws ServiceException;
    void supprimer(int id) throws ServiceException;
    boolean estOrganisateur(int idUtilisateur, int idCourse) throws ServiceException;
}