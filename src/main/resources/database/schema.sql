CREATE TABLE IF NOT EXISTS Role (
    idRole INT PRIMARY KEY AUTO_INCREMENT,
    nomRole VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Utilisateur (
    idUtilisateur INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    motDePasse VARCHAR(255) NOT NULL,
    photoProfile VARCHAR(255),
    compteVerifie BOOLEAN DEFAULT FALSE,
    dateCreation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS UtilisateurRole (
    idUtilisateurRole INT PRIMARY KEY AUTO_INCREMENT,
    idUtilisateur INT NOT NULL,
    idRole INT NOT NULL,
    dateAttribution TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (idUtilisateur) REFERENCES Utilisateur(idUtilisateur),
    FOREIGN KEY (idRole) REFERENCES Role(idRole),
    UNIQUE (idUtilisateur, idRole)
);

CREATE TABLE IF NOT EXISTS Course (
    idCourse INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    description TEXT,
    dateCourse DATE NOT NULL,
    heureDebut TIME NOT NULL,
    ville VARCHAR(100) NOT NULL,
    adresse VARCHAR(255) NOT NULL,
    latitude FLOAT,
    longitude FLOAT,
    distanceKm FLOAT NOT NULL,
    nbMaxParticipants INT NOT NULL,
    prix FLOAT NOT NULL,
    avecObstacles BOOLEAN DEFAULT FALSE,
    causeSoutenue VARCHAR(255),
    idOrganisateur INT NOT NULL,
    dateCreation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (idOrganisateur) REFERENCES Utilisateur(idUtilisateur)
);

CREATE TABLE IF NOT EXISTS Participation (
    idParticipation INT PRIMARY KEY AUTO_INCREMENT,
    idUtilisateur INT NOT NULL,
    idCourse INT NOT NULL,
    numeroDossard INT NOT NULL,
    dateInscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    dossardTelecharge BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (idUtilisateur) REFERENCES Utilisateur(idUtilisateur),
    FOREIGN KEY (idCourse) REFERENCES Course(idCourse),
    UNIQUE (idCourse, numeroDossard),
    UNIQUE (idUtilisateur, idCourse)
);

CREATE TABLE IF NOT EXISTS Message (
    idMessage INT PRIMARY KEY AUTO_INCREMENT,
    idCourse INT NOT NULL,
    idUtilisateur INT NOT NULL,
    contenu TEXT NOT NULL,
    dateEnvoi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (idCourse) REFERENCES Course(idCourse),
    FOREIGN KEY (idUtilisateur) REFERENCES Utilisateur(idUtilisateur)
);

CREATE TABLE IF NOT EXISTS DemandeOrganisateur (
    idDemande INT PRIMARY KEY AUTO_INCREMENT,
    idUtilisateur INT NOT NULL,
    motif TEXT NOT NULL,
    statut VARCHAR(20) DEFAULT 'EN_ATTENTE',
    dateDemande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    dateTraitement TIMESTAMP,
    commentaireAdmin TEXT,
    FOREIGN KEY (idUtilisateur) REFERENCES Utilisateur(idUtilisateur)
);

INSERT INTO Role (nomRole, description) VALUES 
('ADMIN', 'Administrateur de la plateforme'),
('ORGANISATEUR', 'Organisateur de courses'),
('PARTICIPANT', 'Participant aux courses');

-- Insertion des utilisateurs (avec mot de passe "password" en clair pour les tests)
INSERT INTO Utilisateur (nom, prenom, email, motDePasse, compteVerifie, dateCreation) VALUES 
('Admin', 'System', 'admin@colorrun.fr', 'password', TRUE, CURRENT_TIMESTAMP),
('Dupont', 'Jean', 'jean.dupont@example.com', 'password', TRUE, CURRENT_TIMESTAMP),
('Martin', 'Sophie', 'sophie.martin@example.com', 'password', TRUE, CURRENT_TIMESTAMP),
('Petit', 'Pierre', 'pierre.petit@example.com', 'password', TRUE, CURRENT_TIMESTAMP),
('Durand', 'Marie', 'marie.durand@example.com', 'password', TRUE, CURRENT_TIMESTAMP);

-- Attribution des rôles
INSERT INTO UtilisateurRole (idUtilisateur, idRole, dateAttribution) VALUES 
(1, 1, CURRENT_TIMESTAMP), -- Admin est ADMIN
(2, 2, CURRENT_TIMESTAMP), -- Jean est ORGANISATEUR
(2, 3, CURRENT_TIMESTAMP), -- Jean est aussi PARTICIPANT
(3, 2, CURRENT_TIMESTAMP), -- Sophie est ORGANISATEUR
(3, 3, CURRENT_TIMESTAMP), -- Sophie est aussi PARTICIPANT
(4, 3, CURRENT_TIMESTAMP), -- Pierre est PARTICIPANT
(5, 3, CURRENT_TIMESTAMP); -- Marie est PARTICIPANT

-- Insertion de courses
INSERT INTO Course (nom, description, dateCourse, heureDebut, ville, adresse, 
                   latitude, longitude, distanceKm, nbMaxParticipants, prix, 
                   avecObstacles, causeSoutenue, idOrganisateur) VALUES 
-- Course 1 - organisée par Jean (idUtilisateur = 2)
('Color Run Paris', 'La course colorée la plus fun de Paris !', 
 DATEADD('MONTH', 1, CURRENT_DATE), '10:00:00', 'Paris', '2 Avenue des Champs-Élysées, 75008 Paris', 
 48.8698, 2.3075, 5.0, 500, 25.0, FALSE, 'Lutte contre le cancer', 2),

-- Course 2 - organisée par Sophie (idUtilisateur = 3)
('Color Run Lyon', 'Venez courir dans la capitale des Gaules !', 
 DATEADD('MONTH', 2, CURRENT_DATE), '09:30:00', 'Lyon', '20 Place Bellecour, 69002 Lyon', 
 45.7578, 4.8320, 10.0, 300, 30.0, TRUE, 'Protection de l''environnement', 3),

-- Course 3 - organisée par Jean (idUtilisateur = 2)
('Neon Color Run', 'Une course nocturne avec des couleurs fluorescentes !', 
 DATEADD('MONTH', 3, CURRENT_DATE), '21:00:00', 'Paris', 'Parc de la Villette, 75019 Paris', 
 48.8938, 2.3900, 7.5, 400, 35.0, TRUE, 'Aide aux sans-abris', 2);

-- Insertion de participations
INSERT INTO Participation (idUtilisateur, idCourse, numeroDossard, dateInscription, dossardTelecharge) VALUES 
(4, 1, 1, CURRENT_TIMESTAMP, FALSE), -- Pierre participe à la Color Run Paris
(5, 1, 2, CURRENT_TIMESTAMP, FALSE), -- Marie participe à la Color Run Paris
(4, 2, 1, CURRENT_TIMESTAMP, FALSE), -- Pierre participe à la Color Run Lyon
(2, 2, 2, CURRENT_TIMESTAMP, TRUE);  -- Jean participe à la Color Run Lyon (bien qu'il soit organisateur d'autres courses)

-- Insertion de messages dans les fils de discussion
INSERT INTO Message (idCourse, idUtilisateur, contenu, dateEnvoi) VALUES 
(1, 2, 'Bonjour à tous ! Nous sommes ravis de vous accueillir pour cette Color Run Paris. N''hésitez pas à poser vos questions ici.', CURRENT_TIMESTAMP),
(1, 4, 'Bonjour, à quelle heure pourrons-nous récupérer nos dossards ?', DATEADD('MINUTE', 30, CURRENT_TIMESTAMP)),
(1, 2, 'Les dossards seront disponibles 1h avant le départ de la course, ou la veille au village partenaires.', DATEADD('MINUTE', 45, CURRENT_TIMESTAMP)),
(2, 3, 'Bienvenue sur le fil de discussion de la Color Run Lyon ! Toutes les informations importantes seront publiées ici.', CURRENT_TIMESTAMP),
(2, 4, 'Est-ce qu''il y aura des points d''eau sur le parcours ?', DATEADD('MINUTE', 20, CURRENT_TIMESTAMP)),
(2, 3, 'Oui, des points d''eau sont prévus tous les 2.5km et à l''arrivée.', DATEADD('MINUTE', 35, CURRENT_TIMESTAMP));

-- Insertion de demandes pour devenir organisateur
INSERT INTO DemandeOrganisateur (idUtilisateur, motif, statut, dateDemande) VALUES 
(5, 'Je souhaite organiser des Color Run dans ma ville de Bordeaux. J''ai déjà de l''expérience dans l''organisation d''événements sportifs et je pense pouvoir apporter une valeur ajoutée à votre plateforme.', 'EN_ATTENTE', CURRENT_TIMESTAMP);