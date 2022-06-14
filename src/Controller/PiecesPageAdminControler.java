package Controller;

import java.io.IOException;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.jfoenix.controls.*;

import DbConnection.DbHandler;
import application.Pieces;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PiecesPageAdminControler implements Initializable {
	
	 	@FXML
	    private JFXComboBox<String> categoryCombo;

	    @FXML
	    private JFXButton confirmCategory;

	    @FXML
	    private JFXButton refresh;

	    @FXML
	    private JFXButton seeButton;
	    
	    @FXML
	    private TableView<Pieces> piecesTable;

	    @FXML
	    private TableColumn<Pieces, Integer> idColumn;

	    @FXML
	    private TableColumn<Pieces, String> nameColumn;

	    @FXML
	    private TableColumn<Pieces, Double> priceColumn;

	    @FXML
	    private TableColumn<Pieces, Integer> stockColumn;

	    @FXML
	    private TableColumn<Pieces, String> categoryColumn;

	    @FXML
	    private TableColumn<Pieces, String> descriptionColumn;
	    
	    @FXML
	    private JFXButton modifyButton;

	    @FXML
	    private JFXButton deleteButton;

	    @FXML
	    private JFXButton addButton;

	    @FXML
	    private JFXButton homeButton;

	    @FXML
	    private JFXButton piecesButton;

	    @FXML
	    private JFXButton clientsButton;

	    @FXML
	    private JFXButton history;
	    
	    @FXML
	    private ImageView catImg;
	    
	    Connection connection;
	    DbHandler handler;
	    PreparedStatement pst;
	    Pieces piece;
	    
	    String nom="", prenom="", email="", phone="", adress="", admin="";
	    int age, idClient;
	    
	    ObservableList<Pieces> piecesList = FXCollections.observableArrayList(); 

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		handler = new DbHandler();
		loadData();
		refreshComboCategory();
		
	}
	
	public ObservableList<Pieces> getData() {
		piecesList.clear();
		connection = handler.getConnection();
		String select = "SELECT * FROM pieces";
		String select2 = "SELECT name FROM category WHERE id=?";
		int id;
		
		try {
			pst = connection.prepareStatement(select);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				id = rs.getInt("categoryId");
				pst = connection.prepareStatement(select2);
				pst.setString(1, String.valueOf(id));
				ResultSet st = pst.executeQuery();
				st.next();
				piecesList.add(new Pieces(
						rs.getString("name"),
						rs.getString("description"),
						rs.getInt("id"),
						st.getString("name"),
						rs.getInt("stock"),
						rs.getDouble("price")
						));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return piecesList;
	}
	
	public ObservableList<Pieces> getData(String catId) {
		piecesList.clear();
		connection = handler.getConnection();
		String select = "SELECT * FROM pieces WHERE categoryId=(SELECT id FROM category WHERE name=?)";
		
		try {
			pst = connection.prepareStatement(select);
			pst.setString(1, catId);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
			
				piecesList.add(new Pieces(
						rs.getString("name"),
						rs.getString("description"),
						rs.getInt("id"),
						String.valueOf(categoryCombo.getValue()),
						rs.getInt("stock"),
						rs.getDouble("price")
						));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return piecesList;
	}
	
	public void refresh() {
		if(categoryCombo.getValue() == null) {
			piecesTable.setItems(getData());
		} else {
			piecesTable.setItems(getData(String.valueOf(categoryCombo.getValue())));
			loadImage();
		}
	}
	
	public void loadImage() {
			connection = handler.getConnection();
			String select = "SELECT image FROM category WHERE name=?";
			String i = null;
			
			try {
				pst = connection.prepareStatement(select);
				pst.setString(1, categoryCombo.getValue());
				ResultSet rs = pst.executeQuery();
				while(rs.next()) {
					i = rs.getString("Image");
				}
				
				catImg.setImage(new Image(getClass().getResourceAsStream(i)));			

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
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
		catImg.setImage(null);
		refresh();
	}
	
	public void addButton(ActionEvent e) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("/FXML/AddPieces.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
		Stage primaryStage = new Stage();
		primaryStage.setScene(scene);
		primaryStage.setTitle("Ajouter une pieces");
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Img/iconeAplli (1).png")));
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	public void loadData() {
		connection = handler.getConnection();
		refresh();
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
		categoryColumn.setCellValueFactory(new PropertyValueFactory<>("catId"));
		descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
	}
	
	public void delete(ActionEvent e) {
		piece = piecesTable.getSelectionModel().getSelectedItem();
		connection = handler.getConnection();
		
		String delete = "DELETE FROM pieces WHERE id=" + piece.getId();
		
		try {
			pst = connection.prepareStatement(delete);
			pst.executeUpdate();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		refresh();	
	}
	
	public void edit(ActionEvent e) {
		piece = piecesTable.getSelectionModel().getSelectedItem();
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/FXML/AddPieces.fxml"));
		try {
			loader.load();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		AddPiecesControler controler = loader.getController();
		controler.setUpdate(true);
		controler.setText(piece.getId(), piece.getName(), piece.getDescription(), piece.getPrice(), piece.getStock(), piece.getCatId());
		Parent root = loader.getRoot();
		Scene scene = new Scene(root);
		Stage primaryStage = new Stage();
		scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		primaryStage.show();
		primaryStage.setResizable(false);
		primaryStage.setTitle("Ajouter une pieces");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Img/iconeAplli (1).png")));
	}
	
	public void loadPage(String url, String title) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(url));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
		Stage primaryStage = new Stage();
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setTitle(title);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Img/iconeAplli (1).png")));
		primaryStage.setResizable(false);
	}
	
	public void goToMainPage(ActionEvent e) throws IOException {
		homeButton.getScene().getWindow().hide();
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/FXML/MainPageAdmin.fxml"));
		loader.load();
		MainPageAdminControler controler = loader.getController();
		controler.displayInfo(idClient, nom, prenom, email, age, phone, adress, admin);
		Parent root = loader.getRoot();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
		Stage primaryStage = new Stage();
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setTitle("Admin : Home");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Img/iconeAplli (1).png")));
		primaryStage.setResizable(false);
	}
	
	public void goToClient(ActionEvent e) throws IOException {
		homeButton.getScene().getWindow().hide();
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/FXML/ClientAdminPage.fxml"));
		loader.load();
		ClientAdminControler controler = loader.getController();
		controler.displayInfo(idClient, nom, prenom, email, age, phone, adress, admin);
		Parent root = loader.getRoot();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
		Stage primaryStage = new Stage();
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setTitle("Admin : Client");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Img/iconeAplli (1).png")));
		primaryStage.setResizable(false);
	}
	
	public void goToHistory(ActionEvent e) throws IOException {
		homeButton.getScene().getWindow().hide();
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/FXML/HistoryAdminPage.fxml"));
		loader.load();
		HistoryAdminControler controler = loader.getController();
		controler.displayInfo(idClient, nom, prenom, email, age, phone, adress, admin);
		Parent root = loader.getRoot();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
		Stage primaryStage = new Stage();
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setTitle("Admin : Historique de commande");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Img/iconeAplli (1).png")));
		primaryStage.setResizable(false);
	}
	
	public void displayInfo(int idClient, String nom, String prenom, String email, int age, String phone, String adress, String admin) {
		this.idClient = idClient;
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
		this.age = age;
		this.phone = phone;
		this.adress = adress;
		this.admin = admin;
	}
}
