package com.yves.ge.model;

public class Etudiant {
    private int id;
    private String matricule, nom, prenom, dateNaissance, email, telephone,
            parcours, anneeUniversitaire, status;

    public Etudiant() {}
    public Etudiant(int id,String matricule,String nom,String prenom,String dateNaissance,
                    String email,String telephone,String parcours,String anneeUniversitaire,String status){
        this.id=id;this.matricule=matricule;this.nom=nom;this.prenom=prenom;
        this.dateNaissance=dateNaissance;this.email=email;this.telephone=telephone;
        this.parcours=parcours;this.anneeUniversitaire=anneeUniversitaire;this.status=status;
    }
    public Etudiant(String matricule,String nom,String prenom,String dateNaissance,
                    String email,String telephone,String parcours,String anneeUniversitaire,String status){
        this(0,matricule,nom,prenom,dateNaissance,email,telephone,parcours,anneeUniversitaire,status);
    }
    public int getId(){return id;} public void setId(int v){id=v;}
    public String getMatricule(){return matricule;} public void setMatricule(String v){matricule=v;}
    public String getNom(){return nom;} public void setNom(String v){nom=v;}
    public String getPrenom(){return prenom;} public void setPrenom(String v){prenom=v;}
    public String getDateNaissance(){return dateNaissance;} public void setDateNaissance(String v){dateNaissance=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getTelephone(){return telephone;} public void setTelephone(String v){telephone=v;}
    public String getParcours(){return parcours;} public void setParcours(String v){parcours=v;}
    public String getAnneeUniversitaire(){return anneeUniversitaire;} public void setAnneeUniversitaire(String v){anneeUniversitaire=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
}