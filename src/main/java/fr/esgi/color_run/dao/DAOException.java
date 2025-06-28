package fr.esgi.color_run.dao;

/**
 * Exception spécifique pour la couche DAO
 */
public class DAOException extends Exception {
    public DAOException(String message) {
        super(message);
    }
    
    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}