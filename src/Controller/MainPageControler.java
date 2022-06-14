package Controller;

import java.io.IOException;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

import com.jfoenix.controls.*;
import com.sun.xml.internal.ws.util.StringUtils;

import DbConnection.DbHandler;
import application.Pieces;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainPageControler implements Initializable {
	
		@FXML
	    private JFXButton homeButton;

	    @FXML
	    private JFXButton piecesButton;

	    @FXML
	    private JFXButton devisButton;
	    
	    @FXML
	    private JFXComboBox<String> piecesCombo;

	    @FXML
	    private JFXComboBox<String> categoryCombo;
	    
	    @FXML
	    private GridPane grid;

	    @FXML
	    private Label nameLabel;

	    @FXML
	    private Label descriptionLabel;

	    @FXML
	    private Label stockLabel;

	    @FXML
	    private Label priceLabel;

	    @FXML
	    private Label idLabel;

	    @FXML
	    private JFXButton confirmCategory;

	    @FXML
	    private JFXButton confirmPieces;
	    
	    @FXML
	    private ImageView image;

	    @FXML
	    private AnchorPane homePane;

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
	    private Button menuShower;
	    
	    @FXML
	    private VBox toolBar;
	    
	    @FXML
	    private VBox overflow;

	    @FXML
	    private JFXButton logout;

	    @FXML
	    private JFXButton modify;
	    
	    @FXML
	    private JFXButton adminButton;
	    
	    @FXML
	    private Pagination pagination;

    
	    Connection connection;
	    DbHandler handler;
	    PreparedStatement pst;
	    String nom="", prenom="", email="", phone="", adress="", admin="";
	    int age,id;
	    
	    private static MainPageControler instance;
	    
	    public MainPageControler() {
	    	instance = this;
	    }
	    
	    public static MainPageControler getInstance() {
	    	return instance;
	    }
	    
	    public int getIdClient() {
	    	return id;
	    }
	    
	    public String getAdmin() {
	    	return admin;
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		handler = new DbHandler();
		refreshComboCategory();
		refreshComboPieces();
		displayPagination();
		grid.setVisible(false);
		piecesCombo.setVisible(false);
		confirmPieces.setVisible(false);
		
		overflow.setVisible(false);
		openMenu();
		
	}
	
	@FXML
	private void openMenu() {
		JFXPopup popup  = new JFXPopup(overflow); 
		popup.setWidth(150);
		popup.setHeight(150);
		
		menuShower.setOnMouseClicked(e -> {
			if (admin.equals("NO")) {
				adminButton.setVisible(false);
			}
			overflow.setVisible(true);
			popup.show(toolBar,JFXPopup.PopupVPosition.TOP,JFXPopup.PopupHPosition.LEFT);
		});
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
	
	public void goToAdmin() throws IOException {
		piecesButton.getScene().getWindow().hide();
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/FXML/MainPageAdmin.fxml"));
		loader.load();
		MainPageAdminControler controler = loader.getController();
		controler.displayInfo(id, nom, prenom, email, age, phone, adress, admin);
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

	public void refreshComboCategory() {
		connection = handler.getConnection();
		String select = "SELECT name FROM category";
		
		try {
			pst = connection.prepareStatement(select);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				categoryCombo.getItems().add(rs.getString("name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	public void refreshComboPieces() {
		categoryCombo.setOnAction(e -> {
			grid.setVisible(false);
			confirmPieces.setVisible(true);
			piecesCombo.setVisible(true);
			piecesCombo.getItems().clear();
			connection = handler.getConnection();
			String selectCat = "SELECT * FROM category WHERE name=?";
			String select = "SELECT * FROM pieces WHERE categoryId=?";
			String i = null;
			int id = 0;
			
			try {
				pst = connection.prepareStatement(selectCat);
				pst.setString(1, categoryCombo.getValue());
				ResultSet rs = pst.executeQuery();
				while(rs.next()) {
					id = rs.getInt("id");
					i = rs.getString("image");
				}
				
				image.setImage(new Image(getClass().getResourceAsStream(i)));
				
				pst = connection.prepareStatement(select);
				pst.setString(1, String.valueOf(id));
				rs = pst.executeQuery();
				while(rs.next()) {
					piecesCombo.getItems().add(rs.getString("name"));
				}
				
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
		
	}
	
	public void displayPieces(ActionEvent e) {
		connection = handler.getConnection();
		String selectPie = "SELECT * FROM pieces WHERE name=?";
		
		try {
			pst = connection.prepareStatement(selectPie);
			pst.setString(1, piecesCombo.getValue());
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				idLabel.setText(rs.getString("id"));
				nameLabel.setText(rs.getString("name"));
				descriptionLabel.setText(rs.getString("description"));
				stockLabel.setText(rs.getString("stock") + " unites");
				priceLabel.setText(rs.getString("price") + " DH");
			}
			
			grid.setVisible(true);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}	
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
	
	public void displayInfo(int id,String nom, String prenom, String email, int age, String phone, String adress, String admin) {
		this.id = id;
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
	
	public void changeToPieces(ActionEvent e) throws IOException {
		piecesButton.getScene().getWindow().hide();

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/FXML/PiecesPage.fxml"));
		loader.load();
		PiecesPageControler controler = loader.getController();
		controler.displayInfo(id, nom, prenom, email, age, phone, adress, admin);
		Parent root = loader.getRoot();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
		Stage primaryStage = new Stage();
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setTitle("Pieces");
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
