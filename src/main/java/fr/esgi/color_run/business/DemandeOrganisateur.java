package fr.esgi.color_run.business;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandeOrganisateur {
    private int idDemande;
    private int idUtilisateur;
    private String motif;
    private String statut; // "EN_ATTENTE", "ACCEPTEE", "REFUSEE"
    private Timestamp dateDemande;
    private Timestamp dateTraitement;
    private String commentaireAdmin;
}