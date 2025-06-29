package fr.esgi.color_run.service;

import java.util.List;
import java.util.Optional;

import fr.esgi.color_run.business.DemandeOrganisateur;
import fr.esgi.color_run.business.DemandeOrganisateurWithUser;

public interface DemandeOrganisateurService {
    DemandeOrganisateur creerDemande(DemandeOrganisateur demande) throws ServiceException;

    Optional<DemandeOrganisateur> trouverParId(int id) throws ServiceException;

    List<DemandeOrganisateur> trouverParUtilisateur(int idUtilisateur) throws ServiceException;

    List<DemandeOrganisateur> trouverParStatut(String statut) throws ServiceException;

    List<DemandeOrganisateur> trouverTous() throws ServiceException;

    List<DemandeOrganisateurWithUser> trouverParStatutAvecUtilisateur(String statut) throws ServiceException;

    List<DemandeOrganisateurWithUser> trouverTousAvecUtilisateur() throws ServiceException;

    void traiterDemande(int idDemande, String statut, String commentaire) throws ServiceException;

    boolean peutDemanderRole(int idUtilisateur) throws ServiceException;
}
