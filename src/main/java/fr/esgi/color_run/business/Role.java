package fr.esgi.color_run.business;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    private int idRole;
    private String nomRole;
    private String description;

    // public Role() {
    // }

    // public Role(int idRole, String nomRole, String description) {
    // this.idRole = idRole;
    // this.nomRole = nomRole;
    // this.description = description;
    // }

    // // Getters
    // public int getIdRole() {
    // return idRole;
    // }

    // public String getNomRole() {
    // return nomRole;
    // }

    // public String getDescription() {
    // return description;
    // }

    // // Setters
    // public void setIdRole(int idRole) {
    // this.idRole = idRole;
    // }

    // public void setNomRole(String nomRole) {
    // this.nomRole = nomRole;
    // }

    // public void setDescription(String description) {
    // this.description = description;
    // }

    // @Override
    // public String toString() {
    // return "Role{" +
    // "idRole=" + idRole +
    // ", nomRole='" + nomRole + '\'' +
    // ", description='" + description + '\'' +
    // '}';
    // }
}