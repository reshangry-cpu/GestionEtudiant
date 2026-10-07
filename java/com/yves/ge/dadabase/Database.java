package com.yves.ge.dao;
import java.sql.*;

public final class Database {
    private static final String URL="jdbc:sqlite:etudiants.db";
    private Database(){}
    public static Connection getConnection() throws SQLException {return DriverManager.getConnection(URL);}
    public static void initialiser(){
        String sql=""" 
            CREATE TABLE IF NOT EXISTS etudiants (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                matricule TEXT NOT NULL UNIQUE,
                nom TEXT NOT NULL,
                prenom TEXT,
                date_naissance TEXT,
                email TEXT,
                telephone TEXT,
                parcours TEXT,
                annee_universitaire TEXT,
                status TEXT
            )
            """;
        try(Connection c=getConnection();Statement s=c.createStatement()){s.execute(sql);}
        catch(SQLException e){throw new RuntimeException("Erreur SQLite",e);}
    }
}