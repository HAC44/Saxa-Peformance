package Controller;

import java.net.URL;

import java.util.ResourceBundle;

import application.Pieces;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PaginationControler implements Initializable {
	
	@FXML
    private ImageView catImg;

    @FXML
    private Label nameLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label categoryLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label idLabel;
    
    Pieces pieces;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
	
	public void setData(Pieces pieces) {
		this.pieces = pieces;
		nameLabel.setText(pieces.getName());
		descriptionLabel.setText(pieces.getDescription());
		categoryLabel.setText(String.valueOf(pieces.getCatId()));
		priceLabel.setText(String.valueOf(pieces.getPrice()) + " DH");
		idLabel.setText("ID : " + String.valueOf(pieces.getId()));
		Image i = new Image(getClass().getResourceAsStream(pieces.getImage()));
		catImg.setImage(i);
	}

}
