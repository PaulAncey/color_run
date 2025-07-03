package fr.esgi.color_run.service.impl;

import java.util.List;
import java.util.Optional;

import fr.esgi.color_run.business.Role;
import fr.esgi.color_run.business.Utilisateur;
import fr.esgi.color_run.dao.DAOException;
import fr.esgi.color_run.dao.RoleDAO;
import fr.esgi.color_run.dao.UtilisateurDAO;
import fr.esgi.color_run.dao.UtilisateurRoleDAO;
import fr.esgi.color_run.service.ServiceException;
import fr.esgi.color_run.service.UtilisateurService;

import fr.esgi.color_run.business.PasswordResetToken;
import fr.esgi.color_run.dao.PasswordResetTokenDAO;
import fr.esgi.color_run.dao.impl.PasswordResetTokenDAOImpl;
import fr.esgi.color_run.service.EmailService;
import fr.esgi.color_run.service.impl.EmailServiceImpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import fr.esgi.color_run.service.EmailServiceImpl;


public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurDAO utilisateurDAO;
    private final RoleDAO roleDAO;
    private final UtilisateurRoleDAO utilisateurRoleDAO;

    private final PasswordResetTokenDAO passwordResetTokenDAO;
    private final EmailService emailService;

    public UtilisateurServiceImpl(UtilisateurDAO utilisateurDAO, RoleDAO roleDAO, UtilisateurRoleDAO utilisateurRoleDAO) {
        this.utilisateurDAO = utilisateurDAO;
        this.roleDAO = roleDAO;
        this.utilisateurRoleDAO = utilisateurRoleDAO;
        this.passwordResetTokenDAO = new PasswordResetTokenDAOImpl();
        this.emailService = new EmailServiceImpl();
    }

    @Override
    public Utilisateur creerUtilisateur(Utilisateur utilisateur) throws ServiceException {
        try {
            // Vérifier si l'email existe déjà
            if (utilisateurDAO.findByEmail(utilisateur.getEmail()).isPresent()) {
                throw new ServiceException("Un utilisateur avec cet email existe déjà");
            }

            // Sécuriser le mot de passe (dans une application réelle, utiliser BCrypt)
            // utilisateur.setMotDePasse(hashPassword(utilisateur.getMotDePasse()));

            // Créer l'utilisateur
            Utilisateur nouvelUtilisateur = utilisateurDAO.create(utilisateur);

            // Attribuer le rôle PARTICIPANT par défaut
            attribuerRole(nouvelUtilisateur.getIdUtilisateur(), "PARTICIPANT");

            return nouvelUtilisateur;
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la création de l'utilisateur", e);
        }
    }

    @Override
    public Optional<Utilisateur> trouverParId(int id) throws ServiceException {
        try {
            return utilisateurDAO.findById(id);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la recherche de l'utilisateur par ID", e);
        }
    }

    @Override
    public Optional<Utilisateur> trouverParEmail(String email) throws ServiceException {
        try {
            return utilisateurDAO.findByEmail(email);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la recherche de l'utilisateur par email", e);
        }
    }


    @Override
    public Utilisateur mettreAJour(Utilisateur utilisateur) throws ServiceException {
        try {
            // Vérifier si l'utilisateur existe
            if (!utilisateurDAO.findById(utilisateur.getIdUtilisateur()).isPresent()) {
                throw new ServiceException("Utilisateur non trouvé avec l'ID: " + utilisateur.getIdUtilisateur());
            }

            // Vérifier si l'email est déjà utilisé par un autre utilisateur
            Optional<Utilisateur> existingUser = utilisateurDAO.findByEmail(utilisateur.getEmail());
            if (existingUser.isPresent() && existingUser.get().getIdUtilisateur() != utilisateur.getIdUtilisateur()) {
                throw new ServiceException("Cet email est déjà utilisé par un autre utilisateur");
            }

            return utilisateurDAO.update(utilisateur);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la mise à jour de l'utilisateur", e);
        }
    }

    @Override
    public void supprimer(int id) throws ServiceException {
        try {
            // Vérifier si l'utilisateur existe
            if (!utilisateurDAO.findById(id).isPresent()) {
                throw new ServiceException("Utilisateur non trouvé avec l'ID: " + id);
            }

            utilisateurDAO.delete(id);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la suppression de l'utilisateur", e);
        }
    }

    @Override
    public boolean authentifier(String email, String motDePasse) throws ServiceException {
        try {
            return utilisateurDAO.verifyPassword(email, motDePasse);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de l'authentification", e);
        }
    }

    @Override
    public List<Role> obtenirRolesUtilisateur(int idUtilisateur) throws ServiceException {
        try {
            return utilisateurDAO.getUserRoles(idUtilisateur);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la récupération des rôles de l'utilisateur", e);
        }
    }

    @Override
    public boolean aRole(int idUtilisateur, String nomRole) throws ServiceException {
        try {
            List<Role> roles = obtenirRolesUtilisateur(idUtilisateur);
            return roles.stream().anyMatch(role -> role.getNomRole().equals(nomRole));
        } catch (ServiceException e) {
            throw new ServiceException("Erreur lors de la vérification du rôle de l'utilisateur", e);
        }
    }

    @Override
    public void attribuerRole(int idUtilisateur, String nomRole) throws ServiceException {
        try {
            // Vérifier si l'utilisateur existe
            if (!utilisateurDAO.findById(idUtilisateur).isPresent()) {
                throw new ServiceException("Utilisateur non trouvé avec l'ID: " + idUtilisateur);
            }

            // Vérifier si le rôle existe
            Optional<Role> role = roleDAO.findByName(nomRole);
            if (!role.isPresent()) {
                throw new ServiceException("Rôle non trouvé avec le nom: " + nomRole);
            }

            // Vérifier si l'utilisateur a déjà ce rôle
            if (aRole(idUtilisateur, nomRole)) {
                return; // L'utilisateur a déjà ce rôle, rien à faire
            }

            // Attribuer le rôle
            utilisateurRoleDAO.assignRoleToUser(idUtilisateur, role.get().getIdRole());
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de l'attribution du rôle à l'utilisateur", e);
        }
    }

    @Override
    public void retirerRole(int idUtilisateur, String nomRole) throws ServiceException {
        try {
            // Vérifier si l'utilisateur existe
            if (!utilisateurDAO.findById(idUtilisateur).isPresent()) {
                throw new ServiceException("Utilisateur non trouvé avec l'ID: " + idUtilisateur);
            }

            // Vérifier si le rôle existe
            Optional<Role> role = roleDAO.findByName(nomRole);
            if (!role.isPresent()) {
                throw new ServiceException("Rôle non trouvé avec le nom: " + nomRole);
            }

            // Vérifier si l'utilisateur a ce rôle
            if (!aRole(idUtilisateur, nomRole)) {
                return; // L'utilisateur n'a pas ce rôle, rien à faire
            }

            // Retirer le rôle
            utilisateurRoleDAO.removeRoleFromUser(idUtilisateur, role.get().getIdRole());
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors du retrait du rôle de l'utilisateur", e);
        }
    }

    // Méthode utilitaire pour hacher un mot de passe (à implémenter avec BCrypt dans une app réelle)
    private String hashPassword(String password) {
        // Dans une application réelle, utiliser un algorithme comme BCrypt
        return password; // Pour simplifier, on retourne le mot de passe en clair
    }

    @Override
public void demanderResetMotDePasse(String email) throws ServiceException {
    try {
        // Vérifier si l'utilisateur existe
        Optional<Utilisateur> optUtilisateur = utilisateurDAO.findByEmail(email);
        if (!optUtilisateur.isPresent()) {
            // Ne pas révéler si l'email existe ou non pour la sécurité
            // Simuler un envoi d'email
            System.out.println("Tentative de reset pour email inexistant: " + email);
            return;
        }
        
        Utilisateur utilisateur = optUtilisateur.get();
        
        // Générer un token unique
        String token = UUID.randomUUID().toString();
        
        // Créer le token avec expiration dans 24h
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .idUtilisateur(utilisateur.getIdUtilisateur())
                .token(token)
                .dateExpiration(Timestamp.valueOf(LocalDateTime.now().plusHours(24)))
                .utilise(false)
                .build();
        
        // Sauvegarder le token
        passwordResetTokenDAO.save(resetToken);
        
        // Envoyer l'email
        emailService.envoyerEmailResetMotDePasse(email, token);
        
    } catch (DAOException e) {
        throw new ServiceException("Erreur lors de la demande de reset de mot de passe", e);
    }
}

@Override
public void resetMotDePasse(String token, String nouveauMotDePasse) throws ServiceException {
    try {
        // Vérifier si le token existe et est valide
        Optional<PasswordResetToken> optToken = passwordResetTokenDAO.findByToken(token);
        if (!optToken.isPresent()) {
            throw new ServiceException("Token invalide ou expiré");
        }
        
        PasswordResetToken resetToken = optToken.get();
        
        // Récupérer l'utilisateur
        Optional<Utilisateur> optUtilisateur = utilisateurDAO.findById(resetToken.getIdUtilisateur());
        if (!optUtilisateur.isPresent()) {
            throw new ServiceException("Utilisateur non trouvé");
        }
        
        Utilisateur utilisateur = optUtilisateur.get();
        
        // Mettre à jour le mot de passe (à hasher en production)
        utilisateur.setMotDePasse(nouveauMotDePasse);
        utilisateurDAO.update(utilisateur);
        
        // Marquer le token comme utilisé
        passwordResetTokenDAO.markAsUsed(token);
        
    } catch (DAOException e) {
        throw new ServiceException("Erreur lors du reset de mot de passe", e);
    }
}

@Override
public int compterTousLesUtilisateurs() throws ServiceException {
    return utilisateurDAO.countAll();
}

@Override
public List<Utilisateur> trouverTous() throws ServiceException {
    try {
        return utilisateurDAO.findAll();
    } catch (DAOException e) {
        throw new ServiceException("Erreur lors de la récupération de tous les utilisateurs", e);
    }
}
}