package application;

public class Client {
	
	int id;
	String nom,prenom,username,email,phone,adresse;
	int age;
	
	public Client(int id, String nom, String prenom, String username, String email, String phone, String adresse, int age) {
		this.id = id;
		this.nom = nom;
		this.prenom = prenom;
		this.username = username;
		this.email = email;
		this.phone = phone;
		this.adresse = adresse;
		this.age = age;
	}
	
	public Client(Client client) {
		this.id = client.id;
		this.nom = client.nom;
		this.prenom = client.prenom;
		this.username = client.username;
		this.email = client.email;
		this.phone = client.phone;
		this.adresse = client.adresse;
		this.age = client.age;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
}
