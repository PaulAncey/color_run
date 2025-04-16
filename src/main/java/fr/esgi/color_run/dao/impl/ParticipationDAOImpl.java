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

import fr.esgi.color_run.business.Participation;
import fr.esgi.color_run.config.DatabaseConfig;
import fr.esgi.color_run.dao.DAOException;
import fr.esgi.color_run.dao.ParticipationDAO;

public class ParticipationDAOImpl implements ParticipationDAO {

    @Override
    public Participation create(Participation participation) throws DAOException {
        String sql = "INSERT INTO Participation (idUtilisateur, idCourse, numeroDossard, dossardTelecharge) " +
                     "VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, participation.getIdUtilisateur());
            stmt.setInt(2, participation.getIdCourse());
            stmt.setInt(3, participation.getNumeroDossard());
            stmt.setBoolean(4, participation.isDossardTelecharge());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("La création de la participation a échoué, aucune ligne affectée.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                participation.setIdParticipation(rs.getInt(1));
                participation.setDateInscription(new Timestamp(System.currentTimeMillis()));
            } else {
                throw new DAOException("La création de la participation a échoué, aucun ID obtenu.");
            }
            
            return participation;
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la création de la participation: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public Optional<Participation> findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM Participation WHERE idParticipation = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapRowToParticipation(rs));
            } else {
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche de la participation: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public List<Participation> findAll() throws DAOException {
        String sql = "SELECT * FROM Participation";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Participation> participations = new ArrayList<>();
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                participations.add(mapRowToParticipation(rs));
            }
            
            return participations;
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération de toutes les participations: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public Participation update(Participation participation) throws DAOException {
        String sql = "UPDATE Participation SET idUtilisateur = ?, idCourse = ?, numeroDossard = ?, " +
                     "dossardTelecharge = ? WHERE idParticipation = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, participation.getIdUtilisateur());
            stmt.setInt(2, participation.getIdCourse());
            stmt.setInt(3, participation.getNumeroDossard());
            stmt.setBoolean(4, participation.isDossardTelecharge());
            stmt.setInt(5, participation.getIdParticipation());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("La mise à jour de la participation a échoué, aucune participation trouvée avec l'ID: " 
                        + participation.getIdParticipation());
            }
            
            return participation;
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la mise à jour de la participation: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(stmt, conn);
        }
    }

    @Override
    public void delete(Integer id) throws DAOException {
        String sql = "DELETE FROM Participation WHERE idParticipation = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("La suppression de la participation a échoué, aucune participation trouvée avec l'ID: " + id);
            }
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression de la participation: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(stmt, conn);
        }
    }

    @Override
    public List<Participation> findByUserId(int idUtilisateur) throws DAOException {
        String sql = "SELECT * FROM Participation WHERE idUtilisateur = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Participation> participations = new ArrayList<>();
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUtilisateur);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                participations.add(mapRowToParticipation(rs));
            }
            
            return participations;
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche des participations par utilisateur: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public List<Participation> findByCourseId(int idCourse) throws DAOException {
        String sql = "SELECT * FROM Participation WHERE idCourse = ? ORDER BY numeroDossard ASC";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Participation> participations = new ArrayList<>();
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idCourse);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                participations.add(mapRowToParticipation(rs));
            }
            
            return participations;
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche des participations par course: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public Optional<Participation> findByUserAndCourse(int idUtilisateur, int idCourse) throws DAOException {
        String sql = "SELECT * FROM Participation WHERE idUtilisateur = ? AND idCourse = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUtilisateur);
            stmt.setInt(2, idCourse);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapRowToParticipation(rs));
            } else {
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche de la participation par utilisateur et course: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public int countParticipantsByCourse(int idCourse) throws DAOException {
        String sql = "SELECT COUNT(*) FROM Participation WHERE idCourse = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idCourse);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors du comptage des participants par course: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public int getNextDossardNumber(int idCourse) throws DAOException {
        String sql = "SELECT COALESCE(MAX(numeroDossard), 0) + 1 FROM Participation WHERE idCourse = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idCourse);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 1; // Commencer à 1 si aucun dossard n'existe encore
            }
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération du prochain numéro de dossard: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }
    
    /**
     * Transforme une ligne de résultat en objet Participation
     */
    private Participation mapRowToParticipation(ResultSet rs) throws SQLException {
        return Participation.builder()
                .idParticipation(rs.getInt("idParticipation"))
                .idUtilisateur(rs.getInt("idUtilisateur"))
                .idCourse(rs.getInt("idCourse"))
                .numeroDossard(rs.getInt("numeroDossard"))
                .dateInscription(rs.getTimestamp("dateInscription"))
                .dossardTelecharge(rs.getBoolean("dossardTelecharge"))
                .build();
    }
}