package fr.esgi.color_run.dao;

import java.util.List;

import fr.esgi.color_run.business.DemandeOrganisateur;

public interface DemandeOrganisateurDAO extends GenericDAO<DemandeOrganisateur, Integer> {
    List<DemandeOrganisateur> findByUserId(int idUtilisateur) throws DAOException;
    List<DemandeOrganisateur> findByStatus(String statut) throws DAOException;
    void updateStatus(int idDemande, String statut, String commentaire) throws DAOException;
}
