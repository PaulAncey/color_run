package fr.esgi.color_run.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.esgi.color_run.business.Role;
import fr.esgi.color_run.config.DatabaseConfig;
import fr.esgi.color_run.dao.DAOException;
import fr.esgi.color_run.dao.RoleDAO;

public class RoleDAOImpl implements RoleDAO {

    @Override
    public Role create(Role role) throws DAOException {
        String sql = "INSERT INTO Role (nomRole, description) VALUES (?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, role.getNomRole());
            stmt.setString(2, role.getDescription());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("La création du rôle a échoué, aucune ligne affectée.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                role.setIdRole(rs.getInt(1));
            } else {
                throw new DAOException("La création du rôle a échoué, aucun ID obtenu.");
            }
            
            return role;
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la création du rôle: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public Optional<Role> findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM Role WHERE idRole = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapRowToRole(rs));
            } else {
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche du rôle: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public List<Role> findAll() throws DAOException {
        String sql = "SELECT * FROM Role";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Role> roles = new ArrayList<>();
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                roles.add(mapRowToRole(rs));
            }
            
            return roles;
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération de tous les rôles: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public Role update(Role role) throws DAOException {
        String sql = "UPDATE Role SET nomRole = ?, description = ? WHERE idRole = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, role.getNomRole());
            stmt.setString(2, role.getDescription());
            stmt.setInt(3, role.getIdRole());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("La mise à jour du rôle a échoué, aucun rôle trouvé avec l'ID: " 
                        + role.getIdRole());
            }
            
            return role;
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la mise à jour du rôle: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(stmt, conn);
        }
    }

    @Override
    public void delete(Integer id) throws DAOException {
        String sql = "DELETE FROM Role WHERE idRole = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("La suppression du rôle a échoué, aucun rôle trouvé avec l'ID: " + id);
            }
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression du rôle: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(stmt, conn);
        }
    }

    @Override
    public Optional<Role> findByName(String nomRole) throws DAOException {
        String sql = "SELECT * FROM Role WHERE nomRole = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nomRole);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapRowToRole(rs));
            } else {
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche du rôle par nom: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }
    
    /**
     * Transforme une ligne de résultat en objet Role
     */
    private Role mapRowToRole(ResultSet rs) throws SQLException {
        return Role.builder()
                .idRole(rs.getInt("idRole"))
                .nomRole(rs.getString("nomRole"))
                .description(rs.getString("description"))
                .build();
    }
}