package Controller;

import java.io.FileOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXButton;
import com.sun.xml.internal.ws.util.StringUtils;

import DbConnection.DbHandler;
import application.CustomAlert;
import application.Pieces;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

public class ConfirmControler implements Initializable {
	
		@FXML
	    private JFXButton confirmButton;

	    @FXML
	    private JFXButton cancelButton;
	    
	    @FXML
	    private Label nameLabel;
	    
	    Connection connection;
	    DbHandler handler;
	    PreparedStatement pst;
	    Pieces piece;
	    
	    String nom="", prenom="", email="", phone="", adress="", admin="";
	    int age;
	 
	    

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		handler = new DbHandler();
	}
	
	public void print(ActionEvent e) {
		connection = handler.getConnection();
		
		String delete = "DELETE FROM panier WHERE idClient=?";
		String select = "SELECT id FROM accounts WHERE email=?";
		String selectPanier = "SELECT * FROM panier WHERE idClient=?";
		String selectPieces = "SELECT * FROM pieces WHERE id=?";
		String insert = "INSERT INTO commande(numCom,idClient,nb,price,pieces) VALUES (?,?,?,?,?)";
		int idClient = 0, nbTotal = 0, idPiece;
		double priceTotal = 0;
		List<String> piecesArray = new ArrayList<String>();
  		
		try {
			
			pst = connection.prepareStatement(select);
			pst.setString(1, email);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				idClient = rs.getInt("id");
			}
			
			pst = connection.prepareStatement(selectPanier);
			pst.setString(1, String.valueOf(idClient));
			ResultSet st = pst.executeQuery();
			while(st.next()) {
				nbTotal += st.getInt("amount");
				priceTotal += (st.getDouble("price")*st.getInt("amount"));
			}
			
			String numCom = generateNCommande(String.valueOf(nom.charAt(0)),idClient,String.valueOf(prenom.charAt(0)),nbTotal);
			
			Font mainFont = FontFactory.getFont(FontFactory.HELVETICA, 32, Font.BOLD, new BaseColor(136, 166, 148));
			Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD, new BaseColor(136, 166, 148));
			Font tableInfoLabelFont = FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD, new BaseColor(136, 166, 148));
			Font tableInfoFont = FontFactory.getFont(FontFactory.HELVETICA, 14);
			Font tableFooterFont = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD);
			Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 7, Font.ITALIC);

			
			String path = "C:\\Users\\DEFALT\\Downloads\\" + numCom + ".pdf";
			Document doc = new Document();
			doc.setPageSize(PageSize.A4);
			
			PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(path));
			
			doc.open();
			
			Paragraph para = new Paragraph("Saxa Performance\n\n", mainFont);
			para.setAlignment(Element.ALIGN_LEFT);
			
			
			com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance("C:\\Users\\DEFALT\\Desktop\\Java\\POO\\Test\\src\\application\\iconeAplli (1).png");
			img.scalePercent(60, 60);
			img.setAlignment(Element.ALIGN_RIGHT);
			img.setAbsolutePosition(420, 710);
			
			// Tableau info du client
			
			PdfPTable infoTable = new PdfPTable(2);
			infoTable.setHorizontalAlignment(Element.ALIGN_LEFT);
			infoTable.setWidths(new int[] {60, 150});
			
			Phrase infoLabel = new Phrase("Nom du client : ", tableInfoLabelFont);
			String nameStructure = StringUtils.capitalize(nom) + " " + StringUtils.capitalize(prenom);
			Phrase info = new Phrase(nameStructure, tableInfoFont);
			
			PdfPCell infoLabelCell = new PdfPCell(infoLabel);
			PdfPCell infoCell = new PdfPCell(info);
			
			setInfoLabelStyle(infoLabelCell);
			infoTable.addCell(infoLabelCell);
			setInfoLabelStyle(infoCell);
			infoTable.addCell(infoCell);
			
			infoLabel = new Phrase("E-mail : ", tableInfoLabelFont);
			info = new Phrase(email, tableInfoFont);
			infoLabelCell = new PdfPCell(infoLabel);
			infoCell = new PdfPCell(info);
			
			setInfoLabelStyle(infoLabelCell);
			infoTable.addCell(infoLabelCell);
			setInfoLabelStyle(infoCell);
			infoTable.addCell(infoCell);
			
			infoLabel = new Phrase("Téléphone : ", tableInfoLabelFont);
			info = new Phrase(phone, tableInfoFont);
			infoLabelCell = new PdfPCell(infoLabel);
			infoCell = new PdfPCell(info);
			
			setInfoLabelStyle(infoLabelCell);
			infoTable.addCell(infoLabelCell);
			setInfoLabelStyle(infoCell);
			infoTable.addCell(infoCell);
			
			infoLabel = new Phrase("Adresse : ", tableInfoLabelFont);
			info = new Phrase(adress, tableInfoFont);
			infoLabelCell = new PdfPCell(infoLabel);
			infoCell = new PdfPCell(info);
			
			setInfoLabelStyle(infoLabelCell);
			infoTable.addCell(infoLabelCell);
			setInfoLabelStyle(infoCell);
			infoCell.setFixedHeight(50);
			infoTable.addCell(infoCell);
			
			
			// Tableau des infos de la commande
			
			PdfPTable commandeTable = new PdfPTable(2);
			commandeTable.setWidthPercentage(50);
			commandeTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
			

			Phrase infoCommande = new Phrase("Nº de commande : ", tableInfoLabelFont);
			Phrase nCommande = new Phrase(numCom, tableInfoFont);
			
			PdfPCell infoCommandeCell = new PdfPCell(infoCommande);
			PdfPCell nCommandeCell = new PdfPCell(nCommande);
			
			setInfoLabelStyle(infoCommandeCell);
			infoCommandeCell.setFixedHeight(70);
			commandeTable.addCell(infoCommandeCell);
			setInfoLabelStyle(nCommandeCell);
			nCommandeCell.setFixedHeight(70);
			commandeTable.addCell(nCommandeCell);
			
			// Tableau info du panier
			
			int[] tableWidth = {50,	220, 50, 100, 100};
			
			PdfPTable table = new PdfPTable(5);
			table.setWidths(tableWidth);
			table.setHeaderRows(1);
			
			Phrase pr = new Phrase("ID", tableHeaderFont);
			
			PdfPCell c1 = new PdfPCell(pr);
			setHeaderTableStyle(c1);
			table.addCell(c1);
			
			pr = new Phrase("Nom de la pieces" , tableHeaderFont);
			c1 = new PdfPCell(pr);
			setHeaderTableStyle(c1);
			table.addCell(c1);
			
			pr = new Phrase("NB" , tableHeaderFont);
			c1 = new PdfPCell(pr);
			setHeaderTableStyle(c1);
			table.addCell(c1);
			
			pr = new Phrase("Prix Unitaire (Dh)" , tableHeaderFont);
			c1 = new PdfPCell(pr);
			setHeaderTableStyle(c1);
			table.addCell(c1);
			
			pr = new Phrase("Prix (Dh)" , tableHeaderFont);
			c1 = new PdfPCell(pr);
			setHeaderTableStyle(c1);
			table.addCell(c1);
			
			pst = connection.prepareStatement(selectPanier);
			pst.setString(1, String.valueOf(idClient));
			st = pst.executeQuery();
			while(st.next()) {
				
				idPiece = st.getInt("piecesId");
				
				Phrase content = new Phrase(String.valueOf(idPiece), tableInfoFont);
				PdfPCell contentCell = new PdfPCell(content);
				setContentStyle(contentCell);
				table.addCell(contentCell);
				
				pst = connection.prepareStatement(selectPieces);
				pst.setString(1, String.valueOf(idPiece));
				ResultSet get = pst.executeQuery();
				get.next();
				
				content = new Phrase(get.getString("name"), tableInfoFont);
				contentCell = new PdfPCell(content);
				setContentStyle(contentCell);
				contentCell.setBackgroundColor(new BaseColor (241, 241, 241));
				table.addCell(contentCell);
				
				content = new Phrase(st.getString("amount"), tableInfoFont);
				contentCell = new PdfPCell(content);
				setContentStyle(contentCell);
				table.addCell(contentCell);
				
				content = new Phrase(st.getString("price"), tableInfoFont);
				contentCell = new PdfPCell(content);
				setContentStyle(contentCell);
				contentCell.setBackgroundColor(new BaseColor (241, 241, 241));
				table.addCell(contentCell);
				
				double priceT = st.getDouble("price")*st.getDouble("amount");
				
				content = new Phrase(String.valueOf(priceT), tableInfoFont);
				contentCell = new PdfPCell(content);
				setContentStyle(contentCell);
				contentCell.setBackgroundColor(new BaseColor (241, 241, 241));
				table.addCell(contentCell);
				
				String pieceHolder = idPiece + "/" + st.getInt("amount");
				piecesArray.add(pieceHolder);
			}
		
			PdfPTable footerTable = new PdfPTable(2);
			footerTable.setWidthPercentage(50);
			footerTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
			
			Phrase footer = new Phrase("Prix totale TTC : ", tableInfoLabelFont); 
			PdfPCell footerCell = new PdfPCell(footer);
			
			setInfoLabelStyle(footerCell);
			footerCell.setFixedHeight(100);
			footerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			footerTable.addCell(footerCell);
			
			footer = new Phrase(String.valueOf(priceTotal) + " Dhs", tableFooterFont);
			footerCell = new PdfPCell(footer);
			setInfoLabelStyle(footerCell);
			footerCell.setFixedHeight(100);
			footerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			footerTable.addCell(footerCell);
			
			Paragraph paraFooter = new Paragraph("Merci " + nom.toUpperCase() + " " + prenom.toUpperCase() + " pour votre commande", footerFont);
			Paragraph paraFooter2 = new Paragraph("Priere d'attendre que vous votre commande vois soit livree dans un delais de 10 jours ouverts", footerFont);
			Paragraph paraFooter3 = new Paragraph("Pour toute demande ou information veuillez nous contacter par :", footerFont);
			Paragraph paraFooter4 = new Paragraph("Email : demande@saxaPerformance.com", footerFont);
			Paragraph paraFooter5 = new Paragraph("Tel : 0522865715", footerFont);
			
			PdfContentByte cb = writer.getDirectContentUnder();
			ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
					paraFooter, (doc.right() - doc.left()) / 2 + doc.leftMargin(),doc.bottom()+20, 0);
			ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
					paraFooter2, (doc.right() - doc.left()) / 2 + doc.leftMargin(),doc.bottom()+10, 0);
			ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
					paraFooter3, (doc.right() - doc.left()) / 2 + doc.leftMargin(),doc.bottom(), 0);
			ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
					paraFooter4, (doc.right() - doc.left()) / 2 + doc.leftMargin(),doc.bottom()-10, 0);
			ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
					paraFooter5, (doc.right() - doc.left()) / 2 + doc.leftMargin(),doc.bottom()-20, 0);
			
			doc.add(para);
			doc.add(img);
			doc.add(infoTable);
			doc.add(commandeTable);
			doc.add(table);
			doc.add(footerTable);
			
			
			doc.close();
			
			pst = connection.prepareStatement(insert);
			pst.setString(1, numCom);
			pst.setLong(2, idClient);
			pst.setLong(3, nbTotal);
			pst.setDouble(4, priceTotal);
			pst.setString(5, String.join("-", piecesArray));
			pst.execute();
				
			pst = connection.prepareStatement(delete);
			pst.setString(1, String.valueOf(idClient));
			pst.execute();
			
			DevisControler.getInstance().refreshPage(e);
		
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		CustomAlert alert = new CustomAlert(AlertType.CONFIRMATION, "Merci d'avoir confirmé la commande\nVotre reçu a été téléchargé");
		alert.showAndWait();
		
		cancelButton.getScene().getWindow().hide();
	}
	
	public static void setHeaderTableStyle(PdfPCell c1) {
		c1.setVerticalAlignment(Element.ALIGN_TOP);
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setFixedHeight(50);
		c1.setBorderWidth(0);
		c1.setBorderWidthBottom(3);
		c1.setBorderWidthRight(3);
		c1.setBorderColor(new BaseColor(136, 166, 148));
	}
	
	public static void setInfoLabelStyle(PdfPCell c1) {
		c1.setVerticalAlignment(Element.ALIGN_TOP);
		c1.setFixedHeight(30);
		c1.setBorderWidth(0);
		c1.setBorderColor(new BaseColor(136, 166, 148));
	}
	
	public static void setContentStyle(PdfPCell c1) {
		c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setFixedHeight(35);
		c1.setBorderWidth(0);
		c1.setBorderWidthBottom((float) 1.5);
		c1.setBorderColor(new BaseColor(18, 166, 148));
	}
	
	public String generateNCommande(String first, int id, String second, int nb) {
		connection = handler.getConnection();
		String comNum = first.toUpperCase() + id + second.toUpperCase() + nb;
		String check = "SELECT * FROM commande WHERE numCom=?";
		int count = 0, i = 0;
		
		try {
			do {
				pst = connection.prepareStatement(check);
				pst.setString(1, comNum);
				ResultSet rs = pst.executeQuery();
				while(rs.next()) {
					count ++;
				}
				if(count == 0) {
					break;
				} else {
					i++;
					comNum = first.toUpperCase() + id + second.toUpperCase() + nb + String.valueOf(i);
					count = 0;
				}
			} while (count == 0);
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return comNum;
	}
	
	public void cancel(ActionEvent e) {
		cancelButton.getScene().getWindow().hide();
	}
	
	public void displayInfo(String nom, String prenom, String email, int age, String phone, String adress, String admin) {
		nameLabel.setText(nom.toUpperCase() + " " + prenom.toUpperCase());
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
		this.age = age;
		this.phone = phone;
		this.adress = adress;
		this.admin = admin;
	}

}
