package fr.esgi.color_run.service.impl;

import java.util.List;
import java.util.Optional;

import fr.esgi.color_run.business.Role;
import fr.esgi.color_run.dao.DAOException;
import fr.esgi.color_run.dao.RoleDAO;
import fr.esgi.color_run.service.RoleService;
import fr.esgi.color_run.service.ServiceException;

public class RoleServiceImpl implements RoleService {

    private final RoleDAO roleDAO;

    public RoleServiceImpl(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    @Override
    public Role creerRole(Role role) throws ServiceException {
        try {
            // Validation des données
            if (role.getNomRole() == null || role.getNomRole().trim().isEmpty()) {
                throw new ServiceException("Le nom du rôle est obligatoire");
            }
            
            // Vérifier si le rôle existe déjà
            if (roleDAO.findByName(role.getNomRole()).isPresent()) {
                throw new ServiceException("Un rôle avec ce nom existe déjà");
            }
            
            return roleDAO.create(role);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la création du rôle", e);
        }
    }

    @Override
    public Optional<Role> trouverParId(int id) throws ServiceException {
        try {
            return roleDAO.findById(id);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la recherche du rôle par ID", e);
        }
    }

    @Override
    public Optional<Role> trouverParNom(String nomRole) throws ServiceException {
        try {
            return roleDAO.findByName(nomRole);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la recherche du rôle par nom", e);
        }
    }

    @Override
    public List<Role> trouverTous() throws ServiceException {
        try {
            return roleDAO.findAll();
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la récupération de tous les rôles", e);
        }
    }

    @Override
    public Role mettreAJour(Role role) throws ServiceException {
        try {
            // Validation des données
            if (role.getNomRole() == null || role.getNomRole().trim().isEmpty()) {
                throw new ServiceException("Le nom du rôle est obligatoire");
            }
            
            // Vérifier si le rôle existe
            if (!roleDAO.findById(role.getIdRole()).isPresent()) {
                throw new ServiceException("Rôle non trouvé avec l'ID: " + role.getIdRole());
            }
            
            // Vérifier si le nom est déjà utilisé par un autre rôle
            Optional<Role> existingRole = roleDAO.findByName(role.getNomRole());
            if (existingRole.isPresent() && existingRole.get().getIdRole() != role.getIdRole()) {
                throw new ServiceException("Un autre rôle avec ce nom existe déjà");
            }
            
            return roleDAO.update(role);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la mise à jour du rôle", e);
        }
    }

    @Override
    public void supprimer(int id) throws ServiceException {
        try {
            // Vérifier si le rôle existe
            if (!roleDAO.findById(id).isPresent()) {
                throw new ServiceException("Rôle non trouvé avec l'ID: " + id);
            }
            
            // Vérifier si des utilisateurs ont ce rôle (cette vérification devrait être faite au niveau de la base de données avec une contrainte de clé étrangère)
            
            roleDAO.delete(id);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la suppression du rôle", e);
        }
    }
}