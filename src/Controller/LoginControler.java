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
import application.CustomAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class LoginControler implements Initializable {
	
	@FXML
    private JFXTextField usernameField;

    @FXML
    private JFXButton login;

    @FXML
    private JFXButton signIn;

    @FXML
    private JFXPasswordField passwordField;
    
    @FXML
    private JFXTextField showPassword;

    @FXML
    private JFXCheckBox checkBox;
    
    Connection connection;
    DbHandler handler;
    PreparedStatement pst;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		handler = new DbHandler(); 
		this.sPassword(null);
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
	
	@SuppressWarnings("unused")
	public void login(ActionEvent e) {
		connection = handler.getConnection();
		
		if ((usernameField.getText().isEmpty() || checkBox.isSelected() || showPassword.getText().isEmpty()) &&
				(usernameField.getText().isEmpty() || passwordField.getText().isEmpty())) {
	
			CustomAlert alert = new CustomAlert(AlertType.ERROR, "Veuillez remplir tous les champs");
			alert.showAndWait();
			
		} else {
			
			String check1 = "SELECT * FROM accounts WHERE username=? OR email=?";
			String check2 = "SELECT * FROM accounts WHERE password=? AND username=?";
			int count = 0;
			String username = "";
			String nom="", prenom="", email="", phone="", adress="", admin="";
			Date date = null;
			int id = 0;
			try {
				pst = connection.prepareStatement(check1);
				pst.setString(1, usernameField.getText());
				pst.setString(2, usernameField.getText());
				ResultSet rs = pst.executeQuery();
				while(rs.next()) {
					count += 1;
					id = rs.getInt("id");
					username = rs.getString("username");
					nom = rs.getString("nom");
					prenom = rs.getString("prenom");
					email = rs.getString("email");
					phone = rs.getString("phone");
					adress = rs.getString("adress");
					date = rs.getDate("date");
					admin = rs.getString("admin");
				}
				
				if(count == 1 || count == 2) {
					pst = connection.prepareStatement(check2);
					if(checkBox.isSelected()) {
						pst.setString(1, showPassword.getText());
					} else {
						pst.setString(1, passwordField.getText());
					}
					pst.setString(2, username);
					rs = pst.executeQuery();
					count = 0;
					while(rs.next()) {
						count += 1;
					}
					
					if(count == 1) {
						Period period = Period.between(date.toLocalDate(), LocalDate.now());
						login.getScene().getWindow().hide();
						FXMLLoader loader = new FXMLLoader();
						Parent root;
						if (admin.equals("NO")) {
							loader.setLocation(getClass().getResource("/FXML/MainPage.fxml"));
							root = loader.load();
							MainPageControler main = loader.getController();
							main.displayInfo(id, nom, prenom, email, period.getYears(), phone, adress, admin);
						} else {
							loader.setLocation(getClass().getResource("/FXML/MainPageAdmin.fxml"));
							root = loader.load();
							MainPageAdminControler main = loader.getController();
							main.displayInfo(id, nom, prenom, email, period.getYears(), phone, adress, admin);
						}
						Scene scene = new Scene(root);
						scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
						Stage primaryStage = new Stage();
						primaryStage.setScene(scene);
						primaryStage.show();
						primaryStage.setTitle("Home Page");
						primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Img/iconeAplli (1).png")));
						primaryStage.setResizable(false);
						
					} else {
						CustomAlert alert = new CustomAlert(AlertType.ERROR, "Les informations saisies sont erronées");
						alert.showAndWait();
					}
					
				} else {
					CustomAlert alert = new CustomAlert(AlertType.ERROR, "Compte inexistant");
					alert.showAndWait();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
	
	}
	
	public void signIn(ActionEvent e) throws IOException {
		login.getScene().getWindow().hide();
		loadPage("/FXML/SignInPage.fxml","Sign In");
	}
	
	public void sPassword(ActionEvent e) {
		if(checkBox.isSelected()) {
			showPassword.setText(passwordField.getText());
			showPassword.setVisible(true);
			passwordField.setVisible(false);
			return;
		}
		passwordField.setText(showPassword.getText());
		showPassword.setVisible(false);
		passwordField.setVisible(true);
	}
}
