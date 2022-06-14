package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DevisControler implements Initializable {
	
	@FXML
    private TableView<Pieces> cartTable;

    @FXML
    private TableColumn<Pieces, Integer> idColumn;

    @FXML
    private TableColumn<Pieces, String> nameColumn;

    @FXML
    private TableColumn<Pieces, Integer> nbColumn;

    @FXML
    private TableColumn<Pieces, Double> priceColumn;

    @FXML
    private JFXTextField idField;

    @FXML
    private Pane piecesPane;

    @FXML
    private JFXButton confirmButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Label totalPrice;

    @FXML
    private Button refreshButton;

    @FXML
    private Button printButton;

    @FXML
    private JFXButton homeButton;

    @FXML
    private JFXButton piecesButton;

    @FXML
    private JFXButton devisButton;
    
    @FXML
    private Button clearButton;
    
    Connection connection;
    DbHandler handler;
    PreparedStatement pst;
    Pieces piece;
    
    String nom="", prenom="", email="", phone="", adress="", admin="";
    int age,id;
    
    ObservableList<Pieces> piecesList = FXCollections.observableArrayList();
    
    public static DevisControler instance;
    
    public DevisControler() {
    	instance = this;
    }
    
    public static DevisControler getInstance() {
    	return instance;
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
		loadData();
		setTotalPrice();
	}
	
	public void refreshTable() {
		connection = handler.getConnection();
		piecesList.clear();
		String select = "SELECT * FROM panier WHERE idClient=?";
		String selectName = "SELECT * FROM pieces WHERE id=?";
		
		try {
			pst = connection.prepareStatement(select);
			pst.setString(1, String.valueOf(MainPageControler.getInstance().getIdClient()));
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				int id = rs.getInt("piecesId");
				int panierId = rs.getInt("id");
				pst = connection.prepareStatement(selectName);
				pst.setString(1, String.valueOf(id));
				ResultSet st = pst.executeQuery();
				while(st.next()) {
					piecesList.add(new Pieces(
							panierId,
							st.getString("name"),
							id,
							rs.getInt("amount"),
							st.getDouble("price")*rs.getInt("amount")
							));
					cartTable.setItems(piecesList);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void refreshPage(ActionEvent e) {
		refreshTable();
		piecesPane.getChildren().clear();
		setTotalPrice();
		idField.clear();
	}
	
	public void loadData() {
		connection = handler.getConnection();
		refreshTable();
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		nbColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
	}

	public void deleteData() {
		piece = cartTable.getSelectionModel().getSelectedItem();
		connection = handler.getConnection();
		String delete = "DELETE FROM panier WHERE id=" + piece.getPanierId();
		String update = "UPDATE pieces SET stock=stock+? WHERE id=" + piece.getId();
		int newStock = piece.getStock();
		
		try {
			pst = connection.prepareStatement(delete);
			pst.execute();
			
			pst = connection.prepareStatement(update);
			pst.setString(1, String.valueOf(newStock));
			pst.execute();
			
			refreshTable();
			setTotalPrice();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteData(Pieces piece) {
		this.piece = piece;
		connection = handler.getConnection();
		String delete = "DELETE FROM panier WHERE id=" + piece.getPanierId();
		String update = "UPDATE pieces SET stock=stock+? WHERE id=" + piece.getId();
		int newStock = piece.getStock();
		
		try {
			pst = connection.prepareStatement(delete);
			pst.execute();
			
			pst = connection.prepareStatement(update);
			pst.setString(1, String.valueOf(newStock));
			pst.execute();
			
			refreshTable();
			setTotalPrice();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void clearData() {
		connection = handler.getConnection();
		refreshTable();
		boolean check = true;
		while(check) {
			if(piecesList.isEmpty()) {
				check = false;
			} else {
				deleteData(piecesList.get(piecesList.size()-1));
			}
		}
		
		piecesPane.getChildren().clear();
		idField.clear();
		
//		String alter = "ALTER TABLE panier AUTO_INCREMENT = 1";
//		
//		try {
//			pst = connection.prepareStatement(alter);
//			pst.execute();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}		
	}
	
	public void search() {
		connection = handler.getConnection();
		String select = "SELECT * FROM pieces WHERE id=?";
		String select2 = "SELECT * FROM category WHERE id=?";
		piecesPane.getChildren().clear();
		Pieces piece = null;
		
		try {
			pst = connection.prepareStatement(select);
			pst.setString(1, idField.getText());
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				int catId = rs.getInt("categoryId");
				pst = connection.prepareStatement(select2);
				pst.setString(1, String.valueOf(catId));
				ResultSet s = pst.executeQuery();
				s.next();
				String catName = s.getString("name");
	    		String catImg = s.getString("image");
				piece = new Pieces(
						rs.getString("name"),
	    				rs.getString("description"),
	    				rs.getInt("id"),
	    				catName,
	    				rs.getInt("stock"),
	    				rs.getDouble("price"),
	    				catImg
						);
			}
			
			FXMLLoader root = new FXMLLoader();
			root.setLocation(getClass().getResource("/FXML/Pieces.fxml"));
			AnchorPane pane = root.load();
			
			PiecesControler main = root.getController();
			main.setData(piece);
			
			piecesPane.getChildren().add(pane);

			
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
		
		
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
	
	public void changeToPieces(ActionEvent e) throws IOException {
		piecesButton.getScene().getWindow().hide();

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/FXML/PiecesPage.fxml"));
		loader.load();
		PiecesPageControler controler = loader.getController();
		controler.displayInfo(id,nom, prenom, email, age, phone, adress, admin);
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
	
	public void setTotalPrice() {
		double ttc = 0;
		Pieces piece;
		for(int i=0; i<piecesList.size(); i++) {
			piece = piecesList.get(i);
			ttc += piece.getPrice();
		}
		totalPrice.setText(String.valueOf(ttc) + " DH");
	}
	
	public void print(ActionEvent e) throws IOException {
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/FXML/Confirm.fxml"));
		loader.load();
		ConfirmControler controler = loader.getController();
		controler.displayInfo(nom, prenom, email, age, phone, adress, admin);
		Parent root = loader.getRoot();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
		Stage primaryStage = new Stage();
		primaryStage.setScene(scene);
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		primaryStage.show();
		primaryStage.setTitle("Pieces");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Img/iconeAplli (1).png")));
		primaryStage.setResizable(false);
		
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
	
	public String generateNCommande(String first, int id, String second, int nb) {
		return first.toUpperCase() + id + second.toUpperCase() + nb;
	}
	
	
}
