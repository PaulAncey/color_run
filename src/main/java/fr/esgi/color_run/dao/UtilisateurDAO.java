package fr.esgi.color_run.dao;

import fr.esgi.color_run.business.*;
import java.util.List;
import java.util.Optional;

/**
 * DAO pour les utilisateurs
 */
public interface UtilisateurDAO extends GenericDAO<Utilisateur, Integer> {
    Optional<Utilisateur> findByEmail(String email) throws DAOException;
    boolean verifyPassword(String email, String password) throws DAOException;
    List<Role> getUserRoles(int idUtilisateur) throws DAOException;
}