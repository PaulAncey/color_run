package fr.esgi.color_run.dao.impl;

import fr.esgi.color_run.business.Role;
import fr.esgi.color_run.business.Utilisateur;
import fr.esgi.color_run.dao.DAOException;
import fr.esgi.color_run.dao.UtilisateurDAO;
import fr.esgi.color_run.database.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UtilisateurDAOImpl implements UtilisateurDAO {

    @Override
    public Utilisateur create(Utilisateur utilisateur) throws DAOException {
        String sql = "INSERT INTO Utilisateur (nom, prenom, email, motDePasse, photoProfile, compteVerifie) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, utilisateur.getNom());
            stmt.setString(2, utilisateur.getPrenom());
            stmt.setString(3, utilisateur.getEmail());
            stmt.setString(4, utilisateur.getMotDePasse()); // Idéalement, il faudrait hacher le mot de passe
            stmt.setString(5, utilisateur.getPhotoProfile());
            stmt.setBoolean(6, utilisateur.isCompteVerifie());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DAOException("La création de l'utilisateur a échoué, aucune ligne affectée.");
            }

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                utilisateur.setIdUtilisateur(rs.getInt(1));
                utilisateur.setDateCreation(rs.getTimestamp(2));
            } else {
                throw new DAOException("La création de l'utilisateur a échoué, aucun ID obtenu.");
            }

            return utilisateur;

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la création de l'utilisateur: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public Optional<Utilisateur> findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM Utilisateur WHERE idUtilisateur = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToUtilisateur(rs));
            } else {
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche de l'utilisateur: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public List<Utilisateur> findAll() throws DAOException {
        String sql = "SELECT * FROM Utilisateur";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Utilisateur> utilisateurs = new ArrayList<>();

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                utilisateurs.add(mapRowToUtilisateur(rs));
            }

            return utilisateurs;

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération de tous les utilisateurs: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public Utilisateur update(Utilisateur utilisateur) throws DAOException {
        String sql = "UPDATE Utilisateur SET nom = ?, prenom = ?, email = ?, " +
                "motDePasse = ?, photoProfile = ?, compteVerifie = ? " +
                "WHERE idUtilisateur = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, utilisateur.getNom());
            stmt.setString(2, utilisateur.getPrenom());
            stmt.setString(3, utilisateur.getEmail());
            stmt.setString(4, utilisateur.getMotDePasse());
            stmt.setString(5, utilisateur.getPhotoProfile());
            stmt.setBoolean(6, utilisateur.isCompteVerifie());
            stmt.setInt(7, utilisateur.getIdUtilisateur());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DAOException("La mise à jour de l'utilisateur a échoué, aucun utilisateur trouvé avec l'ID: "
                        + utilisateur.getIdUtilisateur());
            }

            return utilisateur;

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(stmt, conn);
        }
    }

    @Override
    public void delete(Integer id) throws DAOException {
        String sql = "DELETE FROM Utilisateur WHERE idUtilisateur = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DAOException(
                        "La suppression de l'utilisateur a échoué, aucun utilisateur trouvé avec l'ID: " + id);
            }

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression de l'utilisateur: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(stmt, conn);
        }
    }

    @Override
    public Optional<Utilisateur> findByEmail(String email) throws DAOException {
        String sql = "SELECT * FROM Utilisateur WHERE email = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);

            rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToUtilisateur(rs));
            } else {
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche de l'utilisateur par email: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public boolean verifyPassword(String email, String password) throws DAOException {
        String sql = "SELECT motDePasse FROM Utilisateur WHERE email = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);

            rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("motDePasse");
                // Dans une application réelle, il faudrait utiliser un algorithme de hachage
                // comme BCrypt
                return storedPassword.equals(password);
            } else {
                return false;
            }

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la vérification du mot de passe: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public List<Role> getUserRoles(int idUtilisateur) throws DAOException {
        String sql = "SELECT r.* FROM Role r " +
                "JOIN UtilisateurRole ur ON r.idRole = ur.idRole " +
                "WHERE ur.idUtilisateur = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Role> roles = new ArrayList<>();

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUtilisateur);

            rs = stmt.executeQuery();

            while (rs.next()) {
                Role role = Role.builder()
                        .idRole(rs.getInt("idRole"))
                        .nomRole(rs.getString("nomRole"))
                        .description(rs.getString("description"))
                        .build();

                roles.add(role);
            }

            return roles;

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération des rôles de l'utilisateur: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    /**
     * Transforme une ligne de résultat en objet Utilisateur
     */
    private Utilisateur mapRowToUtilisateur(ResultSet rs) throws SQLException {
        return Utilisateur.builder()
                .idUtilisateur(rs.getInt("idUtilisateur"))
                .nom(rs.getString("nom"))
                .prenom(rs.getString("prenom"))
                .email(rs.getString("email"))
                .motDePasse(rs.getString("motDePasse"))
                .photoProfile(rs.getString("photoProfile"))
                .compteVerifie(rs.getBoolean("compteVerifie"))
                .dateCreation(rs.getTimestamp("dateCreation"))
                .build();
    }
}