package topay.mobi.pos.vo;

public class Payment {
	private String paymentId;
	private String paykey;
	private String transactionId;
	private String acquirer;
	private String customerreceipt;
	private String authorizerid;
	private String authorizationnumber;
	private String amount;

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getPaykey() {
		return paykey;
	}

	public void setPaykey(String paykey) {
		this.paykey = paykey;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getCustomerreceipt() {
		return customerreceipt;
	}

	public void setCustomerreceipt(String customerreceipt) {
		this.customerreceipt = customerreceipt;
	}

	public String getAuthorizerid() {
		return authorizerid;
	}

	public void setAuthorizerid(String authorizerid) {
		this.authorizerid = authorizerid;
	}

	public String getAuthorizationnumber() {
		return authorizationnumber;
	}

	public void setAuthorizationnumber(String authorizationnumber) {
		this.authorizationnumber = authorizationnumber;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();

		b.append(this.getPaymentId());
		b.append("\n");
		b.append(this.getPaykey());
		b.append("\n");
		b.append(this.getTransactionId());
		b.append("\n");
		b.append(this.getAcquirer());
		b.append("\n");
		b.append(this.getAmount());
		b.append("\n");
		b.append(this.getCustomerreceipt());
		b.append("\n");
		b.append(this.getAuthorizerid());
		b.append("\n");
		b.append(this.getAuthorizationnumber());
		b.append("\n");

		return b.toString();
	}
}
