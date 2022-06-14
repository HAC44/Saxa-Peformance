package Controller;

import java.io.IOException;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;

import DbConnection.DbHandler;
import application.CustomAlert;
import application.Pieces;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PiecesControler implements Initializable {
	
	   @FXML
	    private Label nameLabel;

	    @FXML
	    private ImageView image;

	    @FXML
	    private Label descriptionLabel;

	    @FXML
	    private Label categoryLabel;

	    @FXML
	    private Label priceLabel;

	    @FXML
	    private Label stockLabel;
	    
	    @FXML
	    private Label idLabel;
	    
	    @FXML
	    private Button addToCart;
	    	    	    
	    Pieces pieces;
	    int id,stock;
	    double price;
	    
	    String nom, prenom, email, phone, adress, admin;
	    int age,idClient;
	    
	    Connection connection;
	    DbHandler handler;
	    PreparedStatement pst;
	
	public void setData(Pieces pieces) {
		this.pieces = pieces;
		nameLabel.setText(pieces.getName());
		descriptionLabel.setText(pieces.getDescription());
		categoryLabel.setText(String.valueOf(pieces.getCatId()));
		priceLabel.setText(String.valueOf(pieces.getPrice()) + " DH");
		price = pieces.getPrice();
		stockLabel.setText(String.valueOf(pieces.getStock()) + " Unites");
		stock = pieces.getStock();
		idLabel.setText(String.valueOf(pieces.getId()));
		id = pieces.getId();
		Image i = new Image(getClass().getResourceAsStream(pieces.getImage()));
		image.setImage(i);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		handler = new DbHandler();
		this.admin = MainPageControler.getInstance().getAdmin();
	}
	
	public void displayInfo(int id,String nom, String prenom, String email, int age, String phone, String adress, String admin) {
		this.idClient = id;
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
		this.age = age;
		this.phone = phone;
		this.adress = adress;
	}
	
	public void addToCart(ActionEvent e) throws IOException {
		
		if(admin.equals("NO")) {
			
			if(stock == 0) {
				CustomAlert alert = new CustomAlert(AlertType.ERROR, "Plus de pièces en stock");
				alert.showAndWait();
			} else {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("/FXML/AmountSelector.fxml"));
				Parent root = loader.load();

				AmountSelectorControler controler = loader.getController();
				controler.setSpinner(id,price);
				controler.displayInfo(idClient, nom, prenom, email, age, phone, adress, admin);
					
				Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
				Stage primaryStage = new Stage();
				primaryStage.setScene(scene);
				primaryStage.initModality(Modality.APPLICATION_MODAL);
				primaryStage.show();
				primaryStage.setTitle("Selectionneur de quantité");
				primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Img/iconeAplli (1).png")));
				primaryStage.setResizable(false);
			}

		} else {
			CustomAlert alert = new CustomAlert(AlertType.ERROR, "Vous ne pouvez pas passer de commande en tant qu'administrateur");
			alert.showAndWait();
		}
		
	}

}
