package fr.esgi.color_run.service;

import java.util.List;
import java.util.Optional;

import fr.esgi.color_run.business.Role;

public interface RoleService {
    Role creerRole(Role role) throws ServiceException;
    Optional<Role> trouverParId(int id) throws ServiceException;
    Optional<Role> trouverParNom(String nomRole) throws ServiceException;
    List<Role> trouverTous() throws ServiceException;
    Role mettreAJour(Role role) throws ServiceException;
    void supprimer(int id) throws ServiceException;
}