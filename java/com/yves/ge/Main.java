package com.yves.ge;
import com.yves.ge.dao.Database;
import com.yves.ge.gui.FenetreEtudiant;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override public void start(Stage stage) {
        Database.initialiser();
        FenetreEtudiant root = new FenetreEtudiant();
        Scene scene = new Scene(root, 1200, 720);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setTitle("Gestion des Étudiants");
        stage.setMinWidth(1100); stage.setMinHeight(700);
        stage.setScene(scene); stage.show();
    }
    public static void main(String[] args){launch(args);}
}