package Controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.jfoenix.controls.*;
import DbConnection.DbHandler;
import application.CustomAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AddPiecesControler implements Initializable {
	
	 	@FXML
	    private JFXComboBox<String> categoryCombo;

	    @FXML
	    private JFXTextField nameField;

	    @FXML
	    private JFXTextArea descriptionField;

	    @FXML
	    private Spinner<Integer> nbSpinner;

	    @FXML
	    private Spinner<Double> priceSpinner;

	    @FXML
	    private JFXButton confirmButton;

	    @FXML
	    private JFXButton cancelButton;

	    @FXML
	    private ImageView catImg;
	    
	    Connection connection;
	    DbHandler handler;
	    PreparedStatement pst;
	    private boolean update;
	    int piecesId;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		handler = new DbHandler();
		refreshComboCategory();
		setSpinner();
		loadImage();
		
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
	
	public void cancel(ActionEvent e) {
		cancelButton.getScene().getWindow().hide();
	}
	
	public void loadImage() {
		categoryCombo.setOnAction(e -> {
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
		});
	}
	
	@SuppressWarnings("all")
	public void setSpinner() {
		SpinnerValueFactory factoryStock = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000);
		nbSpinner.setValueFactory(factoryStock);
		nbSpinner.setEditable(true);
		TextFormatter formatterStock = new TextFormatter(factoryStock.getConverter(), factoryStock.getValue());
		nbSpinner.getEditor().setTextFormatter(formatterStock);
		factoryStock.valueProperty().bindBidirectional(formatterStock.valueProperty());
		
		SpinnerValueFactory factoryPrice = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 10000000);
		priceSpinner.setValueFactory(factoryPrice);
		priceSpinner.setEditable(true);
		TextFormatter formatterPrice = new TextFormatter(factoryPrice.getConverter(), factoryPrice.getValue());
		priceSpinner.getEditor().setTextFormatter(formatterPrice);
		factoryPrice.valueProperty().bindBidirectional(formatterPrice.valueProperty());
	}
	
	public void addPieces(ActionEvent e) {
		connection = handler.getConnection();
		
		if(nameField.getText().isEmpty() || descriptionField.getText().isEmpty() ||
				priceSpinner.getValue() == 0) {
			CustomAlert alert = new CustomAlert(AlertType.ERROR, "Veuillez remplir tous les champs");
			alert.showAndWait();
		} else {
			insert();
		}
		
	}

	@SuppressWarnings("unused")
	public void insert() {
		int count = 0;
		
		String select = "SELECT name FROM pieces WHERE name=?";
		String selectId = "SELECT id FROM category WHERE name=?"; 
		String insert; 
		
		if(update == false) {
			insert = "INSERT INTO pieces(name,description,price,stock,categoryId) VALUES (?,?,?,?,?)";
			
			try {
				pst = connection.prepareStatement(select);
				pst.setString(1, nameField.getText());
				ResultSet rs = pst.executeQuery();
				while(rs.next()) {
					count++;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		} else {
			insert = "UPDATE pieces SET name=?, description=?, price=?, stock=?, categoryId=? WHERE id=" + piecesId;
		}
		
		try {
			pst = connection.prepareStatement(selectId);
			pst.setString(1, categoryCombo.getValue().toString());
			ResultSet rs = pst.executeQuery();
			rs.next();
			int categoryId = rs.getInt("id");
			
			pst = connection.prepareStatement(insert);
			pst.setString(1, nameField.getText());
			pst.setString(2, descriptionField.getText());
			pst.setString(3, priceSpinner.getValue().toString());
			pst.setString(4, nbSpinner.getValue().toString());
			pst.setInt(5, categoryId);
			pst.executeUpdate();
			
			CustomAlert alert = new CustomAlert(AlertType.CONFIRMATION, "La piece a été ajouté avec succès");
			alert.showAndWait();
			
			nameField.setText(null);
			descriptionField.setText(null);
			categoryCombo.setValue(null);
			setSpinner();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void setUpdate(boolean update) {
		this.update = update;
	}
	
	public void setText(int id, String name, String description, double price, int stock, String category) {
		piecesId = id;
		nameField.setText(name);
		descriptionField.setText(description);
		priceSpinner.getValueFactory().setValue(price);
		nbSpinner.getValueFactory().setValue(stock);
		categoryCombo.setValue(category);
	}
	
}
