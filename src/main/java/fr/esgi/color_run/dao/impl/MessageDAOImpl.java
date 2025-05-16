package fr.esgi.color_run.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.esgi.color_run.business.Message;
import fr.esgi.color_run.dao.DAOException;
import fr.esgi.color_run.dao.MessageDAO;
import fr.esgi.color_run.database.DatabaseConfig;

public class MessageDAOImpl implements MessageDAO {

    @Override
    public Message create(Message message) throws DAOException {
        String sql = "INSERT INTO Message (idCourse, idUtilisateur, contenu) VALUES (?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, message.getIdCourse());
            stmt.setInt(2, message.getIdUtilisateur());
            stmt.setString(3, message.getContenu());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DAOException("La création du message a échoué, aucune ligne affectée.");
            }

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                message.setIdMessage(rs.getInt(1));
                message.setDateEnvoi(new Timestamp(System.currentTimeMillis()));
            } else {
                throw new DAOException("La création du message a échoué, aucun ID obtenu.");
            }

            return message;

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la création du message: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public Optional<Message> findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM Message WHERE idMessage = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToMessage(rs));
            } else {
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche du message: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public List<Message> findAll() throws DAOException {
        String sql = "SELECT * FROM Message ORDER BY dateEnvoi DESC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Message> messages = new ArrayList<>();

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                messages.add(mapRowToMessage(rs));
            }

            return messages;

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération de tous les messages: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public Message update(Message message) throws DAOException {
        String sql = "UPDATE Message SET idCourse = ?, idUtilisateur = ?, contenu = ? WHERE idMessage = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, message.getIdCourse());
            stmt.setInt(2, message.getIdUtilisateur());
            stmt.setString(3, message.getContenu());
            stmt.setInt(4, message.getIdMessage());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DAOException("La mise à jour du message a échoué, aucun message trouvé avec l'ID: "
                        + message.getIdMessage());
            }

            return message;

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la mise à jour du message: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(stmt, conn);
        }
    }

    @Override
    public void delete(Integer id) throws DAOException {
        String sql = "DELETE FROM Message WHERE idMessage = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DAOException("La suppression du message a échoué, aucun message trouvé avec l'ID: " + id);
            }

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression du message: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(stmt, conn);
        }
    }

    @Override
    public List<Message> findByCourseId(int idCourse) throws DAOException {
        String sql = "SELECT * FROM Message WHERE idCourse = ? ORDER BY dateEnvoi ASC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Message> messages = new ArrayList<>();

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idCourse);

            rs = stmt.executeQuery();

            while (rs.next()) {
                messages.add(mapRowToMessage(rs));
            }

            return messages;

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche des messages par course: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public List<Message> findByUserId(int idUtilisateur) throws DAOException {
        String sql = "SELECT * FROM Message WHERE idUtilisateur = ? ORDER BY dateEnvoi DESC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Message> messages = new ArrayList<>();

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUtilisateur);

            rs = stmt.executeQuery();

            while (rs.next()) {
                messages.add(mapRowToMessage(rs));
            }

            return messages;

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche des messages par utilisateur: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    /**
     * Transforme une ligne de résultat en objet Message
     */
    private Message mapRowToMessage(ResultSet rs) throws SQLException {
        return Message.builder()
                .idMessage(rs.getInt("idMessage"))
                .idCourse(rs.getInt("idCourse"))
                .idUtilisateur(rs.getInt("idUtilisateur"))
                .contenu(rs.getString("contenu"))
                .dateEnvoi(rs.getTimestamp("dateEnvoi"))
                .build();
    }
}