package Controller;

import java.io.IOException;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.sun.xml.internal.ws.util.StringUtils;

import DbConnection.DbHandler;
import application.Commande;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Callback;

public class HistoryAdminControler implements Initializable {
	
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
	    private JFXButton deleteButton;

	    @FXML
	    private TreeTableView<Commande> historyTable;

	    @FXML
	    private TreeTableColumn<Commande, Integer> idColumn;

	    @FXML
	    private TreeTableColumn<Commande, String> nameColumn;

	    @FXML
	    private TreeTableColumn<Commande, Integer> idClientColumn;

	    @FXML
	    private TreeTableColumn<Commande, String> numComColumn;

	    @FXML
	    private TreeTableColumn<Commande, String> piecesColumn;

	    @FXML
	    private TreeTableColumn<Commande, Integer> nbColumn;

	    @FXML
	    private TreeTableColumn<Commande, Double> priceColumn;

	    @FXML
	    private TreeTableColumn<Commande, String> orderColumn;
	    
	    Connection connection;
	    DbHandler handler;
	    PreparedStatement pst;
	    
	    TreeItem<Commande> commande;
	    
	    String nom="", prenom="", email="", phone="", adress="", admin="";
	    int age, idClient;
	    
	    ObservableList<Commande> comList = FXCollections.observableArrayList();

	    
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
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		handler = new DbHandler();
		loadData();
	}
	
	public void refresh() {
		comList.clear();
		connection = handler.getConnection();
		String select = "SELECT * FROM commande";
		String selectName = "SELECT * FROM accounts WHERE id=?";
		String selectPiece = "SELECT * FROM pieces WHERE id=?";
		TreeItem<Commande> root = new TreeItem<>();
		
		try {
			pst = connection.prepareStatement(select);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				int idClient = rs.getInt("idClient");
				pst = connection.prepareStatement(selectName);
				pst.setString(1, String.valueOf(idClient));
				ResultSet st = pst.executeQuery();
				st.next();
				String name = StringUtils.capitalize(st.getString("nom")) + " " + StringUtils.capitalize(st.getString("prenom"));
				String etat = null;
				if(rs.getString("delivery").equals("NO")) {
					etat = "En cours";
				} else {
					etat = "Livrée";
				}
				
				TreeItem<Commande> row = new TreeItem<>(new Commande(
							rs.getInt("id"),
							idClient,
							name,
							rs.getString("numCom"),
							"Voir",
							rs.getInt("nb"),
							rs.getDouble("price"),
							etat
							));
				
				String a = rs.getString("pieces");
				List<String> myList = new ArrayList<String>(Arrays.asList(a.split("-")));
				for (int i = 0; i<myList.size(); i++) {
					String instance = myList.get(i);
					List<String> two = new ArrayList<String>(Arrays.asList(instance.split("/")));
					pst = connection.prepareStatement(selectPiece);
					pst.setString(1, String.valueOf(two.get(0)));
					ResultSet po = pst.executeQuery();
					while(po.next()) {
						TreeItem<Commande> secondaryRow = new TreeItem<>(new Commande(
								rs.getInt("id"),
								idClient,
								po.getString("name"),
								Integer.valueOf(two.get(1)),
								po.getDouble("price")*Integer.valueOf(two.get(1))
								));
						row.getChildren().add(secondaryRow);
					}
				}
				
				root.getChildren().add(row);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		historyTable.setRoot(root);
		historyTable.setShowRoot(false);
	}
	
	public void loadData() {
		connection = handler.getConnection();
		refresh();
		
		idColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Commande, Integer>, ObservableValue<Integer>>() {
			public ObservableValue<Integer> call(TreeTableColumn.CellDataFeatures<Commande, Integer> param) {
				ObservableValue<Integer> obsInt = new ReadOnlyObjectWrapper<>(param.getValue().getValue().getId());
				return obsInt;
			}
		});
		
		nameColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Commande, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Commande, String> param) {
				ObservableValue<String> obsInt = new ReadOnlyObjectWrapper<>(param.getValue().getValue().getNom());
				return obsInt;
			}
		});
		
		idClientColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Commande, Integer>, ObservableValue<Integer>>() {
			public ObservableValue<Integer> call(TreeTableColumn.CellDataFeatures<Commande, Integer> param) {
				ObservableValue<Integer> obsInt = new ReadOnlyObjectWrapper<>(param.getValue().getValue().getIdClient());
				return obsInt;
			}
		});
		
		numComColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Commande, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Commande, String> param) {
				ObservableValue<String> obsInt = new ReadOnlyObjectWrapper<>(param.getValue().getValue().getNumCom());
				return obsInt;
			}
		});
		
		piecesColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Commande, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Commande, String> param) {
				ObservableValue<String> obsInt = new ReadOnlyObjectWrapper<>(param.getValue().getValue().getPieces());
				return obsInt;
			}
		});
		
		nbColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Commande, Integer>, ObservableValue<Integer>>() {
			public ObservableValue<Integer> call(TreeTableColumn.CellDataFeatures<Commande, Integer> param) {
				ObservableValue<Integer> obsInt = new ReadOnlyObjectWrapper<>(param.getValue().getValue().getNbPieces());
				return obsInt;
			}
		});
		
		priceColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Commande, Double>, ObservableValue<Double>>() {
			public ObservableValue<Double> call(TreeTableColumn.CellDataFeatures<Commande, Double> param) {
				ObservableValue<Double> obsInt = new ReadOnlyObjectWrapper<>(param.getValue().getValue().getPrice());
				return obsInt;
			}
		});
		
		orderColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Commande, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Commande, String> param) {
				ObservableValue<String> obsInt = new ReadOnlyObjectWrapper<>(param.getValue().getValue().getOrder());
				return obsInt;
			}
		});
		
	}
	
	public void delete(ActionEvent e) {
		commande = historyTable.getSelectionModel().getSelectedItem();
		connection = handler.getConnection();
		
		String delete = "DELETE FROM commande WHERE id=?";
		
		try {
			pst = connection.prepareStatement(delete);
			pst.setInt(1, commande.getValue().getId());
			pst.execute();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		refresh();
	}
	
	public void confirm(ActionEvent e) {
		commande = historyTable.getSelectionModel().getSelectedItem();
		connection = handler.getConnection();
		
		String update = "UPDATE commande SET delivery='YES' WHERE id=?";
		
		try {
			pst = connection.prepareStatement(update);
			pst.setInt(1, commande.getValue().getId());
			pst.executeUpdate();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		refresh();
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
	
	public void goToClients(ActionEvent e) throws IOException {
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
		primaryStage.setTitle("Admin : Clients");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Img/iconeAplli (1).png")));
		primaryStage.setResizable(false);
	}

}
