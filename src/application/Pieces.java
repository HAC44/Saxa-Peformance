package application;

public class Pieces {
	
	String name, description,image,catId;
	int id,stock,panierId;
	double price;
	
	public Pieces(String name, String description, int id, String catId, int stock, double price, String image) {
		super();
		this.name = name;
		this.description = description;
		this.id = id;
		this.catId = catId;
		this.stock = stock;
		this.price = price;
		this.image = image;
	}
	
	public Pieces(String name, String description, int id, String catId, int stock, double price) {
		super();
		this.name = name;
		this.description = description;
		this.id = id;
		this.catId = catId;
		this.stock = stock;
		this.price = price;
	}
	
	public Pieces(int panierId,String name, int id, int stock, double price) {
		this.panierId = panierId;
		this.name = name;
		this.id = id;
		this.stock = stock;
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCatId() {
		return catId;
	}

	public void setCatId(String catId) {
		this.catId = catId;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getPanierId() {
		return panierId;
	}

	public void setPanierId(int panierId) {
		this.panierId = panierId;
	}
	
	
	
	

}
