package fr.esgi.color_run.dao;

import java.util.List;

import fr.esgi.color_run.business.Course;

public interface CourseDAO extends GenericDAO<Course, Integer> {
    List<Course> findByOrganisateur(int idOrganisateur) throws DAOException;
    List<Course> findUpcomingCourses() throws DAOException;
    List<Course> findByVille(String ville) throws DAOException;
    List<Course> findWithFilters(String ville, Float minDistance, Float maxDistance, 
    Float maxPrix, Boolean avecObstacles) throws DAOException;
}