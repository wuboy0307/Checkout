package topay.mobi.pos.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import topay.mobi.pos.facebook.Utility;
import topay.mobi.pos.util.Constants;
import topay.mobi.pos.util.HttpConnection;
import android.os.Handler;

public class BematechServices {

	public void listCheckins(String estabelecimento, Handler handler) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("eid", estabelecimento));
		new HttpConnection(handler).post(Constants.LISTAR_CHECKINS_URL, nameValuePairs);
	}

	public void listCheckouts(String estabelecimento, Handler handler) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("eid", estabelecimento));
		new HttpConnection(handler).post(Constants.LISTAR_CHECKOUTS_URL, nameValuePairs);
	}

	public void enviaDetalhesPagamento(String amount, String paymentId, Handler handler) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("amount", amount));
		nameValuePairs.add(new BasicNameValuePair("paymentId", paymentId));
		new HttpConnection(handler).post(Constants.ENVIA_DETALHES_PAGAMENTO, nameValuePairs);
	}

	public void detalhesClienteExperiencia(String email, String estabelecimento, Handler handler) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("email", email));
		nameValuePairs.add(new BasicNameValuePair("eid", estabelecimento));
		new HttpConnection(handler).post(Constants.DETALHES_CHECKIN_URL, nameValuePairs);
	}
	
	public void enviaClientNumber(String experienciaId, String number, Handler handler) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("experienciaId", experienciaId));
		nameValuePairs.add(new BasicNameValuePair("number", number));
		new HttpConnection(handler).post(Constants.ENVIA_CLIENT_NUMBER, nameValuePairs);
	}
	
	public void removeClientePayment(String status, String paymentId, Handler handler) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("status", status));
		nameValuePairs.add(new BasicNameValuePair("paymentid", paymentId));		
		new HttpConnection(handler).post(Constants.REMOVE_CLIENTE_PAYMENT, nameValuePairs);
	}
	
	public void callfacebook(String idFacebook, Handler handler) {
//		new HttpConnection(handler).post("http://graph.facebook.com/"+idFacebook+"/picture?type=large", "");
		
		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>(4);
		pairs.add(new BasicNameValuePair("access_token", Utility.APP_ID));
		new HttpConnection(handler).post("http://graph.facebook.com/"+idFacebook+"?fields=picture", "");		
	}

}
