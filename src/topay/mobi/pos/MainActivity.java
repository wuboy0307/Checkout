package topay.mobi.pos;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import topay.mobi.pos.controller.BematechServices;
import topay.mobi.pos.util.Constants;
import topay.mobi.pos.util.HttpConnection;
import topay.mobi.pos.util.SessionUtil;
import topay.mobi.pos.vo.Checkin;
import topay.mobi.pos.vo.Checkout;
import topay.mobi.pos.vo.Client;
import topay.mobi.pos.vo.Payment;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends SherlockFragmentActivity implements ActionBar.TabListener {

	public static ActionBar ab;
	public String result;
	private String total_checkin = "0", total_checkout = "0";
	private int cont_checkin = 0 , cont_checkout = 0, position = 0;
	public Object obj;
	private Tab checkinTab;
	private Tab checkoutTab;
	private BematechServices bms;
	private ProgressDialog pDialog;
	
	public static int cont_in = 0, cont_out = 0;
	
	public static int first_acess;
	
	public static List<Checkout> checkoutList = new ArrayList<Checkout>();		
	public static List<Checkin> checkinList = new ArrayList<Checkin>();
	
	public static List<Checkout> last_checkoutList = new ArrayList<Checkout>();
	static SherlockFragmentActivity main;


	boolean close = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 // Create global configuration and initialize ImageLoader with this configuration
		
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
		.cacheInMemory()
		.cacheOnDisc()
		.build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
		.defaultDisplayImageOptions(defaultOptions)
		.build();

		ImageLoader.getInstance().init(config);

           
		setContentView(R.layout.activity_main);
		bms = new BematechServices();
		MainActivity.main = this;
		pDialog = new ProgressDialog(this);
//		pDialog.setMessage(tab.getPosition() == 0 ? this.getString(R.string.detalhes_client_loading) : this.getString(R.string.detalhes_client_loading2));
		pDialog.setMessage("Aguardando checkin...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
		
		first_acess = 0;
		SessionUtil.getInstance(getApplicationContext()).setMesa("");
		SessionUtil.getInstance(getApplicationContext()).commit();
		
		bms.listCheckins(Constants.ESTABELECIMENTO_ID, checkinHandler);
		
				
		tabs();
	}

	public void tabs() {
		ab = getSupportActionBar();
		ab.removeAllTabs();
		ab.setTitle(getString(R.string.app_name));

		ab.setBackgroundDrawable(getResources().getDrawable(R.color.color_text));
		ab.setStackedBackgroundDrawable(getResources().getDrawable(R.drawable.tabs_background));
				
		ab.setLogo(getResources().getDrawable(R.drawable.lg_bematech));

		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		checkinTab = ab.newTab();		
		checkinTab.setTabListener(this);
//		checkinTab.setIcon(R.drawable.checkin);
		checkinTab.setText(getString(R.string.checkins) + " (00)");
		

		
		checkoutTab = ab.newTab();
		checkoutTab.setTabListener(this);
//		checkoutTab.setIcon(R.drawable.checkout);
		checkoutTab.setText(getString(R.string.checkouts) + " (00)");
		ab.addTab(checkinTab);
		ab.addTab(checkoutTab);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		ft = getSupportFragmentManager().beginTransaction();

		System.out.println("###################: "+tab.getPosition());
		
		position = tab.getPosition();

		if (tab.getPosition() == 0) {
			showCheckins();
		}else {
			showCheckouts();
		}
	}

	public void showCheckins() {		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.root, new CheckinFragment(checkinList));
		ft.commitAllowingStateLoss();
	}

	public void showCheckouts() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.root, new CheckoutFragment(checkoutList));
		ft.commitAllowingStateLoss();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// No call for super(). Bug on API Level > 11.
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}

	public void closeApp() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(true);
		builder.setTitle(getString(R.string.txt_title_sair));
		builder.setMessage(getString(R.string.txt_sairapp));
		builder.setPositiveButton(getString(R.string.txt_sim), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				close = true;
				dialog.dismiss();
				closeContextMenu();
				finish();
				return;
			}
		});
		builder.setNegativeButton(getString(R.string.txt_nao), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			int stackCount = getSupportFragmentManager().getBackStackEntryCount();
			
			System.out.println("++++++++++++ getBackStackEntryCount = "+stackCount);
			System.out.println("++++++++++++ ClientDetailsFragment = "+ClientDetailsFragment.instance);

			if (stackCount == 0 && ClientDetailsFragment.instance == null) {
				closeApp();

			} else if (ClientDetailsFragment.instance != null) {
				if(ClientDetailsFragment.instance.getTag().equalsIgnoreCase("datail_checkout")){
					ft.remove(ClientDetailsFragment.instance);
					ft.replace(R.id.root, new CheckoutFragment(checkoutList), "checkout");
					ft.commit();
					ClientDetailsFragment.instance = null;
				}else{
					ft.remove(ClientDetailsFragment.instance);
					ft.replace(R.id.root, new CheckinFragment(checkinList), "checkin");
					ft.commit();
					ClientDetailsFragment.instance = null;
				}

			}else
				getSupportFragmentManager().popBackStack();

		}
		return false;
	}

	private class ProcessaCheckins extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Void... params) {
			BufferedReader br = (BufferedReader) obj;
			checkinList = new ArrayList<Checkin>();
			int list = Integer.parseInt(total_checkin);
			for (int i = 0; i < list; i++) {

				Client client = new Client();

				try {
					
					String id = br.readLine();
					client.setClientId(Integer.parseInt(id));
					client.setFacebookId(br.readLine());
					client.setEmail(br.readLine());
					client.setNascimento(br.readLine().toString());
					client.setName(br.readLine());
					client.setExperienciaId(br.readLine());
					String checkinDate = br.readLine();

					client.setPayId(br.readLine());
					client.setTotalVisitas(br.readLine());
					client.setTotalGasto(br.readLine());
					client.setMesa(br.readLine());
					
					Checkin checkin = new Checkin();
					checkin.setClient(client);
					checkin.setDate(checkinDate);
					
					Log.d(MainActivity.class.toString(), client.toString());
										
					checkinList.add(checkin);

				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			
			for (Checkin in :  checkinList) {
				System.out.println("NOME ORDER: "+in.getClient().getName());
			}

			return null;
		}
 
		@Override
		protected void onPostExecute(Void result) {
			
			Collections.sort(checkinList, new Comparator<Checkin>() {
				@Override
				public int compare(Checkin c1, Checkin c2) {										
					return c1.getDate().compareTo(c2.getDate());
				}
			});
			
			pDialog.dismiss();
		}
	}

	Handler checkinHandler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
				case HttpConnection.DID_START: {

					Log.d(CheckinFragment.class.toString(), "Starting connection...");
					break;
				}
				case HttpConnection.DID_SUCCEED: {
					System.out.println("Lista de checkins...");
					
					obj = message.obj;

					try {

						result = ((BufferedReader) obj).readLine();
						total_checkin = ((BufferedReader) obj).readLine();
						if(total_checkin != null){
							if (total_checkin.length() < 2)
								total_checkin = "0" + total_checkin;
							
							checkinTab.setText(getString(R.string.checkins) + " (" + total_checkin + ")");
						}else{
							checkinTab.setText(getString(R.string.checkins) + " (00)");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

					if ("OK".equalsIgnoreCase(result) && !"0".equalsIgnoreCase(total_checkin))
						new ProcessaCheckins().execute();

					bms.listCheckouts(Constants.ESTABELECIMENTO_ID, checkoutHandler);
					break;
				}
				case HttpConnection.DID_ERROR: {
					pDialog.dismiss();
					//TODO Verificar o erro.. sem conexáo ou servidor off...
					AlertDialog.Builder mensagem = new AlertDialog.Builder(MainActivity.main);
					mensagem.setTitle(MainActivity.main.getString(R.string.title_erro_client_details));
					mensagem.setMessage(MainActivity.main.getString(R.string.msg_erro_client_details));
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
	

	private class ProcessaCheckouts extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Void... params) {
			BufferedReader br = (BufferedReader) obj;
			checkoutList = new ArrayList<Checkout>();
			int list = Integer.parseInt(total_checkout);
			for (int i = 0; i < list; i++) {

				Client client = new Client();
				Checkout checkout = new Checkout();
				Payment payment = new Payment();

				try {
					
					System.out.println("============================== CHEKOUT ==============================");

					String id = br.readLine();
					client.setClientId(Integer.parseInt(id));
					client.setFacebookId(br.readLine());
					client.setEmail(br.readLine());
					client.setNascimento(br.readLine().toString());
					client.setName(br.readLine());
					client.setExperienciaId(br.readLine());


					Log.d(MainActivity.class.toString(), client.toString());
					// TODO INFORMACOES DO PAGAMENTO
					String paymentId = br.readLine();					
					String paykey = br.readLine();
					String transactionId = br.readLine();
					String acquirer = br.readLine();
					String amount = br.readLine();
					String customerreceipt = br.readLine();
					String authorizerid = br.readLine();
					String authorizationnumber = br.readLine();
					String checkoutDate = br.readLine();

					payment.setPaymentId(paymentId);
					payment.setPaykey(paykey);
					payment.setTransactionId(transactionId);
					payment.setAcquirer(acquirer);
					payment.setAmount(amount);
					payment.setCustomerreceipt(customerreceipt);
					payment.setAuthorizerid(authorizerid);
					payment.setAuthorizationnumber(authorizationnumber);
					
					client.setPayId(paymentId);
					client.setTotalVisitas(br.readLine());
					client.setTotalGasto(br.readLine());
					client.setMesa(br.readLine());

					checkout.setClient(client);
					checkout.setPayment(payment);
					checkout.setDate(checkoutDate);

					checkoutList.add(checkout);

				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				if(cont_checkout==1){
					last_checkoutList = checkoutList;
				}
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Collections.sort(checkoutList, new Comparator<Checkout>() {
				@Override
				public int compare(Checkout c1, Checkout c2) {
					return c1.getDate().compareTo(c2.getDate());
				}
			});
						
			pDialog.dismiss();
			
			if(position==0 && cont_checkin != Integer.parseInt(total_checkin)){
				System.out.println("$$$$$$$$$$$$$$$$$$$$$$$ ENTROU NO SHOW CHECKIN: aaa "+getSupportActionBar().getSelectedNavigationIndex());
				cont_checkin = Integer.parseInt(total_checkin);
				first_acess = 1;
				showCheckins();
			}else if(cont_checkout != Integer.parseInt(total_checkout)){
				cont_checkout = Integer.parseInt(total_checkout);
								
				if(getSupportActionBar().getSelectedNavigationIndex()!=0){
					System.out.println("######################### ENTROU NO SHOW CHECKOUT: bbb "+getSupportActionBar().getSelectedNavigationIndex());
					showCheckouts();	
				}					
			}			
		}
	}

	Handler checkoutHandler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
				case HttpConnection.DID_START: {
					Log.d(CheckinFragment.class.toString(), "Starting connection...");
					break;
				}
				case HttpConnection.DID_SUCCEED: {
					obj = message.obj;

					try {	
						result = ((BufferedReader) obj).readLine();
						total_checkout = ((BufferedReader) obj).readLine();					
						if(total_checkout != null){
							if (total_checkout.length() < 2)
								total_checkout = "0" + total_checkout;
							
							checkoutTab.setText(getString(R.string.checkouts) + " (" + total_checkout + ")");
						}else{
							checkoutTab.setText(getString(R.string.checkouts) + " (00)");
						}
			
					} catch (IOException e) {
						e.printStackTrace();
					}

					if ("OK".equalsIgnoreCase(result) && !"0".equalsIgnoreCase(total_checkout))
						new ProcessaCheckouts().execute();
					if(!close){
						System.out.println("ENTROU NA THREAD!!!");
						startTimer(2000);
					}
					
					break;
				}
				case HttpConnection.DID_ERROR: {
					pDialog.dismiss();
					AlertDialog.Builder mensagem = new AlertDialog.Builder(MainActivity.main);
					mensagem.setTitle(MainActivity.main.getString(R.string.title_erro_client_details));
					mensagem.setMessage(MainActivity.main.getString(R.string.msg_erro_client_details));
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

		
	public void startTimer(final int timeMiliSec) {
		new Thread() {
			@Override
			public void run() {
				try {
					int logoTimer=0;
					while (logoTimer<timeMiliSec) {
						sleep(100);
						logoTimer+=100;
					}
					bms.listCheckins(Constants.ESTABELECIMENTO_ID, checkinHandler);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}
