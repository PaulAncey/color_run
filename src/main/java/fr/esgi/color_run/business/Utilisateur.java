package fr.esgi.color_run.business;

import java.sql.Timestamp;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utilisateur {
    private int idUtilisateur;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String photoProfile;
    private boolean compteVerifie;
    private Timestamp dateCreation;
}