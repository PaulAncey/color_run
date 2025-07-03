package fr.esgi.color_run.dao;

import fr.esgi.color_run.business.PasswordResetToken;
import java.util.Optional;

public interface PasswordResetTokenDAO {
    PasswordResetToken save(PasswordResetToken token) throws DAOException;
    Optional<PasswordResetToken> findByToken(String token) throws DAOException;
    void markAsUsed(String token) throws DAOException;
    void deleteExpiredTokens() throws DAOException;
}