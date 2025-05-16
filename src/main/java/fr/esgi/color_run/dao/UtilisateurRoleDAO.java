package fr.esgi.color_run.dao;

import java.util.List;

import fr.esgi.color_run.business.UtilisateurRole;


public interface UtilisateurRoleDAO extends GenericDAO<UtilisateurRole, Integer> {
    List<UtilisateurRole> findByUserId(int idUtilisateur) throws DAOException;
    List<UtilisateurRole> findByRoleId(int idRole) throws DAOException;
    void assignRoleToUser(int idUtilisateur, int idRole) throws DAOException;
    void removeRoleFromUser(int idUtilisateur, int idRole) throws DAOException;
}