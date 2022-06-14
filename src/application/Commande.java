package application;

public class Commande {
	
	String nom, numCom, pieces, order;
	int id, idClient, nbPieces;
	double price;
	
	public Commande(int id, int idClient, String nom, String numCom, String pieces, int nbPieces, double price, String order) {
		super();
		this.id = id;
		this.idClient = idClient;
		this.nom = nom;
		this.numCom = numCom;
		this.pieces = pieces;
		this.nbPieces = nbPieces;
		this.price = price;
		this.order = order;
	}
	
	public Commande(int id, int idClient, String pieces, int nbPieces, double price) {
		this.id = id;
		this.idClient = idClient;
		this.pieces = pieces;
		this.nbPieces = nbPieces;
		this.price = price;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getNumCom() {
		return numCom;
	}

	public void setNumCom(String numCom) {
		this.numCom = numCom;
	}

	public String getPieces() {
		return pieces;
	}

	public void setPieces(String pieces) {
		this.pieces = pieces;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdClient() {
		return idClient;
	}

	public void setIdClient(int idClient) {
		this.idClient = idClient;
	}

	public int getNbPieces() {
		return nbPieces;
	}

	public void setNbPieces(int nbPieces) {
		this.nbPieces = nbPieces;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
	
	
	
}
