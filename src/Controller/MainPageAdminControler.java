package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

import com.jfoenix.controls.*;
import com.sun.xml.internal.ws.util.StringUtils;

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
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainPageAdminControler implements Initializable {

	 	@FXML
	 	private JFXButton homeButton;

	    @FXML
	    private JFXButton piecesButton;

	    @FXML
	    private JFXButton clientsButton;

	    @FXML
	    private JFXButton history;

	    @FXML
	    private VBox toolBar;

	    @FXML
	    private VBox overflow;

	    @FXML
	    private JFXButton logout;

	    @FXML
	    private JFXButton modify;

	    @FXML
	    private JFXButton userAccount;

	    @FXML
	    private Button menuShower;

	    @FXML
	    private AnchorPane homePane;

	    @FXML
	    private PieChart pieChart;

	    @FXML
	    private Label nbPieces;
	    
	    @FXML
	    private Label valuePieces;

	    @FXML
	    private Label nbCommade;

	    @FXML
	    private Label nameInfo;

	    @FXML
	    private Label emailInfo;

	    @FXML
	    private Label ageInfo;

	    @FXML
	    private Label phoneInfo;

	    @FXML
	    private Label adressInfo;
	    
	    @FXML
	    private Pagination pagination;
	    
	    Connection connection;
	    DbHandler handler;
	    PreparedStatement pst;
	    
	    String nom="", prenom="", email="", phone="", adress="", admin="";
	    int age,idClient;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		handler = new DbHandler();
		nbPieces();
		valuePieces();
		nbCommandes();
		pieChart();
		displayPagination();
		
		overflow.setVisible(false);
		openMenu();
	}
	
	private void openMenu() {
		JFXPopup popup  = new JFXPopup(overflow); 
		popup.setWidth(175);
		popup.setHeight(200);
		
		menuShower.setOnMouseClicked(e -> {
			overflow.setVisible(true);
			popup.show(toolBar,JFXPopup.PopupVPosition.TOP,JFXPopup.PopupHPosition.LEFT);
		});
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
	
	public void logout(ActionEvent e) throws IOException {
		piecesButton.getScene().getWindow().hide();		
		loadPage("/FXML/LoginPage.fxml", "Login");
	}
	
	public void update(ActionEvent e) {
		connection = handler.getConnection();
		String getInfo = "SELECT * FROM accounts WHERE email=?";
		String username = null,nom = null,prenom = null,email = null,phone = null,adress = null,password = null;
		int id = 0;
		@SuppressWarnings("unused")
		Date date;
		try {
			pst = connection.prepareStatement(getInfo);
			pst.setString(1, emailInfo.getText());
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()) {
				id = rs.getInt("id");
				username = rs.getString("username");
				nom = rs.getString("nom");
				prenom = rs.getString("prenom");
				email = rs.getString("email");
				phone = rs.getString("phone");
				adress = rs.getString("adress");
				password = rs.getString("password");
				date = rs.getDate("date");
			}
			
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/FXML/SignInPage.fxml"));
			loader.load();
			SignInControler controler = loader.getController();
			controler.setUpdate(true);
			controler.setTextField(id,nom,prenom,email,username,phone,adress,password, LocalDate.now());
			
			Parent root = loader.getRoot();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
			Stage primaryStage = new Stage();
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("SignIn");
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Img/iconeAplli (1).png")));
			primaryStage.setResizable(false);
			
			piecesButton.getScene().getWindow().hide();		
			
			
		} catch (SQLException | IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void goToUser(ActionEvent e) throws IOException {
		piecesButton.getScene().getWindow().hide();		
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/FXML/MainPage.fxml"));
		loader.load();
		MainPageControler controler = loader.getController();
		controler.displayInfo(idClient, nom, prenom, email, age, phone, adress, admin);
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
	
	public void goToPieces(ActionEvent e) throws IOException {
		piecesButton.getScene().getWindow().hide();		

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
	
	public void goToClients(ActionEvent e) throws IOException {
		piecesButton.getScene().getWindow().hide();		
		
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
		primaryStage.setTitle("Admin : Tableau de clients");
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
	
	public void nbPieces() {
		connection = handler.getConnection();
		String select = "SELECT stock FROM pieces";
		int count = 0;
		
			try {
				
				pst = connection.prepareStatement(select);
				ResultSet rs = pst.executeQuery();
				while(rs.next()) {
				count += rs.getInt("stock");
				}
				
				nbPieces.setText(withLargeIntegers(count) + " ");
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	public void valuePieces() {
		connection = handler.getConnection();
		String select = "SELECT * FROM pieces";
		long tt = 0;
		
		try {
			
			pst = connection.prepareStatement(select);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
			tt += rs.getInt("stock")*rs.getDouble("price");
			}
			
			valuePieces.setText(withLargeIntegers(tt) + " Dhs ");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void nbCommandes() {
		connection = handler.getConnection();
		String count = "SELECT count(*) AS nb FROM commande";
		int nb = 0;
		
		try {
			pst = connection.prepareStatement(count);
			ResultSet rs = pst.executeQuery();
			rs.next();
			nb = rs.getInt("nb");
			
			nbCommade.setText(withLargeIntegers(nb) + " ");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void pieChart() {
		connection = handler.getConnection();
		ObservableList<PieChart.Data> list = FXCollections.observableArrayList();
		String select = "SELECT stock FROM pieces WHERE categoryId=?";
		String select2 = "SELECT name FROM category WHERE id=?";
		int count = 0;
		
		for(int i=0; i<8; i++) {
			
			try {
				pst = connection.prepareStatement(select);
				pst.setString(1, String.valueOf(i+1));
				ResultSet rs = pst.executeQuery();
				while(rs.next()) {
					count += rs.getInt("stock");
				}
				
				pst = connection.prepareStatement(select2);
				pst.setString(1, String.valueOf(i+1));
				ResultSet st = pst.executeQuery();
				st.next();
				
				if(count > 0) {
					list.add(new PieChart.Data(st.getString("name"), count));
				}
						
				count = 0;

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
			
		pieChart.setData(list);
		pieChart.setLegendVisible(false);
		pieChart.setTitle("Pièces par categorie");
	}
	
	public static String withLargeIntegers(double value) {
	    DecimalFormat df = new DecimalFormat("###,###,###");
	    return df.format(value);
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
		nameInfo.setText(StringUtils.capitalize(nom) + " " + StringUtils.capitalize(prenom));
		emailInfo.setText(email);
		ageInfo.setText(String.valueOf(age));
		phoneInfo.setText(phone);
		adressInfo.setText(adress);
	}
	
	public void displayPagination() {
		pagination.setPageFactory((Integer test) -> createPage(test));
	}
	
	public Parent createPage(int test) {


		connection = handler.getConnection();
		
		String select = "SELECT * FROM pieces WHERE stock>0 ORDER BY RAND() LIMIT 1";
		String selectCat = "SELECT * FROM category WHERE id=?";
		String name = null, description = null, categoryId = null, img = null;
		int id = 0, stock = 0;
		double price = 0;
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/FXML/Pagination.fxml"));
		try {
			loader.load();
			pst = connection.prepareStatement(select);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				pst = connection.prepareStatement(selectCat);
				pst.setString(1, rs.getString("categoryId"));
				ResultSet st = pst.executeQuery();		
				st.next();
				name = rs.getString("name");
				description = rs.getString("description");
				categoryId = st.getString("name");
				id = rs.getInt("id");
				price = rs.getDouble("price");
 				img = st.getString("image");
			}	
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
		PaginationControler controler = loader.getController();
		controler.setData(new Pieces(name,description,id,categoryId,stock,price,img));
		Parent pane = loader.getRoot();
		return pane;		
	}
	
	
	
}
