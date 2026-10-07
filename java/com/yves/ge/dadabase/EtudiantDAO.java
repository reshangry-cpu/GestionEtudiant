package com.yves.ge.dao;
import com.yves.ge.model.Etudiant;
import java.sql.*;
import java.util.*;

public class EtudiantDAO {
    private Etudiant from(ResultSet r)throws SQLException{
        return new Etudiant(r.getInt("id"),r.getString("matricule"),r.getString("nom"),
        r.getString("prenom"),r.getString("date_naissance"),r.getString("email"),
        r.getString("telephone"),r.getString("parcours"),r.getString("annee_universitaire"),
        r.getString("status"));
    }
    public List<Etudiant> search(String matricule,String nom,String parcours,String status){
        List<Etudiant> out=new ArrayList<>();
        String sql = """
        SELECT * FROM etudiants
        WHERE matricule LIKE ? AND nom LIKE ?
        AND (?='' OR parcours=?) AND (?='' OR status=?)
        ORDER BY id DESC
        """;
        try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,"%"+safe(matricule)+"%");
            p.setString(2,"%"+safe(nom)+"%");
            p.setString(3,parcours);p.setString(4,parcours);
            p.setString(5,status);p.setString(6,status);
            ResultSet r=p.executeQuery(); while(r.next())out.add(from(r));
        }catch(SQLException e){throw new RuntimeException(e);}
        return out;
    }
    private String safe(String s){return s==null?"":s.trim();}
    public void insert(Etudiant e){
        String sql="INSERT INTO etudiants(matricule,nom,prenom,date_naissance,email,telephone,parcours,annee_universitaire,status) VALUES(?,?,?,?,?,?,?,?,?)";
        try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement(sql)){fill(p,e);p.executeUpdate();}
        catch(SQLException x){throw new RuntimeException(x);}
    }
    public void update(Etudiant e){
        String sql="UPDATE etudiants SET matricule=?,nom=?,prenom=?,date_naissance=?,email=?,telephone=?,parcours=?,annee_universitaire=?,status=? WHERE id=?";
        try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement(sql)){fill(p,e);p.setInt(10,e.getId());p.executeUpdate();}
        catch(SQLException x){throw new RuntimeException(x);}
    }
    private void fill(PreparedStatement p,Etudiant e)throws SQLException{
        p.setString(1,e.getMatricule());p.setString(2,e.getNom());p.setString(3,e.getPrenom());
        p.setString(4,e.getDateNaissance());p.setString(5,e.getEmail());p.setString(6,e.getTelephone());
        p.setString(7,e.getParcours());p.setString(8,e.getAnneeUniversitaire());p.setString(9,e.getStatus());
    }
    public void delete(int id){
        try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM etudiants WHERE id=?")){
            p.setInt(1,id);p.executeUpdate();
        }catch(SQLException x){throw new RuntimeException(x);}
    }
    public int count(){try(Connection c=Database.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT COUNT(*) FROM etudiants")){return r.next()?r.getInt(1):0;}catch(SQLException e){throw new RuntimeException(e);}}
}