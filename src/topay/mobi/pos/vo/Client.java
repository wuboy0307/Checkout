package topay.mobi.pos.vo;

public class Client {

	private int clientId;
	private String email;
	private String name;
	private String cidade;
	private String estado;
	private String nascimento;
	private String facebookId;
	private String fone;
	private String payId;
	private String totalVisitas;
	private String totalGasto;
	private String experienciaId;
	private String mesa;

	public Client() {
	}

	public Client(int clientId, String email, String name, String fone, String cidade, String estado, String nascimento, String facebookId, String totalVisitas,
	String totalGasto, String experienciaId, String mesa) {
		this.clientId = clientId;
		this.email = email;
		this.name = name;
		this.cidade = cidade;
		this.estado = estado;
		this.nascimento = nascimento;
		this.facebookId = facebookId;
		this.fone = fone;
		this.totalVisitas = totalVisitas;
		this.totalGasto = totalGasto;
		this.experienciaId =experienciaId;
		this.mesa = mesa;
	}

	public String getMesa() {
		return mesa;
	}

	public void setMesa(String mesa) {
		this.mesa = mesa;
	}

	public String getExperienciaId() {
		return experienciaId;
	}

	public void setExperienciaId(String experienciaId) {
		this.experienciaId = experienciaId;
	}

	public String getFone() {
		return fone;
	}

	public void setFone(String fone) {
		this.fone = fone;
	}

	public int getClientId() {
		return this.clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCidade() {
		return this.cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getFacebookId() {
		return this.facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public String getNascimento() {
		return this.nascimento;
	}

	public void setNascimento(String nascimento) {
		this.nascimento = nascimento;
	}
	
	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getTotalVisitas() {
		return totalVisitas;
	}

	public void setTotalVisitas(String totalVisitas) {
		this.totalVisitas = totalVisitas;
	}

	public String getTotalGasto() {
		return totalGasto;
	}

	public void setTotalGasto(String totalGasto) {
		this.totalGasto = totalGasto;
	}	

}
