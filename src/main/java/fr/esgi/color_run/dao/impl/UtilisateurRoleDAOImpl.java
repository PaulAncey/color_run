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

import fr.esgi.color_run.business.UtilisateurRole;
import fr.esgi.color_run.config.DatabaseConfig;
import fr.esgi.color_run.dao.DAOException;
import fr.esgi.color_run.dao.UtilisateurRoleDAO;

public class UtilisateurRoleDAOImpl implements UtilisateurRoleDAO {

    @Override
    public UtilisateurRole create(UtilisateurRole utilisateurRole) throws DAOException {
        String sql = "INSERT INTO UtilisateurRole (idUtilisateur, idRole) VALUES (?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, utilisateurRole.getIdUtilisateur());
            stmt.setInt(2, utilisateurRole.getIdRole());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("La création de l'association utilisateur-rôle a échoué, aucune ligne affectée.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                utilisateurRole.setIdUtilisateurRole(rs.getInt(1));
                utilisateurRole.setDateAttribution(new Timestamp(System.currentTimeMillis()));
            } else {
                throw new DAOException("La création de l'association utilisateur-rôle a échoué, aucun ID obtenu.");
            }
            
            return utilisateurRole;
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la création de l'association utilisateur-rôle: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public Optional<UtilisateurRole> findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM UtilisateurRole WHERE idUtilisateurRole = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapRowToUtilisateurRole(rs));
            } else {
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche de l'association utilisateur-rôle: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public List<UtilisateurRole> findAll() throws DAOException {
        String sql = "SELECT * FROM UtilisateurRole";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<UtilisateurRole> utilisateurRoles = new ArrayList<>();
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                utilisateurRoles.add(mapRowToUtilisateurRole(rs));
            }
            
            return utilisateurRoles;
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération de toutes les associations utilisateur-rôle: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public UtilisateurRole update(UtilisateurRole utilisateurRole) throws DAOException {
        String sql = "UPDATE UtilisateurRole SET idUtilisateur = ?, idRole = ? WHERE idUtilisateurRole = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, utilisateurRole.getIdUtilisateur());
            stmt.setInt(2, utilisateurRole.getIdRole());
            stmt.setInt(3, utilisateurRole.getIdUtilisateurRole());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("La mise à jour de l'association utilisateur-rôle a échoué, aucune association trouvée avec l'ID: " 
                        + utilisateurRole.getIdUtilisateurRole());
            }
            
            return utilisateurRole;
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la mise à jour de l'association utilisateur-rôle: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(stmt, conn);
        }
    }

    @Override
    public void delete(Integer id) throws DAOException {
        String sql = "DELETE FROM UtilisateurRole WHERE idUtilisateurRole = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("La suppression de l'association utilisateur-rôle a échoué, aucune association trouvée avec l'ID: " + id);
            }
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression de l'association utilisateur-rôle: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(stmt, conn);
        }
    }

    @Override
    public List<UtilisateurRole> findByUserId(int idUtilisateur) throws DAOException {
        String sql = "SELECT * FROM UtilisateurRole WHERE idUtilisateur = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<UtilisateurRole> utilisateurRoles = new ArrayList<>();
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUtilisateur);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                utilisateurRoles.add(mapRowToUtilisateurRole(rs));
            }
            
            return utilisateurRoles;
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche des associations utilisateur-rôle par utilisateur: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public List<UtilisateurRole> findByRoleId(int idRole) throws DAOException {
        String sql = "SELECT * FROM UtilisateurRole WHERE idRole = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<UtilisateurRole> utilisateurRoles = new ArrayList<>();
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idRole);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                utilisateurRoles.add(mapRowToUtilisateurRole(rs));
            }
            
            return utilisateurRoles;
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche des associations utilisateur-rôle par rôle: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public void assignRoleToUser(int idUtilisateur, int idRole) throws DAOException {
        // Vérifier si l'association existe déjà
        String checkSql = "SELECT COUNT(*) FROM UtilisateurRole WHERE idUtilisateur = ? AND idRole = ?";
        String insertSql = "INSERT INTO UtilisateurRole (idUtilisateur, idRole) VALUES (?, ?)";
        
        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            
            // Vérifier si l'association existe déjà
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, idUtilisateur);
            checkStmt.setInt(2, idRole);
            
            rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                // L'association existe déjà, rien à faire
                return;
            }
            
            // Créer l'association
            insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setInt(1, idUtilisateur);
            insertStmt.setInt(2, idRole);
            
            int affectedRows = insertStmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("L'attribution du rôle à l'utilisateur a échoué, aucune ligne affectée.");
            }
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de l'attribution du rôle à l'utilisateur: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, checkStmt, null);
            DatabaseConfig.closeResources(insertStmt, conn);
        }
    }

    @Override
    public void removeRoleFromUser(int idUtilisateur, int idRole) throws DAOException {
        String sql = "DELETE FROM UtilisateurRole WHERE idUtilisateur = ? AND idRole = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUtilisateur);
            stmt.setInt(2, idRole);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors du retrait du rôle de l'utilisateur: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(stmt, conn);
        }
    }
    
    /**
     * Transforme une ligne de résultat en objet UtilisateurRole
     */
    private UtilisateurRole mapRowToUtilisateurRole(ResultSet rs) throws SQLException {
        return UtilisateurRole.builder()
                .idUtilisateurRole(rs.getInt("idUtilisateurRole"))
                .idUtilisateur(rs.getInt("idUtilisateur"))
                .idRole(rs.getInt("idRole"))
                .dateAttribution(rs.getTimestamp("dateAttribution"))
                .build();
    }
}