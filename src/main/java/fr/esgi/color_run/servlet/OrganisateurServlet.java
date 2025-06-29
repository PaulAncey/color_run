package fr.esgi.color_run.servlet;

import fr.esgi.color_run.business.Course;
import fr.esgi.color_run.business.Participation;
import fr.esgi.color_run.business.ParticipationWithUser;
import fr.esgi.color_run.config.ServiceFactory;
import fr.esgi.color_run.service.CourseService;
import fr.esgi.color_run.service.ParticipationService;
import fr.esgi.color_run.service.ServiceException;
import fr.esgi.color_run.service.UtilisateurService;
import fr.esgi.color_run.util.SessionUtil;
import fr.esgi.color_run.util.ThymeleafUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servlet pour gérer les fonctionnalités organisateur
 */
@WebServlet(name = "organisateurServlet", urlPatterns = {
		"/organisateur/courses/create",
		"/organisateur/courses/edit/*",
		"/organisateur/courses/*/participants"
})
public class OrganisateurServlet extends HttpServlet {

	private CourseService courseService;
	private ParticipationService participationService;
	private UtilisateurService utilisateurService;

	@Override
	public void init() throws ServletException {
		// Initialiser les services
		ServiceFactory serviceFactory = ServiceFactory.getInstance();
		courseService = serviceFactory.getCourseService();
		participationService = serviceFactory.getParticipationService();
		utilisateurService = serviceFactory.getUtilisateurService();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Vérifier si l'utilisateur est connecté
		if (!SessionUtil.isUserLoggedIn(request)) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		// Vérifier si l'utilisateur est organisateur
		try {
			Integer userId = SessionUtil.getUserIdFromSession(request);
			List<String> userRoles = utilisateurService.obtenirRolesUtilisateur(userId)
					.stream()
					.map(role -> role.getNomRole())
					.toList();

			if (!userRoles.contains("ORGANISATEUR")) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès réservé aux organisateurs");
				return;
			}

			String pathInfo = request.getRequestURI();

			if (pathInfo.endsWith("/organisateur/courses/create")) {
				// Afficher le formulaire de création
				showCreateForm(request, response);
			} else if (pathInfo.matches(".*/organisateur/courses/edit/\\d+")) {
				// Afficher le formulaire d'édition
				showEditForm(request, response);
			} else if (pathInfo.matches(".*/organisateur/courses/\\d+/participants")) {
				// Afficher la liste des participants
				showParticipants(request, response);
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}

		} catch (ServiceException e) {
			Map<String, Object> variables = new HashMap<>();
			variables.put("error", "Une erreur est survenue: " + e.getMessage());
			ThymeleafUtil.processTemplate("error", variables, request, response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Vérifier si l'utilisateur est connecté
		if (!SessionUtil.isUserLoggedIn(request)) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		// Vérifier si l'utilisateur est organisateur
		try {
			Integer userId = SessionUtil.getUserIdFromSession(request);
			List<String> userRoles = utilisateurService.obtenirRolesUtilisateur(userId)
					.stream()
					.map(role -> role.getNomRole())
					.toList();

			if (!userRoles.contains("ORGANISATEUR")) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès réservé aux organisateurs");
				return;
			}

			String pathInfo = request.getRequestURI();

			if (pathInfo.endsWith("/organisateur/courses/create")) {
				// Traiter la création de course
				createCourse(request, response);
			} else if (pathInfo.matches(".*/organisateur/courses/edit/\\d+")) {
				// Traiter la modification de course
				updateCourse(request, response);
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}

		} catch (ServiceException e) {
			Map<String, Object> variables = new HashMap<>();
			variables.put("error", "Une erreur est survenue: " + e.getMessage());
			ThymeleafUtil.processTemplate("error", variables, request, response);
		}
	}

	/**
	 * Affiche le formulaire de création de course
	 */
	private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Map<String, Object> variables = new HashMap<>();
		ThymeleafUtil.processTemplate("organisateur/create-course", variables, request, response);
	}

	/**
	 * Affiche le formulaire d'édition de course
	 */
	private void showEditForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, ServiceException {

		// Extraire l'ID de la course
		String pathInfo = request.getRequestURI();
		int courseId = Integer.parseInt(pathInfo.substring(pathInfo.lastIndexOf('/') + 1));

		// Récupérer la course
		Optional<Course> optCourse = courseService.trouverParId(courseId);
		if (!optCourse.isPresent()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Course non trouvée");
			return;
		}

		Course course = optCourse.get();
		Integer userId = SessionUtil.getUserIdFromSession(request);

		// Vérifier que l'utilisateur est bien l'organisateur de cette course
		if (!courseService.estOrganisateur(userId, courseId)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous n'êtes pas l'organisateur de cette course");
			return;
		}

		// Récupérer les statistiques
		int nbInscrits = participationService.compterParticipants(courseId);
		boolean estComplete = participationService.courseEstComplete(courseId);

		Map<String, Object> variables = new HashMap<>();
		variables.put("course", course);
		variables.put("nbInscrits", nbInscrits);
		variables.put("estComplete", estComplete);
		ThymeleafUtil.processTemplate("organisateur/edit-course", variables, request, response);
	}

	/**
	 * Affiche la liste des participants d'une course
	 */
	private void showParticipants(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, ServiceException {

		// Extraire l'ID de la course
		String pathInfo = request.getRequestURI();
		String[] pathParts = pathInfo.split("/");
		int courseId = Integer.parseInt(pathParts[pathParts.length - 2]); // L'avant-dernier élément

		// Récupérer la course
		Optional<Course> optCourse = courseService.trouverParId(courseId);
		if (!optCourse.isPresent()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Course non trouvée");
			return;
		}

		Course course = optCourse.get();
		Integer userId = SessionUtil.getUserIdFromSession(request);

		// Vérifier que l'utilisateur est bien l'organisateur de cette course
		if (!courseService.estOrganisateur(userId, courseId)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous n'êtes pas l'organisateur de cette course");
			return;
		}

		// Récupérer les participations avec les informations utilisateur
		List<ParticipationWithUser> participations = participationService.trouverParCourseAvecUtilisateur(courseId);
		int nbParticipants = participations.size();

		Map<String, Object> variables = new HashMap<>();
		variables.put("course", course);
		variables.put("participations", participations);
		variables.put("nbParticipants", nbParticipants);
		ThymeleafUtil.processTemplate("organisateur/course-participants", variables, request, response);
	}

	/**
	 * Traite la création d'une nouvelle course
	 */
	private void createCourse(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, ServiceException {

		Integer userId = SessionUtil.getUserIdFromSession(request);

		// Récupérer les données du formulaire
		String nom = request.getParameter("nom");
		String description = request.getParameter("description");
		String ville = request.getParameter("ville");
		String adresse = request.getParameter("adresse");
		String causeSoutenue = request.getParameter("causeSoutenue");
		String dateCourseStr = request.getParameter("dateCourse");
		String heureDebutStr = request.getParameter("heureDebut");
		String distanceKmStr = request.getParameter("distanceKm");
		String prixStr = request.getParameter("prix");
		String nbMaxParticipantsStr = request.getParameter("nbMaxParticipants");
		String avecObstaclesStr = request.getParameter("avecObstacles");
		String latitudeStr = request.getParameter("latitude");
		String longitudeStr = request.getParameter("longitude");

		try {
			// Validation des champs obligatoires
			if (nom == null || nom.trim().isEmpty() ||
					description == null || description.trim().isEmpty() ||
					ville == null || ville.trim().isEmpty() ||
					adresse == null || adresse.trim().isEmpty() ||
					dateCourseStr == null || dateCourseStr.trim().isEmpty() ||
					heureDebutStr == null || heureDebutStr.trim().isEmpty() ||
					distanceKmStr == null || distanceKmStr.trim().isEmpty() ||
					prixStr == null || prixStr.trim().isEmpty() ||
					nbMaxParticipantsStr == null || nbMaxParticipantsStr.trim().isEmpty()) {

				Map<String, Object> variables = new HashMap<>();
				variables.put("error", "Tous les champs obligatoires doivent être renseignés");
				ThymeleafUtil.processTemplate("organisateur/create-course", variables, request, response);
				return;
			}

			// Conversion des types
			Date dateCourse = Date.valueOf(dateCourseStr);
			Time heureDebut = Time.valueOf(heureDebutStr + ":00");
			float distanceKm = Float.parseFloat(distanceKmStr);
			float prix = Float.parseFloat(prixStr);
			int nbMaxParticipants = Integer.parseInt(nbMaxParticipantsStr);
			boolean avecObstacles = Boolean.parseBoolean(avecObstaclesStr);

			Float latitude = null;
			Float longitude = null;
			if (latitudeStr != null && !latitudeStr.trim().isEmpty()) {
				latitude = Float.parseFloat(latitudeStr);
			}
			if (longitudeStr != null && !longitudeStr.trim().isEmpty()) {
				longitude = Float.parseFloat(longitudeStr);
			}

			// Créer l'objet Course
			Course course = Course.builder()
					.nom(nom.trim())
					.description(description.trim())
					.ville(ville.trim())
					.adresse(adresse.trim())
					.causeSoutenue(causeSoutenue != null ? causeSoutenue.trim() : null)
					.dateCourse(dateCourse)
					.heureDebut(heureDebut)
					.distanceKm(distanceKm)
					.prix(prix)
					.nbMaxParticipants(nbMaxParticipants)
					.avecObstacles(avecObstacles)
					.latitude(latitude)
					.longitude(longitude)
					.idOrganisateur(userId)
					.build();

			// Créer la course
			Course createdCourse = courseService.creerCourse(course);

			// Rediriger vers le dashboard avec succès
			response.sendRedirect(request.getContextPath() + "/dashboard?success=Course créée avec succès !");

		} catch (NumberFormatException e) {
			Map<String, Object> variables = new HashMap<>();
			variables.put("error", "Erreur dans le format des données numériques");
			ThymeleafUtil.processTemplate("organisateur/create-course", variables, request, response);
		} catch (IllegalArgumentException e) {
			Map<String, Object> variables = new HashMap<>();
			variables.put("error", "Erreur dans le format de la date ou de l'heure");
			ThymeleafUtil.processTemplate("organisateur/create-course", variables, request, response);
		}
	}

	/**
	 * Traite la modification d'une course
	 */
	private void updateCourse(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, ServiceException {

		// Extraire l'ID de la course
		String pathInfo = request.getRequestURI();
		int courseId = Integer.parseInt(pathInfo.substring(pathInfo.lastIndexOf('/') + 1));

		// Récupérer la course existante
		Optional<Course> optCourse = courseService.trouverParId(courseId);
		if (!optCourse.isPresent()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Course non trouvée");
			return;
		}

		Course existingCourse = optCourse.get();
		Integer userId = SessionUtil.getUserIdFromSession(request);

		// Vérifier que l'utilisateur est bien l'organisateur de cette course
		if (!courseService.estOrganisateur(userId, courseId)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous n'êtes pas l'organisateur de cette course");
			return;
		}

		// Récupérer les données du formulaire
		String nom = request.getParameter("nom");
		String description = request.getParameter("description");
		String ville = request.getParameter("ville");
		String adresse = request.getParameter("adresse");
		String causeSoutenue = request.getParameter("causeSoutenue");
		String dateCourseStr = request.getParameter("dateCourse");
		String heureDebutStr = request.getParameter("heureDebut");
		String distanceKmStr = request.getParameter("distanceKm");
		String prixStr = request.getParameter("prix");
		String nbMaxParticipantsStr = request.getParameter("nbMaxParticipants");
		String avecObstaclesStr = request.getParameter("avecObstacles");
		String latitudeStr = request.getParameter("latitude");
		String longitudeStr = request.getParameter("longitude");

		try {
			// Validation des champs obligatoires
			if (nom == null || nom.trim().isEmpty() ||
					description == null || description.trim().isEmpty() ||
					ville == null || ville.trim().isEmpty() ||
					adresse == null || adresse.trim().isEmpty() ||
					dateCourseStr == null || dateCourseStr.trim().isEmpty() ||
					heureDebutStr == null || heureDebutStr.trim().isEmpty() ||
					distanceKmStr == null || distanceKmStr.trim().isEmpty() ||
					prixStr == null || prixStr.trim().isEmpty() ||
					nbMaxParticipantsStr == null || nbMaxParticipantsStr.trim().isEmpty()) {

				Map<String, Object> variables = new HashMap<>();
				variables.put("error", "Tous les champs obligatoires doivent être renseignés");
				variables.put("course", existingCourse);
				ThymeleafUtil.processTemplate("organisateur/edit-course", variables, request, response);
				return;
			}

			// Conversion des types
			Date dateCourse = Date.valueOf(dateCourseStr);
			Time heureDebut = Time.valueOf(heureDebutStr + ":00");
			float distanceKm = Float.parseFloat(distanceKmStr);
			float prix = Float.parseFloat(prixStr);
			int nbMaxParticipants = Integer.parseInt(nbMaxParticipantsStr);
			boolean avecObstacles = Boolean.parseBoolean(avecObstaclesStr);

			Float latitude = null;
			Float longitude = null;
			if (latitudeStr != null && !latitudeStr.trim().isEmpty()) {
				latitude = Float.parseFloat(latitudeStr);
			}
			if (longitudeStr != null && !longitudeStr.trim().isEmpty()) {
				longitude = Float.parseFloat(longitudeStr);
			}

			// Mettre à jour l'objet Course
			Course updatedCourse = Course.builder()
					.idCourse(courseId)
					.nom(nom.trim())
					.description(description.trim())
					.ville(ville.trim())
					.adresse(adresse.trim())
					.causeSoutenue(causeSoutenue != null ? causeSoutenue.trim() : null)
					.dateCourse(dateCourse)
					.heureDebut(heureDebut)
					.distanceKm(distanceKm)
					.prix(prix)
					.nbMaxParticipants(nbMaxParticipants)
					.avecObstacles(avecObstacles)
					.latitude(latitude)
					.longitude(longitude)
					.idOrganisateur(userId)
					.dateCreation(existingCourse.getDateCreation()) // Conserver la date de création
					.build();

			// Mettre à jour la course
			courseService.mettreAJour(updatedCourse);

			// Rediriger vers la page d'édition avec succès
			response.sendRedirect(request.getContextPath() + "/organisateur/courses/edit/" + courseId
					+ "?success=Course modifiée avec succès !");

		} catch (NumberFormatException e) {
			Map<String, Object> variables = new HashMap<>();
			variables.put("error", "Erreur dans le format des données numériques");
			variables.put("course", existingCourse);
			ThymeleafUtil.processTemplate("organisateur/edit-course", variables, request, response);
		} catch (IllegalArgumentException e) {
			Map<String, Object> variables = new HashMap<>();
			variables.put("error", "Erreur dans le format de la date ou de l'heure");
			variables.put("course", existingCourse);
			ThymeleafUtil.processTemplate("organisateur/edit-course", variables, request, response);
		}
	}
}