package fr.esgi.color_run.dao;

import java.util.List;
import java.util.Optional;

/**
 * Interface générique pour les opérations CRUD
 */
public interface GenericDAO<T, ID> {
    T create(T entity) throws DAOException;
    Optional<T> findById(ID id) throws DAOException;
    List<T> findAll() throws DAOException;
    T update(T entity) throws DAOException;
    void delete(ID id) throws DAOException;
}