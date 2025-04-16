package fr.esgi.color_run.dao;

import fr.esgi.color_run.business.*;
import java.util.Optional;

public interface RoleDAO extends GenericDAO<Role, Integer> {
    Optional<Role> findByName(String nomRole) throws DAOException;
}
