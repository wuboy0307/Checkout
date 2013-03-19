package topay.mobi.pos.vo;

public class Checkin extends ClientDetail {
	private Client client;
	private String date;

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
