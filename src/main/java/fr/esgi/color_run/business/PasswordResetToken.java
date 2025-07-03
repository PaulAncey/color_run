package fr.esgi.color_run.business;

import lombok.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {
    private int idToken;
    private int idUtilisateur;
    private String token;
    private Timestamp dateExpiration;
    private boolean utilise;
    private Timestamp dateCreation;
}
