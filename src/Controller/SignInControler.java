package Controller;

import java.io.IOException;


import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SignInControler implements Initializable {
	
	@FXML
    private JFXTextField usernameField;

    @FXML
    private JFXPasswordField passwordField;

    @FXML
    private JFXButton signIn;

    @FXML
    private JFXTextField emailField;

    @FXML
    private JFXDatePicker dateField;

    @FXML
    private JFXButton login;
    
    @FXML
    private JFXPasswordField confirmPasswordField;

    @FXML
    private JFXTextField phoneField;

    @FXML
    private JFXTextField adressField;
    
    @FXML
    private JFXCheckBox checkBox;
    
    @FXML
    private JFXTextField showPassword;
    
    @FXML
    private JFXTextField confirmShowPassword;
    
    @FXML
    private JFXTextField nameField;

    @FXML
    private JFXTextField prenomField;
    
    Connection connection;
    DbHandler handler;
    PreparedStatement pst;
    private boolean update;
    String insert;
    int id;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		handler = new DbHandler();
		this.sPassword(null);
		setDatePicker();
	}
	
	public void setDatePicker() {
		
		final Callback<DatePicker, DateCell> dayCellFactory;

		dayCellFactory = (dateField) -> new DateCell() {
		    @Override
		    public void updateItem(LocalDate item, boolean empty) {
		        super.updateItem(item, empty);
		        if (item.isAfter(LocalDate.now())) { //Disable all dates after required date
		            setDisable(true);
		            setStyle("-fx-background-color: #ffc0cb;"); //To set background on different color
		        }
		    }
		};
		
		dateField.setDayCellFactory(dayCellFactory);
		dateField.setShowWeekNumbers(false);
	}

	
	public void loadPage(String url, String title) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(url));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
		Stage primaryStage = new Stage();primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setTitle(title);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Img/iconeAplli (1).png")));
		primaryStage.setResizable(false);
	}
	
	void setUpdate(boolean b) {
		this.update = b;
	}
	
	void setTextField(int id,String nom, String prenom, String email, String username, String phone, String adress, String password, LocalDate date) {
	   	this.id = id;
	   	usernameField.setText(username);
	   	nameField.setText(nom);
	   	prenomField.setText(prenom);
	   	emailField.setText(email);
	   	passwordField.setText(password);
		confirmPasswordField.setText(password);
	   	phoneField.setText(phone);
	   	adressField.setText(adress);
	   	dateField.setValue(date);
	}
	
	public void signIn(ActionEvent e) {
		connection = handler.getConnection();
		String check = "SELECT * FROM accounts WHERE username=? OR email=?";
		int count = 0;

		if(update == false) {
			insert = "INSERT INTO accounts(nom,prenom,username,email,phone,adress,password,date) VALUES(?,?,?,?,?,?,?,?)";
			try {
				pst = connection.prepareStatement(check);
				pst.setString(1, usernameField.getText());
				pst.setString(2, emailField.getText());
				ResultSet rs = pst.executeQuery();
				while(rs.next()) {
					count += 1; 
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
		} else {
			insert = "UPDATE accounts SET nom=?, prenom=?, username=?, email=?, phone=?, adress=?, password=?, date=? WHERE id=" + id;
		}
		
		
		String one, two;
		if(checkBox.isSelected()) {
			one = showPassword.getText();
			two = confirmShowPassword.getText();
		} else {
			one = passwordField.getText();
			two = confirmPasswordField.getText();	
		}
		
		if(usernameField.getText().isEmpty() || nameField.getText().isEmpty() || prenomField.getText().isEmpty() 
				|| emailField.getText().isEmpty() || (passwordField.getText().isEmpty() && showPassword.getText().isEmpty()) 
				|| (confirmPasswordField.getText().isEmpty() && confirmShowPassword.getText().isEmpty()) || phoneField.getText().isEmpty() 
				|| adressField.getText().isEmpty() || String.valueOf(dateField.getValue()) == "") {
			
			CustomAlert alert = new CustomAlert(AlertType.ERROR, "Veuillez remplir tous les champs");
			alert.showAndWait();
				
		} else {
				
			if(one.equals(two)) {
					
				try {
						
					if(count == 0) {
						pst = connection.prepareStatement(insert);
						pst.setString(1, nameField.getText());
						pst.setString(2, prenomField.getText());
						pst.setString(3, usernameField.getText());
						pst.setString(4, emailField.getText());
						pst.setString(5, phoneField.getText());
						pst.setString(6, adressField.getText());
						if (checkBox.isSelected()) {
							pst.setString(7, showPassword.getText());
						} else {
							pst.setString(7, passwordField.getText());
						}	
						pst.setString(8, String.valueOf(dateField.getValue()));
						pst.execute();
						CustomAlert alert = new CustomAlert(AlertType.CONFIRMATION, "Le compte a été créé");
						alert.showAndWait();
						
						signIn.getScene().getWindow().hide();
						loadPage("/FXML/LoginPage.fxml","Login");
						
					} else {
						CustomAlert alert = new CustomAlert(AlertType.ERROR, "Le compte existe deja");
						alert.showAndWait();
					}
						
				} catch (SQLException | IOException e1) {
					e1.printStackTrace();
				}
					
			} else {
				CustomAlert alert = new CustomAlert(AlertType.ERROR, "les mots de passe saisis ne sont pas identiques");
				alert.showAndWait();
			}
				
		}
					
	}
	
	public void login(ActionEvent e) throws IOException {
		signIn.getScene().getWindow().hide();
		loadPage("/FXML/LoginPage.fxml","Login");
	}
	
	public void sPassword(ActionEvent e) {
		if(checkBox.isSelected()) {
			showPassword.setText(passwordField.getText());
			confirmShowPassword.setText(confirmPasswordField.getText());
			showPassword.setVisible(true);
			confirmShowPassword.setVisible(true);
			passwordField.setVisible(false);
			confirmPasswordField.setVisible(false);
			return;
		}
		passwordField.setText(showPassword.getText());
		confirmPasswordField.setText(confirmShowPassword.getText());
		showPassword.setVisible(false);
		confirmShowPassword.setVisible(false);
		passwordField.setVisible(true);
		confirmPasswordField.setVisible(true);

	}

}
