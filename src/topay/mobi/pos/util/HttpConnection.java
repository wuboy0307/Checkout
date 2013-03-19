package topay.mobi.pos.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import android.os.Handler;
import android.os.Message;

/**
 * Asynchronous HTTP connections
 * 
 * @author Sato
 */
public class HttpConnection implements Runnable {

	public static final int DID_START=0;
	public static final int DID_ERROR=1;
	public static final int DID_SUCCEED=2;

	private static final int GET=0;
	private static final int POST=1;
	private static final int PUT=2;
	private static final int DELETE=3;

	private String url;
	private int method;
	private Handler handler;
	private String data;
	private List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();

	private HttpClient httpClient;

	public HttpConnection() {
		this(new Handler());
	}

	public HttpConnection(Handler _handler) {
		handler=_handler;
	}

	public void create(int method, String url, String data) {
		this.method=method;
		this.url=url;
		this.data=data;
		ConnectionManager.getInstance().push(this);
	}

	public void get(String url) {
		create(GET, url, null);
	}

	public void post(String url, String data) {
		create(POST, url, data);
	}

	public void post(String url, List<NameValuePair> nameValuePairs) {
		this.nameValuePairs=nameValuePairs;
		create(POST, url, null);
	}

	public void put(String url, String data) {
		create(PUT, url, data);
	}

	public void delete(String url) {
		create(DELETE, url, null);
	}

	public void run() {
		handler.sendMessage(Message.obtain(handler, HttpConnection.DID_START));
		httpClient=new DefaultHttpClient();
		HttpConnectionParams.setSoTimeout(httpClient.getParams(), 25000);
		try {
			HttpResponse response=null;
			switch (method) {
			case GET:
				response=httpClient.execute(new HttpGet(url));
				break;
			case POST:
				HttpPost httpPost=new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				response=httpClient.execute(httpPost);
				break;
			case PUT:
				HttpPut httpPut=new HttpPut(url);
				httpPut.setEntity(new StringEntity(data));
				response=httpClient.execute(httpPut);
				break;
			case DELETE:
				response=httpClient.execute(new HttpDelete(url));
				break;

			}

			BufferedReader br=new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "ISO-8859-1"));

			Message message=Message.obtain(handler, DID_SUCCEED, br);
			handler.sendMessage(message);

		} catch (Exception e) {
			handler.sendMessage(Message.obtain(handler, HttpConnection.DID_ERROR, e));
		}
		ConnectionManager.getInstance().didComplete(this);
	}

}
