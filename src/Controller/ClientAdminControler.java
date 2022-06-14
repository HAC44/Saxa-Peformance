package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;

import com.jfoenix.controls.*;

import DbConnection.DbHandler;
import application.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ClientAdminControler implements Initializable {
	
	@FXML
    private JFXButton homeButton;

    @FXML
    private JFXButton piecesButton;

    @FXML
    private JFXButton clientsButton;

    @FXML
    private JFXButton history;

    @FXML
    private JFXButton refresh;

    @FXML
    private JFXButton seeButton;

    @FXML
    private TableView<Client> clientsTable;

    @FXML
    private TableColumn<Client, Integer> idColumn;

    @FXML
    private TableColumn<Client, String> nameColumn;

    @FXML
    private TableColumn<Client, String> prenomColumn;

    @FXML
    private TableColumn<Client, Integer> ageColumn;

    @FXML
    private TableColumn<Client, String> usernameColumn;

    @FXML
    private TableColumn<Client, String> emailColumn;

    @FXML
    private TableColumn<Client, String> phoneColumn;

    @FXML
    private TableColumn<Client, String> adresseColumn;

    @FXML
    private JFXButton modifyButton;

    @FXML
    private JFXButton deleteButton;
    
    Connection connection;
    DbHandler handler;
    PreparedStatement pst;
    Client client;
    
    String nom="", prenom="", email="", phone="", adress="", admin="";
    int age,idClient;
    
    ObservableList<Client> clientsList = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		handler = new DbHandler();
		loadData();
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
		controler.displayInfo(idClient,nom, prenom, email, age, phone, adress, admin);
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
	
	public void goToPieces(ActionEvent e) throws IOException {
		homeButton.getScene().getWindow().hide();

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/FXML/PiecesPageAdmin.fxml"));
		loader.load();
		PiecesPageAdminControler controler = loader.getController();
		controler.displayInfo(idClient, nom, prenom, email, age, phone, adress, admin);
		Parent root = loader.getRoot();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
		Stage primaryStage = new Stage();
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setTitle("Admin : Pieces");
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
	
	public void refresh() {
		clientsList.clear();
		connection = handler.getConnection();
		
		String select = "SELECT * FROM accounts WHERE admin='NO'";
		
		try {
			pst = connection.prepareStatement(select);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				Date date = rs.getDate("date");
				Period period = Period.between(date.toLocalDate(), LocalDate.now());
				clientsList.add(new Client(
						rs.getInt("id"),
						rs.getString("nom"),
						rs.getString("prenom"),
						rs.getString("username"),
						rs.getString("email"),
						rs.getString("phone"),
						rs.getString("adress"),
						period.getYears()
						));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		clientsTable.setItems(clientsList);
	}
	
	public void loadData() {
		connection = handler.getConnection();
		refresh();
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
		prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
		ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
		usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
		emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
		phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
		adresseColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
	}
	
	public void delete() {
		client = clientsTable.getSelectionModel().getSelectedItem();
		connection = handler.getConnection();
		
		String delete = "DELETE FROM accounts WHERE id=" + client.getId();
		
		try {
			pst = connection.prepareStatement(delete);
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		refresh();
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
