package Controller;

import java.io.IOException;


import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import DbConnection.DbHandler;
import application.Pieces;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class PiecesPageControler implements Initializable {
	
	@FXML
	private GridPane grid;

	@FXML
	private ScrollPane scroll;
	
	@FXML
    private JFXButton refresh;
	
	@FXML
	private JFXComboBox<String> categoryCombo;

	@FXML
	private JFXButton confirmCategory;
	
	@FXML
    private JFXButton homeButton;

    @FXML
    private JFXButton piecesButton;

    @FXML
    private JFXButton devisButton;
    
    @FXML
    private JFXButton seeButton;
	
	Connection connection;
    DbHandler handler;
    PreparedStatement pst;
    
    private static PiecesPageControler instance; 
	
    String nom, prenom, email, phone, adress, admin;
    int age,id;
    
    public PiecesPageControler() {
    	instance = this;
    }
    
    public static PiecesPageControler getInstance() {
    	return instance;
    }
    
	public void displayInfo(int id, String nom, String prenom, String email, int age, String phone, String adress, String admin) {
		this.id = id;
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
		this.age = age;
		this.phone = phone;
		this.adress = adress;
		this.admin = admin;
	}
    
	ObservableList<Pieces> piecesList = FXCollections.observableArrayList();
	
	public void refresh() {
		piecesList.clear();
		grid.getChildren().clear();
		
		if(categoryCombo.getValue() == null) {
			getData();
			piecesList.addAll(getData());
		} else {
			getData(categoryCombo.getValue());
			piecesList.addAll(getData(categoryCombo.getValue()));
		}
		
		int column = 0, row = 1;
			try {
				
				for(int i=0; i<piecesList.size(); i++) {
				
				FXMLLoader root = new FXMLLoader();
				root.setLocation(getClass().getResource("/FXML/Pieces.fxml"));
				root.load();				
				PiecesControler main = root.getController();
				main.setData(piecesList.get(i));
				Parent pane = root.getRoot();
				
				
				if (column == 2) {
					column = 0;
					row++;
				}
				
				grid.add(pane, column++, row);
				
				grid.setMinHeight(Region.USE_COMPUTED_SIZE);
				grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
				grid.setMaxHeight(Region.USE_COMPUTED_SIZE);
				grid.setMinWidth(Region.USE_COMPUTED_SIZE);
				grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
				grid.setMaxWidth(Region.USE_COMPUTED_SIZE);
				GridPane.setMargin(pane, new Insets(60,0,0,30));
				
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			

	}
    
    public ObservableList<Pieces> getData() {
    	connection = handler.getConnection();
    	ObservableList<Pieces> piecesList = FXCollections.observableArrayList();
    	String select = "SELECT * FROM pieces";
    	String select2 = "SELECT * FROM category WHERE id=?";
    	
    	try {
			pst = connection.prepareStatement(select);
			ResultSet rs = pst.executeQuery();
	    	while(rs.next()) {
	    		int catId = rs.getInt("categoryId");
	    		pst = connection.prepareStatement(select2);
	    		pst.setString(1, String.valueOf(catId));
	    		ResultSet s = pst.executeQuery();
	    		s.next();
	    		String catName = s.getString("name");
	    		String catImg = s.getString("image");
	    		
	    		piecesList.add(new Pieces(
	    				rs.getString("name"),
	    				rs.getString("description"),
	    				rs.getInt("id"),
	    				catName,
	    				rs.getInt("stock"),
	    				rs.getDouble("price"),
	    				catImg
	    				));
	    	}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    		
    	return piecesList;
    }
    
    public ObservableList<Pieces> getData(String categoryName) {
    	connection = handler.getConnection();
    	ObservableList<Pieces> piecesList = FXCollections.observableArrayList();
    	String selectCatId = "SELECT id FROM category WHERE name=?"; 
    	String select = "SELECT * FROM pieces WHERE categoryId=?";
    	String select2 = "SELECT * FROM category WHERE id=?";
    	
    	try {
    		pst = connection.prepareStatement(selectCatId);
    		pst.setString(1, categoryName);
    		ResultSet st = pst.executeQuery();
    		st.next();
    		int category_id = st.getInt("id");
    		
			pst = connection.prepareStatement(select);
			pst.setString(1, String.valueOf(category_id));
			ResultSet rs = pst.executeQuery();
	    	while(rs.next()) {
	    		int catId = rs.getInt("categoryId");
	    		pst = connection.prepareStatement(select2);
	    		pst.setString(1, String.valueOf(catId));
	    		ResultSet s = pst.executeQuery();
	    		s.next();
	    		String catName = s.getString("name");
	    		String catImg = s.getString("image");
	    		
	    		piecesList.add(new Pieces(
	    				rs.getString("name"),
	    				rs.getString("description"),
	    				rs.getInt("id"),
	    				catName,
	    				rs.getInt("stock"),
	    				rs.getDouble("price"),
	    				catImg
	    				));
	    	}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    		
    	return piecesList;
    }
    

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		handler = new DbHandler();
		refresh();
		refreshComboCategory();
	}
	
	public void refreshComboCategory() {
		connection = handler.getConnection();
		String select = "SELECT name FROM category";
		
		try {
			pst = connection.prepareStatement(select);
			ResultSet rs =pst.executeQuery();
			while(rs.next()) {
				categoryCombo.getItems().add(rs.getString("name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	public void see(ActionEvent e) {
		categoryCombo.setValue(null);
		refresh();
	}
	
	public void changeToHome(ActionEvent e) throws IOException {
		homeButton.getScene().getWindow().hide();

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/FXML/MainPage.fxml"));
		loader.load();
		MainPageControler controler = loader.getController();
		controler.displayInfo(id,nom, prenom, email, age, phone, adress, admin);
		Parent root = loader.getRoot();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
		Stage primaryStage = new Stage();
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setTitle("Home");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Img/iconeAplli (1).png")));
		primaryStage.setResizable(false);
	}
	
	public void changeToDevis(ActionEvent e) throws IOException {
		devisButton.getScene().getWindow().hide();

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/FXML/DevisPage.fxml"));
		loader.load();
		DevisControler controler = loader.getController();
		controler.displayInfo(id,nom, prenom, email, age, phone, adress, admin);
		Parent root = loader.getRoot();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
		Stage primaryStage = new Stage();
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setTitle("Devis");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Img/iconeAplli (1).png")));
		primaryStage.setResizable(false);
	}
	
}
