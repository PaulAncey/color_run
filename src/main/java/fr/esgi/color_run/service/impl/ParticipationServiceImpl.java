package fr.esgi.color_run.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.esgi.color_run.business.Course;
import fr.esgi.color_run.business.Participation;
import fr.esgi.color_run.business.ParticipationWithCourse;
import fr.esgi.color_run.business.Utilisateur;
import fr.esgi.color_run.dao.CourseDAO;
import fr.esgi.color_run.dao.DAOException;
import fr.esgi.color_run.dao.ParticipationDAO;
import fr.esgi.color_run.dao.UtilisateurDAO;
import fr.esgi.color_run.service.ParticipationService;
import fr.esgi.color_run.service.ServiceException;
import fr.esgi.color_run.util.PDFGenerator;

public class ParticipationServiceImpl implements ParticipationService {

    private final ParticipationDAO participationDAO;
    private final CourseDAO courseDAO;
    private final UtilisateurDAO utilisateurDAO;

    public ParticipationServiceImpl(ParticipationDAO participationDAO, CourseDAO courseDAO, UtilisateurDAO utilisateurDAO) {
        this.participationDAO = participationDAO;
        this.courseDAO = courseDAO;
        this.utilisateurDAO = utilisateurDAO;
    }

    @Override
    public Participation inscrire(int idUtilisateur, int idCourse) throws ServiceException {
        try {
            // Vérifier si l'utilisateur existe
            Optional<Utilisateur> utilisateur = utilisateurDAO.findById(idUtilisateur);
            if (!utilisateur.isPresent()) {
                throw new ServiceException("Utilisateur non trouvé avec l'ID: " + idUtilisateur);
            }
            
            // Vérifier si la course existe
            Optional<Course> course = courseDAO.findById(idCourse);
            if (!course.isPresent()) {
                throw new ServiceException("Course non trouvée avec l'ID: " + idCourse);
            }
            
            // Vérifier si l'utilisateur est déjà inscrit à cette course
            if (participationDAO.findByUserAndCourse(idUtilisateur, idCourse).isPresent()) {
                throw new ServiceException("L'utilisateur est déjà inscrit à cette course");
            }
            
            // Vérifier si la course est complète
            int nbParticipants = participationDAO.countParticipantsByCourse(idCourse);
            if (nbParticipants >= course.get().getNbMaxParticipants()) {
                throw new ServiceException("La course est complète, plus d'inscriptions possibles");
            }
            
            // Obtenir le prochain numéro de dossard
            int numeroDossard = participationDAO.getNextDossardNumber(idCourse);
            
            // Créer la participation
            Participation participation = Participation.builder()
                    .idUtilisateur(idUtilisateur)
                    .idCourse(idCourse)
                    .numeroDossard(numeroDossard)
                    .dateInscription(new Timestamp(System.currentTimeMillis()))
                    .dossardTelecharge(false)
                    .build();
            
            return participationDAO.create(participation);
            
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de l'inscription à la course", e);
        }
    }

    @Override
    public Optional<Participation> trouverParId(int id) throws ServiceException {
        try {
            return participationDAO.findById(id);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la recherche de la participation par ID", e);
        }
    }

    @Override
    public List<Participation> trouverParUtilisateur(int idUtilisateur) throws ServiceException {
        try {
            return participationDAO.findByUserId(idUtilisateur);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la recherche des participations par utilisateur", e);
        }
    }

    @Override
    public List<ParticipationWithCourse> trouverParUtilisateurAvecCourse(int idUtilisateur) throws ServiceException {
        try {
            List<Participation> participations = participationDAO.findByUserId(idUtilisateur);
            List<ParticipationWithCourse> result = new ArrayList<>();
            
            for (Participation participation : participations) {
                Optional<Course> course = courseDAO.findById(participation.getIdCourse());
                if (course.isPresent()) {
                    result.add(ParticipationWithCourse.from(participation, course.get()));
                }
            }
            
            return result;
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la recherche des participations avec courses par utilisateur", e);
        }
    }

    @Override
    public List<Participation> trouverParCourse(int idCourse) throws ServiceException {
        try {
            return participationDAO.findByCourseId(idCourse);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la recherche des participations par course", e);
        }
    }

    @Override
    public Optional<Participation> trouverParUtilisateurEtCourse(int idUtilisateur, int idCourse) throws ServiceException {
        try {
            return participationDAO.findByUserAndCourse(idUtilisateur, idCourse);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la recherche de la participation par utilisateur et course", e);
        }
    }

    @Override
    public Participation mettreAJour(Participation participation) throws ServiceException {
        try {
            // Vérifier si la participation existe
            if (!participationDAO.findById(participation.getIdParticipation()).isPresent()) {
                throw new ServiceException("Participation non trouvée avec l'ID: " + participation.getIdParticipation());
            }
            
            return participationDAO.update(participation);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la mise à jour de la participation", e);
        }
    }

    @Override
    public void annulerInscription(int idUtilisateur, int idCourse) throws ServiceException {
        try {
            // Vérifier si l'utilisateur est inscrit à cette course
            Optional<Participation> participation = participationDAO.findByUserAndCourse(idUtilisateur, idCourse);
            if (!participation.isPresent()) {
                throw new ServiceException("L'utilisateur n'est pas inscrit à cette course");
            }
            
            // Supprimer la participation
            participationDAO.delete(participation.get().getIdParticipation());
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de l'annulation de l'inscription", e);
        }
    }

    @Override
    public void marquerDossardTelecharge(int idParticipation) throws ServiceException {
        try {
            // Vérifier si la participation existe
            Optional<Participation> optParticipation = participationDAO.findById(idParticipation);
            if (!optParticipation.isPresent()) {
                throw new ServiceException("Participation non trouvée avec l'ID: " + idParticipation);
            }
            
            // Mettre à jour le statut du dossard
            Participation participation = optParticipation.get();
            participation.setDossardTelecharge(true);
            
            participationDAO.update(participation);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors du marquage du dossard comme téléchargé", e);
        }
    }

    @Override
    public int compterParticipants(int idCourse) throws ServiceException {
        try {
            return participationDAO.countParticipantsByCourse(idCourse);
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors du comptage des participants", e);
        }
    }

    @Override
    public boolean courseEstComplete(int idCourse) throws ServiceException {
        try {
            // Vérifier si la course existe
            Optional<Course> course = courseDAO.findById(idCourse);
            if (!course.isPresent()) {
                throw new ServiceException("Course non trouvée avec l'ID: " + idCourse);
            }
            
            // Comparer le nombre de participants au maximum autorisé
            int nbParticipants = participationDAO.countParticipantsByCourse(idCourse);
            return nbParticipants >= course.get().getNbMaxParticipants();
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la vérification si la course est complète", e);
        }
    }

    @Override
    public byte[] genererDossardPDF(int idParticipation) throws ServiceException {
        try {
            // Vérifier si la participation existe
            Optional<Participation> optParticipation = participationDAO.findById(idParticipation);
            if (!optParticipation.isPresent()) {
                throw new ServiceException("Participation non trouvée avec l'ID: " + idParticipation);
            }
            
            Participation participation = optParticipation.get();
            
            // Récupérer les informations de l'utilisateur et de la course
            Optional<Utilisateur> optUtilisateur = utilisateurDAO.findById(participation.getIdUtilisateur());
            if (!optUtilisateur.isPresent()) {
                throw new ServiceException("Utilisateur non trouvé avec l'ID: " + participation.getIdUtilisateur());
            }
            
            Optional<Course> optCourse = courseDAO.findById(participation.getIdCourse());
            if (!optCourse.isPresent()) {
                throw new ServiceException("Course non trouvée avec l'ID: " + participation.getIdCourse());
            }
            
            // Générer le PDF
            byte[] pdfContent = PDFGenerator.generateBibNumber(participation, optUtilisateur.get(), optCourse.get());
            
            // Marquer le dossard comme téléchargé
            marquerDossardTelecharge(idParticipation);
            
            return pdfContent;
        } catch (DAOException e) {
            throw new ServiceException("Erreur lors de la génération du dossard PDF", e);
        }
    }
}