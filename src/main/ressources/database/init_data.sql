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