package fr.esgi.color_run.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.esgi.color_run.business.DemandeOrganisateur;
import fr.esgi.color_run.dao.DAOException;
import fr.esgi.color_run.dao.DemandeOrganisateurDAO;
import fr.esgi.color_run.database.DatabaseConfig;

public class DemandeOrganisateurDAOImpl implements DemandeOrganisateurDAO {

    @Override
    public DemandeOrganisateur create(DemandeOrganisateur demande) throws DAOException {
        String sql = "INSERT INTO DemandeOrganisateur (idUtilisateur, motif, statut) VALUES (?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, demande.getIdUtilisateur());
            stmt.setString(2, demande.getMotif());
            stmt.setString(3, demande.getStatut());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DAOException("La création de la demande d'organisateur a échoué, aucune ligne affectée.");
            }

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                demande.setIdDemande(rs.getInt(1));
                demande.setDateDemande(new Timestamp(System.currentTimeMillis()));
            } else {
                throw new DAOException("La création de la demande d'organisateur a échoué, aucun ID obtenu.");
            }

            return demande;

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la création de la demande d'organisateur: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public Optional<DemandeOrganisateur> findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM DemandeOrganisateur WHERE idDemande = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToDemandeOrganisateur(rs));
            } else {
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche de la demande d'organisateur: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public List<DemandeOrganisateur> findAll() throws DAOException {
        String sql = "SELECT * FROM DemandeOrganisateur ORDER BY dateDemande DESC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<DemandeOrganisateur> demandes = new ArrayList<>();

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                demandes.add(mapRowToDemandeOrganisateur(rs));
            }

            return demandes;

        } catch (SQLException e) {
            throw new DAOException(
                    "Erreur lors de la récupération de toutes les demandes d'organisateur: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public DemandeOrganisateur update(DemandeOrganisateur demande) throws DAOException {
        String sql = "UPDATE DemandeOrganisateur SET idUtilisateur = ?, motif = ?, statut = ?, " +
                "dateTraitement = ?, commentaireAdmin = ? WHERE idDemande = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, demande.getIdUtilisateur());
            stmt.setString(2, demande.getMotif());
            stmt.setString(3, demande.getStatut());

            if (demande.getDateTraitement() != null) {
                stmt.setTimestamp(4, demande.getDateTraitement());
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }

            stmt.setString(5, demande.getCommentaireAdmin());
            stmt.setInt(6, demande.getIdDemande());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DAOException(
                        "La mise à jour de la demande d'organisateur a échoué, aucune demande trouvée avec l'ID: "
                                + demande.getIdDemande());
            }

            return demande;

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la mise à jour de la demande d'organisateur: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(stmt, conn);
        }
    }

    @Override
    public void delete(Integer id) throws DAOException {
        String sql = "DELETE FROM DemandeOrganisateur WHERE idDemande = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DAOException(
                        "La suppression de la demande d'organisateur a échoué, aucune demande trouvée avec l'ID: "
                                + id);
            }

        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression de la demande d'organisateur: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(stmt, conn);
        }
    }

    @Override
    public List<DemandeOrganisateur> findByUserId(int idUtilisateur) throws DAOException {
        String sql = "SELECT * FROM DemandeOrganisateur WHERE idUtilisateur = ? ORDER BY dateDemande DESC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<DemandeOrganisateur> demandes = new ArrayList<>();

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUtilisateur);

            rs = stmt.executeQuery();

            while (rs.next()) {
                demandes.add(mapRowToDemandeOrganisateur(rs));
            }

            return demandes;

        } catch (SQLException e) {
            throw new DAOException(
                    "Erreur lors de la recherche des demandes d'organisateur par utilisateur: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public List<DemandeOrganisateur> findByStatus(String statut) throws DAOException {
        String sql = "SELECT * FROM DemandeOrganisateur WHERE statut = ? ORDER BY dateDemande ASC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<DemandeOrganisateur> demandes = new ArrayList<>();

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, statut);

            rs = stmt.executeQuery();

            while (rs.next()) {
                demandes.add(mapRowToDemandeOrganisateur(rs));
            }

            return demandes;

        } catch (SQLException e) {
            throw new DAOException(
                    "Erreur lors de la recherche des demandes d'organisateur par statut: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public void updateStatus(int idDemande, String statut, String commentaire) throws DAOException {
        String sql = "UPDATE DemandeOrganisateur SET statut = ?, dateTraitement = ?, commentaireAdmin = ? " +
                "WHERE idDemande = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, statut);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setString(3, commentaire);
            stmt.setInt(4, idDemande);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DAOException(
                        "La mise à jour du statut de la demande d'organisateur a échoué, aucune demande trouvée avec l'ID: "
                                + idDemande);
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Erreur lors de la mise à jour du statut de la demande d'organisateur: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(stmt, conn);
        }
    }

    /**
     * Transforme une ligne de résultat en objet DemandeOrganisateur
     */
    private DemandeOrganisateur mapRowToDemandeOrganisateur(ResultSet rs) throws SQLException {
        return DemandeOrganisateur.builder()
                .idDemande(rs.getInt("idDemande"))
                .idUtilisateur(rs.getInt("idUtilisateur"))
                .motif(rs.getString("motif"))
                .statut(rs.getString("statut"))
                .dateDemande(rs.getTimestamp("dateDemande"))
                .dateTraitement(rs.getTimestamp("dateTraitement"))
                .commentaireAdmin(rs.getString("commentaireAdmin"))
                .build();
    }
}