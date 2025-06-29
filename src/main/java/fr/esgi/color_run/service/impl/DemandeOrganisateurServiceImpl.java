package fr.esgi.color_run.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.esgi.color_run.business.DemandeOrganisateur;
import fr.esgi.color_run.business.DemandeOrganisateurWithUser;
import fr.esgi.color_run.business.Role;
import fr.esgi.color_run.business.Utilisateur;
import fr.esgi.color_run.dao.DAOException;
import fr.esgi.color_run.dao.DemandeOrganisateurDAO;
import fr.esgi.color_run.dao.RoleDAO;
import fr.esgi.color_run.dao.UtilisateurDAO;
import fr.esgi.color_run.service.DemandeOrganisateurService;
import fr.esgi.color_run.service.ServiceException;

public class DemandeOrganisateurServiceImpl implements DemandeOrganisateurService {

    private final DemandeOrganisateurDAO demandeOrganisateurDAO;
    private final UtilisateurDAO utilisateurDAO;
    private final RoleDAO roleDAO;

    public DemandeOrganisateurServiceImpl(DemandeOrganisateurDAO demandeOrganisateurDAO, UtilisateurDAO utilisateurDAO,
            RoleDAO roleDAO) {
        this.demandeOrganisateurDAO = demandeOrganisateurDAO;
        this.utilisateurDAO = utilisateurDAO;
        this.roleDAO = roleDAO;
    }

    @Override
    public DemandeOrganisateur creerDemande(DemandeOrganisateur demande) throws ServiceException {
        try {
            // Validation des données
            if (demande.getMotif() == null || demande.getMotif().trim().isEmpty()) {
                throw new ServiceException("Le motif de la demande est obligatoire");
            }

            if (demande.getIdUtilisateur() <= 0) {
                throw new ServiceException("L'ID de l'utilisateur est invalide");
            }

            // Vérifier si l'utilisateur existe
            if (!utilisateurDAO.findById(demande.getIdUtilisateur()).isPresent()) {
                throw new ServiceException("Utilisateur non trouvé avec l'ID: " + demande.getIdUtilisateur());
            }

            // Vérifier si l'utilisateur est déjà organisateur
            List<Role> roles = utilisateurDAO.getUserRoles(demande.getIdUtilisateur());
            boolean isOrganisateur = roles.stream().anyMatch(role -> role.getNomRole().equals("ORGANISATEUR"));

            if (isOrganisateur) {
                throw new ServiceException("L'utilisateur est déjà organisateur");
            }

            // Vérifier si l'utilisateur a déjà une demande en attente
            List<DemandeOrganisateur> demandes = demandeOrganisateurDAO.findByUserId(demande.getIdUtilisateur());
            boolean hasActiveRequest = demandes.stream().anyMatch(d -> d.getStatut().equals("EN_ATTENTE"));

            if (hasActiveRequest) {
                throw new ServiceException("L'utilisateur a déjà une demande en attente");
            }

            // Définir le statut par défaut si non spécifié
            if (demande.getStatut() == null || demande.getStatut().trim().isEmpty()) {
                demande.setStatut("EN_ATTENTE");
            }

            // Définir la date de demande si elle n'est pas spécifiée
            if (demande.getDateDemande() == null) {
                demande.setDateDemande(new Timestamp(System.currentTimeMillis()));
            }

            return demandeOrganisateurDAO.create(demande);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la création de la demande d'organisateur", e);
        }
    }

    @Override
    public Optional<DemandeOrganisateur> trouverParId(int id) throws ServiceException {
        try {
            return demandeOrganisateurDAO.findById(id);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la recherche de la demande d'organisateur par ID", e);
        }
    }

    @Override
    public List<DemandeOrganisateur> trouverParUtilisateur(int idUtilisateur) throws ServiceException {
        try {
            return demandeOrganisateurDAO.findByUserId(idUtilisateur);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la recherche des demandes d'organisateur par utilisateur", e);
        }
    }

    @Override
    public List<DemandeOrganisateur> trouverParStatut(String statut) throws ServiceException {
        try {
            return demandeOrganisateurDAO.findByStatus(statut);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la recherche des demandes d'organisateur par statut", e);
        }
    }

    @Override
    public List<DemandeOrganisateur> trouverTous() throws ServiceException {
        try {
            return demandeOrganisateurDAO.findAll();
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la récupération de toutes les demandes d'organisateur", e);
        }
    }

    @Override
    public void traiterDemande(int idDemande, String statut, String commentaire) throws ServiceException {
        try {
            // Validation des données
            if (statut == null || statut.trim().isEmpty() ||
                    (!statut.equals("ACCEPTEE") && !statut.equals("REFUSEE"))) {
                throw new ServiceException("Le statut doit être 'ACCEPTEE' ou 'REFUSEE'");
            }

            // Vérifier si la demande existe
            Optional<DemandeOrganisateur> optDemande = demandeOrganisateurDAO.findById(idDemande);
            if (!optDemande.isPresent()) {
                throw new ServiceException("Demande non trouvée avec l'ID: " + idDemande);
            }

            // Vérifier si la demande est déjà traitée
            DemandeOrganisateur demande = optDemande.get();
            if (!demande.getStatut().equals("EN_ATTENTE")) {
                throw new ServiceException("La demande a déjà été traitée");
            }

            // Mettre à jour le statut de la demande
            demandeOrganisateurDAO.updateStatus(idDemande, statut, commentaire);

        } catch (DAOException e) {
            throw new ServiceException("Erreur lors du traitement de la demande d'organisateur", e);
        }
    }

    @Override
    public boolean peutDemanderRole(int idUtilisateur) throws ServiceException {
        try {
            // Vérifier si l'utilisateur existe
            if (!utilisateurDAO.findById(idUtilisateur).isPresent()) {
                throw new ServiceException("Utilisateur non trouvé avec l'ID: " + idUtilisateur);
            }

            // Vérifier si l'utilisateur est déjà organisateur
            List<Role> roles = utilisateurDAO.getUserRoles(idUtilisateur);
            boolean isOrganisateur = roles.stream().anyMatch(role -> role.getNomRole().equals("ORGANISATEUR"));

            if (isOrganisateur) {
                return false; // Déjà organisateur
            }

            // Vérifier si l'utilisateur a déjà une demande en attente
            List<DemandeOrganisateur> demandes = demandeOrganisateurDAO.findByUserId(idUtilisateur);
            boolean hasActiveRequest = demandes.stream().anyMatch(d -> d.getStatut().equals("EN_ATTENTE"));

            return !hasActiveRequest; // Peut demander si aucune demande en attente

        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la vérification de la possibilité de demander le rôle", e);
        }
    }

    @Override
    public List<DemandeOrganisateurWithUser> trouverParStatutAvecUtilisateur(String statut) throws ServiceException {
        try {
            List<DemandeOrganisateur> demandes = demandeOrganisateurDAO.findByStatus(statut);
            List<DemandeOrganisateurWithUser> result = new ArrayList<>();

            for (DemandeOrganisateur demande : demandes) {
                Optional<Utilisateur> optUtilisateur = utilisateurDAO.findById(demande.getIdUtilisateur());
                if (optUtilisateur.isPresent()) {
                    result.add(DemandeOrganisateurWithUser.from(demande, optUtilisateur.get()));
                }
            }

            return result;
        } catch (DAOException e) {
            throw new ServiceException(
                    "Erreur lors de la recherche des demandes d'organisateur par statut avec utilisateur", e);
        }
    }

    @Override
    public List<DemandeOrganisateurWithUser> trouverTousAvecUtilisateur() throws ServiceException {
        try {
            List<DemandeOrganisateur> demandes = demandeOrganisateurDAO.findAll();
            List<DemandeOrganisateurWithUser> result = new ArrayList<>();

            for (DemandeOrganisateur demande : demandes) {
                Optional<Utilisateur> optUtilisateur = utilisateurDAO.findById(demande.getIdUtilisateur());
                if (optUtilisateur.isPresent()) {
                    result.add(DemandeOrganisateurWithUser.from(demande, optUtilisateur.get()));
                }
            }

            return result;
        } catch (DAOException e) {
            throw new ServiceException(
                    "Erreur lors de la récupération de toutes les demandes d'organisateur avec utilisateur", e);
        }
    }
}