package topay.mobi.pos;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import topay.mobi.pos.controller.BematechServices;
import topay.mobi.pos.util.Constants;
import topay.mobi.pos.util.HttpConnection;
import topay.mobi.pos.util.Moeda;
import topay.mobi.pos.util.SessionUtil;
import topay.mobi.pos.vo.Checkin;
import topay.mobi.pos.vo.Checkout;
import topay.mobi.pos.vo.Client;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ClientDetailsFragment extends SherlockFragment implements OnClickListener {
	private GoogleAnalyticsTracker tracker;
	private Checkout checkout;
	private Checkin checkin;
	private BematechServices bms;
	private AlertDialog.Builder mensagem;
	private ProgressDialog pDialog;
	
	Moeda moeda = new Moeda(); 
	SessionUtil sessionUtil;
	String mesa, clientid, experienciaId;
	
	public static int dif = 0;
	
	public static ClientDetailsFragment instance;

	public ClientDetailsFragment() {

	}

	public ClientDetailsFragment(Checkin checkin, Checkout checkout) {
		this.checkin = checkin;
		this.checkout = checkout;		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		tracker = GoogleAnalyticsTracker.getInstance();
		bms = new BematechServices();
		mensagem = new AlertDialog.Builder(getActivity());
		sessionUtil = SessionUtil.getInstance(getActivity());
	}

	View myView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (checkout == null) {
			myView = inflater.inflate(R.layout.client_details, container, false);
		}else{
			myView = inflater.inflate(R.layout.pagamento_detalils, container, false);
		}
		
		TextView nomeTxt = (TextView) myView.findViewById(R.id.nameTextView);
		TextView valorGastTxt = (TextView) myView.findViewById(R.id.valorGastoTextView);
		TextView totalVisitas = (TextView) myView.findViewById(R.id.totalVisitasTextView);
		
    	Button ok = (Button) myView.findViewById(R.id.bt_ok);
    	Button cancelar = (Button) myView.findViewById(R.id.bt_cancel);
    	Button removerButton = (Button) myView.findViewById(R.id.removerButton);
		Button enviarButton = (Button) myView.findViewById(R.id.envidarButton);
		
		LinearLayout valorLayout = (LinearLayout) myView.findViewById(R.id.valorLayout);
		LinearLayout checkoutLayout = (LinearLayout) myView.findViewById(R.id.checkoutLayout);
		Client client = null;
		
		

		if (checkout == null) {

			client = checkin.getClient();

			String cen = null, dez = null;		
			String valor2 = checkin.getClient().getTotalGasto().replace(".0", "");
			
			if(checkin.getClient().getTotalGasto().equalsIgnoreCase("0.0")){
				cen = "0"; 
				dez = "0";	
			}
			
			System.out.println("2 TESTE: "+valor2.length());
					
			for (int i = 0; i < valor2.length(); i++) {
				if(i>=(valor2.length()-2)){
					System.out.println("CEN: "+valor2.charAt(i));
					if(cen!=null){
						cen = cen + valor2.charAt(i);
					}else{
						cen = ""+valor2.charAt(i);
					}
				}else{
					System.out.println("DEZ: "+valor2.charAt(i));
					if(dez!=null){
						dez = dez + valor2.charAt(i);
					}else{
						dez = ""+valor2.charAt(i);
					}
				}			
			}		
			System.out.println("TOTAL: R$ "+dez+","+cen);			
			valorGastTxt.setText("Total Gasto "+"R$" +dez+","+cen);
			
			
			totalVisitas.setText("Visitas: "+checkin.getTotalVisitas());
			
			enviarButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					EditText valor = (EditText) myView.findViewById(R.id.valorEditText);
					mesa = valor.getText().toString();
					clientid = String.valueOf(checkin.getClient().getClientId());
					experienciaId = checkin.getClient().getExperienciaId();
					System.out.println("TESTE ENVIA NUMERO: "+mesa);
					bms.enviaClientNumber(experienciaId, mesa, handler_mesa);
				}
			});
			
			removerButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					System.out.println("STATUS: "+"DNR");
					System.out.println("PAYMENT ID: "+checkin.getClient().getPayId());
					
					dif = 1;
							
					bms.removeClientePayment("DNR", checkin.getClient().getPayId(), handler_mesa);
				}
			});
			
			
			ImageView faceImageView = (ImageView) myView.findViewById(R.id.clientImageView);
			String url = "http://graph.facebook.com/" + checkin.getClient().getFacebookId() + "/picture?type=normal";
			ImageLoader.getInstance().displayImage(url, faceImageView);


		} else {			
			client = checkout.getClient();
			
			System.out.println("######################### data: "+checkout.getDate());

			String cen = null, dez = null;		
			String valor2 = checkout.getClient().getTotalGasto().replace(".0", "");
			
			if(checkout.getClient().getTotalGasto().equalsIgnoreCase("0.0")){
				cen = "0"; 
				dez = "0";	
			}
			
			System.out.println("2 TESTE: "+valor2.length());
					
			for (int i = 0; i < valor2.length(); i++) {
				if(i>=(valor2.length()-2)){
					System.out.println("CEN: "+valor2.charAt(i));
					if(cen!=null){
						cen = cen + valor2.charAt(i);
					}else{
						cen = ""+valor2.charAt(i);
					}
				}else{
					System.out.println("DEZ: "+valor2.charAt(i));
					if(dez!=null){
						dez = dez + valor2.charAt(i);
					}else{
						dez = ""+valor2.charAt(i);
					}
				}			
			}		
			System.out.println("TOTAL: R$ "+dez+","+cen);			
			valorGastTxt.setText("Total Gasto "+"R$" +dez+","+cen);
			
			
			totalVisitas.setText(checkout.getTotalVisitas());
			
			
			System.out.println("######################### data: "+checkout.getDate());
			totalVisitas.setText(checkout.getTotalVisitas());
			System.out.println("######################### visitas: "+checkout.getTotalVisitas());

			checkoutLayout.setVisibility(View.VISIBLE);
			valorLayout.setVisibility(View.VISIBLE);
			
	    	ok.setOnClickListener(new View.OnClickListener() {
	    		public void onClick(View v) {
	    			System.out.println();
	    			
	    			mensagem = new AlertDialog.Builder(getActivity());
	    			bms = new BematechServices();			
	    			bms.enviaDetalhesPagamento("2225", checkout.getPayment().getPaymentId(), handlerPay);    			
	    		}
	    	});

	    	cancelar.setOnClickListener(new View.OnClickListener() {
	    		public void onClick(View v) {
					FragmentTransaction ft = getFragmentManager().beginTransaction();
					ft.remove(ClientDetailsFragment.instance);
					ft.replace(R.id.root, new CheckoutFragment(MainActivity.checkoutList), "checkout");
					ft.commit();
					ClientDetailsFragment.instance = null;
	    		}
	    	});
	    	
			removerButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					System.out.println("STATUS: "+"DNR");
					System.out.println("PAYMENT ID: "+checkout.getClient().getPayId());
					dif = 2;
					bms.removeClientePayment("DNR", checkout.getClient().getPayId(), handler_mesa);
				}
			});
						
			ImageView faceImageView = (ImageView) myView.findViewById(R.id.clientImageView);
			String url = "http://graph.facebook.com/" + checkout.getClient().getFacebookId() + "/picture?type=normal";
			ImageLoader.getInstance().displayImage(url, faceImageView);

		}

		nomeTxt.setText(client.getName());

		return myView;
	}
	
	public Bitmap StringToBitMap(String encodedString) {
		try {
			byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,encodeByte.length);
			return bitmap;
		} catch (Exception e) {
			e.getMessage();
			return null;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		tracker.startNewSession(getString(R.string.google_analytics_ua_account), Integer.parseInt(getString(R.string.google_analytics_timeout)), getActivity());
		tracker.trackPageView(getString(R.string.google_analytics_trackpage_envia_detalhes_pagamento));
		tracker.dispatch();
	}

	@Override
	public void onPause() {
		super.onPause();
		tracker.stopSession();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		tracker.stopSession();
	}

	@Override
	public void onClick(View v) {
//		EditText valor = (EditText) myView.findViewById(R.id.valorEditText);
//		bms.enviaDetalhesPagamento(valor.getText().toString(), checkout.getPayment().getPaymentId(), handler);
	}
	
	Handler handler_mesa = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
				case HttpConnection.DID_START: {
					pDialog = new ProgressDialog(getActivity());
					pDialog.setMessage(getActivity().getString(R.string.detalhes_client_loading));
					pDialog.setIndeterminate(false);
					pDialog.setCancelable(false);
					pDialog.show();
					Log.d(CheckinFragment.class.toString(), "Starting connection...");
					break;
				}
				case HttpConnection.DID_SUCCEED: {

					Object obj = message.obj;

					try {

						String result = ((BufferedReader) obj).readLine();

						System.out.println("RESULT ? " + result);

						if ("OK".equalsIgnoreCase(result)) {
							pDialog.dismiss();
				
							
							if(dif==0){
								for (int i = 0; i < MainActivity.checkinList.size(); i++) {
									System.out.println("MESA: "+mesa+"COMPARA MESA: "+MainActivity.checkinList.get(i).getClient().getMesa());
									if(checkin.getClient().getExperienciaId().equalsIgnoreCase(MainActivity.checkinList.get(i).getClient().getExperienciaId())){
																	
										MainActivity.checkinList.get(i).getClient().setMesa(mesa);
										FragmentTransaction ft = getFragmentManager().beginTransaction();
										ft.remove(ClientDetailsFragment.instance);
										ft.replace(R.id.root, new CheckinFragment(MainActivity.checkinList), "checkin");
										ft.commit();
										ClientDetailsFragment.instance = null;
									}
								}
							}else{
								FragmentTransaction ft = getFragmentManager().beginTransaction();
								ft.remove(ClientDetailsFragment.instance);
								
								if(dif==1){
									MainActivity.cont_in = 0;
									MainActivity.checkinList.clear();
									System.out.println("GGGGGGGGGGGGGGG: "+MainActivity.checkinList.size());
									ft.replace(R.id.root, new CheckinFragment(MainActivity.checkinList), "checkin");
								}else{
									MainActivity.cont_out = 0;
									MainActivity.checkoutList.clear();
									System.out.println("GGGGGGGGGGGGGGG: "+MainActivity.checkoutList.size());
									ft.replace(R.id.root, new CheckoutFragment(MainActivity.checkoutList), "checkout");
								}
								
								ft.commit();
								ClientDetailsFragment.instance = null;
							}

							


						} else {
							pDialog.dismiss();
							mensagem.setTitle(getActivity().getString(R.string.title_erro_client_details));
							mensagem.setMessage(getActivity().getString(R.string.msg_erro_client_details));
							mensagem.setNeutralButton("OK", null);
							mensagem.show();
						}

					} catch (IOException e) {
						e.printStackTrace();
					}

					break;
				}
				case HttpConnection.DID_ERROR: {
					pDialog.dismiss();
					mensagem.setTitle(getActivity().getString(R.string.title_erro_client_details));
					mensagem.setMessage(getActivity().getString(R.string.msg_erro_client_details));
					mensagem.setNeutralButton("OK", null);
					mensagem.show();

					Exception e = (Exception) message.obj;
					e.printStackTrace();
					Log.d(CheckinFragment.class.toString(), "Connection failed. " + e.getMessage());
					break;
				}
			}
		}
	};
	
	Handler handlerPay = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
				case HttpConnection.DID_START: {
					pDialog = new ProgressDialog(getActivity());
					pDialog.setMessage(getActivity().getString(R.string.detalhes_client_loading));
					pDialog.setIndeterminate(false);
					pDialog.setCancelable(false);
					pDialog.show();
					Log.d(CheckinFragment.class.toString(), "Starting connection...");
					break;
				}
				case HttpConnection.DID_SUCCEED: {

					Object obj = message.obj;

					try {

						String result = ((BufferedReader) obj).readLine();

						System.out.println("RESULT ? " + result);

						if ("OK".equalsIgnoreCase(result)) {
							pDialog.dismiss();
							System.out.println("SUUUUUCESSOOOOOOOO!!!");
//							showCustomDialog();
							FragmentTransaction ft = getFragmentManager().beginTransaction();
							ft.remove(ClientDetailsFragment.instance);
							ft.replace(R.id.root, new CheckoutFragment(MainActivity.checkoutList), "checkout");
							ft.commit();
							ClientDetailsFragment.instance = null;
							
								
						} else {
							pDialog.dismiss();
							mensagem.setTitle(getActivity().getString(R.string.title_erro_client_details));
							mensagem.setMessage(getActivity().getString(R.string.msg_erro_client_details));
							mensagem.setNeutralButton("OK", null);
							mensagem.show();
						}

					} catch (IOException e) {
						e.printStackTrace();
					}

					break;
				}
				case HttpConnection.DID_ERROR: {
					pDialog.dismiss();
					mensagem.setTitle(getActivity().getString(R.string.title_erro_client_details));
					mensagem.setMessage(getActivity().getString(R.string.msg_erro_client_details));
					mensagem.setNeutralButton("OK", null);
					mensagem.show();

					Exception e = (Exception) message.obj;
					e.printStackTrace();
					Log.d(CheckinFragment.class.toString(), "Connection failed. " + e.getMessage());
					break;
				}
			}
		}
	};

	Handler handler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
				case HttpConnection.DID_START: {
					pDialog = new ProgressDialog(getActivity());
					pDialog.setMessage(getActivity().getString(R.string.detalhes_client_loading));
					pDialog.setIndeterminate(false);
					pDialog.setCancelable(false);
					pDialog.show();
					Log.d(CheckinFragment.class.toString(), "Starting connection...");
					break;
				}
				case HttpConnection.DID_SUCCEED: {

					Object obj = message.obj;

					try {

						String result = ((BufferedReader) obj).readLine();

						System.out.println("RESULT ? " + result);

						if ("OK".equalsIgnoreCase(result)) {
							pDialog.dismiss();
							FragmentTransaction ft = getFragmentManager().beginTransaction();
							ft.remove(ClientDetailsFragment.instance);
							ft.replace(R.id.root, new CheckoutFragment(MainActivity.checkoutList), "checkout");
							ft.commit();
							ClientDetailsFragment.instance = null;
						} else {
							pDialog.dismiss();
							mensagem.setTitle(getActivity().getString(R.string.title_erro_client_details));
							mensagem.setMessage(getActivity().getString(R.string.msg_erro_client_details));
							mensagem.setNeutralButton("OK", null);
							mensagem.show();
						}

					} catch (IOException e) {
						e.printStackTrace();
					}

					break;
				}
				case HttpConnection.DID_ERROR: {
					pDialog.dismiss();
					mensagem.setTitle(getActivity().getString(R.string.title_erro_client_details));
					mensagem.setMessage(getActivity().getString(R.string.msg_erro_client_details));
					mensagem.setNeutralButton("OK", null);
					mensagem.show();

					Exception e = (Exception) message.obj;
					e.printStackTrace();
					Log.d(CheckinFragment.class.toString(), "Connection failed. " + e.getMessage());
					break;
				}
			}
		}
	};

}
