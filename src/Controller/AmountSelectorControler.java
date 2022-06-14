package Controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import DbConnection.DbHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class AmountSelectorControler implements Initializable {
	

    @FXML
    private Spinner<Integer> amount;

    @FXML
    private JFXButton confirmButton;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private Label idLabel;
    
    int id;
    double price;
    
    Connection connection;
    DbHandler handler;
    PreparedStatement pst;
    
    String nom, prenom, email, phone, adress, admin;
    int age,idClient;
    
	public void displayInfo(int id, String nom, String prenom, String email, int age, String phone, String adress, String admin) {
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
		this.age = age;
		this.phone = phone;
		this.adress = adress;
		this.admin = admin;
	}
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		handler = new DbHandler();
		this.idClient = MainPageControler.getInstance().getIdClient();
	}
		
	public void setSpinner(int id, double price) {
		this.id = id;
		this.price = price;
		connection = handler.getConnection();
		String select = "SELECT * FROM pieces WHERE id=?";
		SpinnerValueFactory<Integer> value = null;
			try {
				pst = connection.prepareStatement(select);
				pst.setString(1, String.valueOf(id));
				ResultSet rs = pst.executeQuery();
				while(rs.next()) {
					value = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,rs.getInt("stock"));
					value.setValue(0); 			
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		amount.setValueFactory(value);
	}
	
	public void cancel(ActionEvent e) {
		cancelButton.getScene().getWindow().hide();
	}
	
	public void addToCart(ActionEvent e) {
		connection = handler.getConnection();
		
		String check = "SELECT * FROM panier WHERE piecesId=? AND idClient=?";
		String insert = "INSERT INTO panier(piecesId,idClient,amount,price) VALUES (?,?,?,?)";
		String update = "UPDATE pieces SET stock=stock-? WHERE id=?";
		String update2 = "UPDATE panier SET amount=amount+? WHERE piecesId=?";
		int count=0;
		
		try {
			pst = connection.prepareStatement(check);
			pst.setString(1, String.valueOf(id));
			pst.setString(2, String.valueOf(idClient));
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				count++;
			}
			
			if(count == 0) {
				
				pst = connection.prepareStatement(insert);
				pst.setString(1, String.valueOf(id));
				pst.setString(2, String.valueOf(idClient));
				pst.setString(3, String.valueOf(amount.getValue()));
				pst.setString(4, String.valueOf(price));
				pst.execute();
				
			} else {
				
				pst = connection.prepareStatement(update2);
				pst.setString(1, String.valueOf(amount.getValue()));
				pst.setString(2, String.valueOf(id));
				pst.execute();
			}
			
			pst = connection.prepareStatement(update);
			pst.setString(1, String.valueOf(amount.getValue()));
			pst.setString(2, String.valueOf(id));
			pst.execute();
			
			
			PiecesPageControler.getInstance().refresh();
			confirmButton.getScene().getWindow().hide();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
	}	

}
