package application;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class CustomAlert extends Alert {

	public CustomAlert(AlertType alertType, String mess) {
		super(alertType);
		this.setTitle(alertType.toString());
		this.setContentText(mess);
		this.setHeaderText(null);
		Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("/Img/iconeAplli (1).png"));
		stage.getScene().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	}

}
