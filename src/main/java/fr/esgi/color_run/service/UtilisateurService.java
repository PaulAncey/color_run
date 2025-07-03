package fr.esgi.color_run.service;

import java.util.List;
import java.util.Optional;

import fr.esgi.color_run.business.Role;
import fr.esgi.color_run.business.Utilisateur;

public interface UtilisateurService {
    Utilisateur creerUtilisateur(Utilisateur utilisateur) throws ServiceException;
    Optional<Utilisateur> trouverParId(int id) throws ServiceException;
    Optional<Utilisateur> trouverParEmail(String email) throws ServiceException;
    List<Utilisateur> trouverTous() throws ServiceException;
    Utilisateur mettreAJour(Utilisateur utilisateur) throws ServiceException;
    void supprimer(int id) throws ServiceException;
    boolean authentifier(String email, String motDePasse) throws ServiceException;
    List<Role> obtenirRolesUtilisateur(int idUtilisateur) throws ServiceException;
    boolean aRole(int idUtilisateur, String nomRole) throws ServiceException;
    void attribuerRole(int idUtilisateur, String nomRole) throws ServiceException;
    void retirerRole(int idUtilisateur, String nomRole) throws ServiceException;
    void demanderResetMotDePasse(String email) throws ServiceException;
    void resetMotDePasse(String token, String nouveauMotDePasse) throws ServiceException;
    int compterTousLesUtilisateurs() throws ServiceException;
}