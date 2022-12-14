package fr.insee.springIntegration.experimental.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Unit implements Serializable{

    private static final long serialVersionUID = -7408851479146003262L;
    private String id;
    private String nom;
    private String prenom;
    private String email;

    public Unit() {
    }

    public Unit(Unit u) {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Unit unit = (Unit) o;
        return Objects.equals(id, unit.id) && Objects.equals(nom, unit.nom) && Objects.equals(prenom, unit.prenom) && Objects.equals(email, unit.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, prenom, email);
    }

    @Override
    public String toString() {
        return "Unit{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
