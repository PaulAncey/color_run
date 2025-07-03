package fr.esgi.color_run.dao.impl;

import fr.esgi.color_run.business.PasswordResetToken;
import fr.esgi.color_run.dao.DAOException;
import fr.esgi.color_run.dao.PasswordResetTokenDAO;
import fr.esgi.color_run.util.DatabaseConnection;

import java.sql.*;
import java.util.Optional;

public class PasswordResetTokenDAOImpl implements PasswordResetTokenDAO {

    @Override
    public PasswordResetToken save(PasswordResetToken token) throws DAOException {
        String sql = "INSERT INTO password_reset_tokens (id_utilisateur, token, date_expiration) VALUES (?, ?, ?)";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, token.getIdUtilisateur());
            stmt.setString(2, token.getToken());
            stmt.setTimestamp(3, token.getDateExpiration());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Échec de l'insertion du token");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    token.setIdToken(generatedKeys.getInt(1));
                }
            }
            
            return token;
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la sauvegarde du token", e);
        }
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String tokenValue) throws DAOException {
        String sql = "SELECT * FROM password_reset_tokens WHERE token = ? AND utilise = FALSE AND date_expiration > CURRENT_TIMESTAMP";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, tokenValue);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToToken(rs));
                }
            }
            
            return Optional.empty();
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche du token", e);
        }
    }

    @Override
    public void markAsUsed(String tokenValue) throws DAOException {
        String sql = "UPDATE password_reset_tokens SET utilise = TRUE WHERE token = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, tokenValue);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors du marquage du token comme utilisé", e);
        }
    }

    @Override
    public void deleteExpiredTokens() throws DAOException {
        String sql = "DELETE FROM password_reset_tokens WHERE date_expiration < CURRENT_TIMESTAMP OR utilise = TRUE";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors du nettoyage des tokens expirés", e);
        }
    }
    
    private PasswordResetToken mapResultSetToToken(ResultSet rs) throws SQLException {
        return PasswordResetToken.builder()
                .idToken(rs.getInt("id_token"))
                .idUtilisateur(rs.getInt("id_utilisateur"))
                .token(rs.getString("token"))
                .dateExpiration(rs.getTimestamp("date_expiration"))
                .utilise(rs.getBoolean("utilise"))
                .dateCreation(rs.getTimestamp("date_creation"))
                .build();
    }
}