package com.yves.ge.gui;

import com.yves.ge.dao.EtudiantDAO;
import com.yves.ge.model.Etudiant;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.*;
import java.util.function.Function;

public class FenetreEtudiant extends BorderPane {
    private final EtudiantDAO dao=new EtudiantDAO();
    private final ObservableList<Etudiant> all=FXCollections.observableArrayList();
    private final ObservableList<Etudiant> visible=FXCollections.observableArrayList();
    private final TableView<Etudiant> table=new TableView<>(visible);

    private final TextField searchMatricule=new TextField(), searchNom=new TextField();
    private final ComboBox<String> searchParcours=new ComboBox<>(), searchStatus=new ComboBox<>();
    private final TextField matricule=new TextField(), nom=new TextField(), prenom=new TextField(),
        email=new TextField(), telephone=new TextField();
    private final DatePicker dateNaissance=new DatePicker();
    private final ComboBox<String> parcours=new ComboBox<>(), annee=new ComboBox<>(), status=new ComboBox<>();
    private final Label totalLabel=new Label("Total : 0 étudiants");
    private final Label pageLabel=new Label("1");
    private final Label message=new Label();
    private int page=1; private int pageSize=10; private Etudiant selected;

    public FenetreEtudiant(){setPadding(new Insets(16));build();load();}

    private void build(){
        setTop(buildSearch());
        setLeft(buildForm());
        setCenter(buildTable());
        setBottom(buildFooter());
    }

    private TitledPane titled(String title, javafx.scene.Node node, String css){
        TitledPane p=new TitledPane(title,node);p.setCollapsible(false);p.getStyleClass().add(css);return p;
    }

    private VBox buildSearch(){
        VBox box=new VBox(0);box.getStyleClass().add("search-zone");
        Label title=new Label("⌕  Recherche d'étudiant");title.getStyleClass().add("zone-title");
        Label zone=new Label("ZONE RECHERCHE");zone.getStyleClass().add("zone-label");
        StackPane.setAlignment(zone,Pos.TOP_CENTER);
        HBox fields=new HBox(14);fields.setAlignment(Pos.CENTER_LEFT);
        searchMatricule.setPromptText("Matricule");searchNom.setPromptText("Nom");
        searchParcours.getItems().addAll("Tous","Informatique","Réseaux","Génie Logiciel","Systèmes Embarqués");
        searchStatus.getItems().addAll("Tous","Actif","Inactif");searchParcours.setValue("Tous");searchStatus.setValue("Tous");
        fields.getChildren().addAll(labeled("Matricule :",searchMatricule),labeled("Nom :",searchNom),
            labeled("Parcours :",searchParcours),labeled("Statut :",searchStatus));
        Button find=new Button("⌕  Rechercher");find.getStyleClass().add("search-btn");find.setOnAction(e->applyFilters());
        Button reset=new Button("⟳  Réinitialiser");reset.setOnAction(e->resetSearch());
        fields.getChildren().addAll(find,reset);
        box.getChildren().addAll(zone,title,fields);return box;
    }
    private VBox labeled(String label,Control c){Label l=new Label(label);VBox v=new VBox(5,l,c);HBox.setHgrow(v,Priority.ALWAYS);return v;}

    private VBox buildForm(){
        VBox box=new VBox(0);box.getStyleClass().add("form-zone");box.setPrefWidth(350);
        Label title=new Label("♟  Formulaire Étudiant");title.getStyleClass().add("form-title");
        dateNaissance.setPromptText("jj/mm/aaaa");
        parcours.getItems().addAll("Sélectionner","Informatique","Réseaux","Génie Logiciel","Systèmes Embarqués");parcours.setValue("Sélectionner");
        annee.getItems().addAll("2024 - 2025","2025 - 2026","2026 - 2027");annee.setValue("2024 - 2025");
        status.getItems().addAll("Actif","Inactif");status.setValue("Actif");
        VBox fields=new VBox(0,
            labeled("Matricule :",matricule),labeled("Nom :",nom),labeled("Prénom :",prenom),
            labeled("Date de naissance :",dateNaissance),labeled("Email :",email),labeled("Téléphone :",telephone),
            labeled("Parcours :",parcours),labeled("Année universitaire :",annee),labeled("Statut :",status));
        Button nouveau=new Button("＋  Nouveau");nouveau.getStyleClass().add("new-btn");nouveau.setOnAction(e->clearForm());
        Button save=new Button("▣  Enregistrer");save.getStyleClass().add("save-btn");save.setOnAction(e->save());
        Button edit=new Button("✎  Modifier");edit.getStyleClass().add("edit-btn");edit.setOnAction(e->edit());
        Button del=new Button("▣  Supprimer");del.getStyleClass().add("delete-btn");del.setOnAction(e->delete());
        Button cancel=new Button("⊗  Annuler");cancel.setOnAction(e->clearForm());
        HBox r1=new HBox(10,nouveau,save),r2=new HBox(10,edit,del,cancel);
        box.getChildren().addAll(title,fields,new Separator(),r1,new Separator(),r2,message);return box;
    }

    private VBox buildTable(){
        VBox box=new VBox(0);box.getStyleClass().add("table-zone");
        Label title=new Label("▤  Liste des Étudiants");title.getStyleClass().add("table-title");
        table.getColumns().addAll(
            col("Matricule",Etudiant::getMatricule),col("Nom",Etudiant::getNom),col("Prénom",Etudiant::getPrenom),
            col("Date naissance",Etudiant::getDateNaissance),col("Email",Etudiant::getEmail),col("Parcours",Etudiant::getParcours),
            col("Année Univ.",Etudiant::getAnneeUniversitaire),statusCol());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPlaceholder(new Label("Aucun étudiant"));
        table.setPrefHeight(500);
        table.getSelectionModel().selectedItemProperty().addListener((o,a,e)->select(e));
        box.getChildren().addAll(title,table);return box;
    }
    private TableColumn<Etudiant,String> col(String n,Function<Etudiant,String> f){
        TableColumn<Etudiant,String> c=new TableColumn<>(n);c.setCellValueFactory(v->new SimpleStringProperty(nvl(f.apply(v.getValue()))));return c;
    }
    private TableColumn<Etudiant,String> statusCol(){
        TableColumn<Etudiant,String> c=col("Statut",Etudiant::getStatus);
        c.setCellFactory(x->new TableCell<>(){
            @Override protected void updateItem(String s,boolean empty){super.updateItem(s,empty);if(empty){setText(null);setGraphic(null);return;}
                Label b=new Label(s);b.getStyleClass().add("status-pill");setGraphic(b);setText(null);}
        });return c;
    }

    private HBox buildFooter(){
        Button first=new Button("⏮"),prev=new Button("‹"),next=new Button("›"),last=new Button("⏭");
        Button one=new Button("1"),two=new Button("2"),three=new Button("3"),four=new Button("4"),five=new Button("5");
        first.setOnAction(e->gotoPage(1));prev.setOnAction(e->gotoPage(page-1));next.setOnAction(e->gotoPage(page+1));
        last.setOnAction(e->gotoPage(pageCount()));one.setOnAction(e->gotoPage(1));two.setOnAction(e->gotoPage(2));three.setOnAction(e->gotoPage(3));four.setOnAction(e->gotoPage(4));five.setOnAction(e->gotoPage(5));
        ComboBox<Integer> size=new ComboBox<>(FXCollections.observableArrayList(10,20,50));size.setValue(10);size.valueProperty().addListener((o,a,b)->{pageSize=b;page=1;refresh();});
        HBox nav=new HBox(5,first,prev,one,two,three,four,five,next,last);nav.setAlignment(Pos.CENTER);
        HBox footer=new HBox(20,totalLabel,nav,new Label("Affichage :"),pageLabel,new Label("sur"),size);
        footer.setAlignment(Pos.CENTER_LEFT);footer.setPadding(new Insets(12,10,0,10));HBox.setHgrow(nav,Priority.ALWAYS);return footer;
    }

    private void load(){all.setAll(dao.search("","","",""));totalLabel.setText("Total : "+dao.count()+" étudiants");refresh();}
    private void applyFilters(){all.setAll(dao.search(searchMatricule.getText(),searchNom.getText(),val(searchParcours),val(searchStatus)));page=1;refresh();}
    private String val(ComboBox<String> c){return c.getValue()==null||"Tous".equals(c.getValue())?"":c.getValue();}
    private void resetSearch(){searchMatricule.clear();searchNom.clear();searchParcours.setValue("Tous");searchStatus.setValue("Tous");load();}
    private void refresh(){int from=(page-1)*pageSize,to=Math.min(from+pageSize,all.size());visible.setAll(from<all.size()?all.subList(from,to):List.of());pageLabel.setText("Page "+page+" / "+pageCount());totalLabel.setText("Total : "+all.size()+" étudiants");}
    private int pageCount(){return Math.max(1,(int)Math.ceil((double)all.size()/pageSize));}
    private void gotoPage(int p){page=Math.max(1,Math.min(p,pageCount()));refresh();}
    private void select(Etudiant e){if(e==null)return;selected=e;matricule.setText(e.getMatricule());nom.setText(e.getNom());prenom.setText(e.getPrenom());email.setText(e.getEmail());telephone.setText(e.getTelephone());parcours.setValue(e.getParcours()==null||e.getParcours().isBlank()?"Sélectionner":e.getParcours());annee.setValue(e.getAnneeUniversitaire());status.setValue(e.getStatus());try{if(e.getDateNaissance()!=null&&!e.getDateNaissance().isBlank())dateNaissance.setValue(java.time.LocalDate.parse(e.getDateNaissance()));}catch(Exception ignored){}}
    private boolean valid(){if(matricule.getText().isBlank()){alert("Le matricule est obligatoire.");return false;}if(nom.getText().isBlank()){alert("Le nom est obligatoire.");return false;}return true;}
    private Etudiant form(){return new Etudiant(matricule.getText().trim(),nom.getText().trim(),prenom.getText().trim(),dateNaissance.getValue()==null?"":dateNaissance.getValue().toString(),email.getText().trim(),telephone.getText().trim(),parcours.getValue()==null?"":parcours.getValue().equals("Sélectionner")?"":parcours.getValue(),annee.getValue(),status.getValue());}
    private void save(){if(!valid())return;try{dao.insert(form());message.setText("Étudiant enregistré.");clearForm();load();}catch(Exception e){alert("Matricule déjà utilisé ou données invalides.");}}
    private void edit(){if(selected==null){alert("Sélectionnez un étudiant dans la liste.");return;}if(!valid())return;Etudiant e=form();e.setId(selected.getId());try{dao.update(e);message.setText("Étudiant modifié.");clearForm();load();}catch(Exception x){alert("Modification impossible.");}}
    private void delete(){if(selected==null){alert("Sélectionnez un étudiant.");return;}Alert a=new Alert(Alert.AlertType.CONFIRMATION,"Supprimer cet étudiant ?",ButtonType.YES,ButtonType.NO);a.showAndWait().ifPresent(b->{if(b==ButtonType.YES){dao.delete(selected.getId());clearForm();load();}});}
    private void clearForm(){selected=null;matricule.clear();nom.clear();prenom.clear();dateNaissance.setValue(null);email.clear();telephone.clear();parcours.setValue("Sélectionner");annee.setValue("2024 - 2025");status.setValue("Actif");table.getSelectionModel().clearSelection();}
    private void alert(String s){new Alert(Alert.AlertType.WARNING,s,ButtonType.OK).showAndWait();}
    private String nvl(String s){return s==null?"":s;}
}